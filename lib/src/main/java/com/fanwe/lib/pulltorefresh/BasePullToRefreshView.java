/*
 * Copyright (C) 2017 zhengjun, fanwe (http://www.fanwe.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fanwe.lib.pulltorefresh;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.fanwe.lib.pulltorefresh.loadingview.LoadingView;
import com.fanwe.lib.pulltorefresh.loadingview.SimpleTextLoadingView;

import java.lang.reflect.Constructor;

public abstract class BasePullToRefreshView extends ViewGroup implements PullToRefreshView
{
    public BasePullToRefreshView(Context context)
    {
        super(context);
        init(null);
    }

    public BasePullToRefreshView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public BasePullToRefreshView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private LoadingView mHeaderView;
    private LoadingView mFooterView;
    private View mRefreshView;

    private Mode mMode = Mode.PULL_BOTH;
    private State mState = State.RESET;
    private Direction mDirection = Direction.NONE;
    /**
     * HeaderView和FooterView是否是覆盖的模式
     */
    private boolean mIsOverLayMode = false;
    /**
     * 拖动的时候要消耗的拖动距离比例
     */
    private float mComsumeScrollPercent = DEFAULT_COMSUME_SCROLL_PERCENT;
    /**
     * 显示刷新结果的时长
     */
    private int mDurationShowRefreshResult = DEFAULT_DURATION_SHOW_REFRESH_RESULT;

    private OnRefreshCallback mOnRefreshCallback;
    private OnStateChangedCallback mOnStateChangedCallback;
    private OnViewPositionChangedCallback mOnViewPositionChangedCallback;
    private PullCondition mPullCondition;

    protected boolean mIsDebug;

    private void init(AttributeSet attrs)
    {
        addLoadingViews();
    }

    public final void setDebug(boolean debug)
    {
        mIsDebug = debug;
    }

    protected final String getDebugTag()
    {
        return getClass().getSimpleName();
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams()
    {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    //----------PullToRefreshView implements start----------

    @Override
    public void setMode(Mode mode)
    {
        if (mode == null)
        {
            throw new NullPointerException("mode is null");
        }

        if (mMode != mode)
        {
            mMode = mode;
        }
    }

    @Override
    public void setOnRefreshCallback(OnRefreshCallback onRefreshCallback)
    {
        mOnRefreshCallback = onRefreshCallback;
    }

    @Override
    public void setOnStateChangedCallback(OnStateChangedCallback onStateChangedCallback)
    {
        mOnStateChangedCallback = onStateChangedCallback;
    }

    @Override
    public void setOnViewPositionChangedCallback(OnViewPositionChangedCallback onViewPositionChangedCallback)
    {
        mOnViewPositionChangedCallback = onViewPositionChangedCallback;
    }

    @Override
    public void setPullCondition(PullCondition pullCondition)
    {
        mPullCondition = pullCondition;
    }

    @Override
    public void setOverLayMode(boolean overLayMode)
    {
        if (mState == State.RESET)
        {
            mIsOverLayMode = overLayMode;
        }
    }

    @Override
    public boolean isOverLayMode()
    {
        return mIsOverLayMode;
    }

    @Override
    public void setComsumeScrollPercent(float percent)
    {
        if (percent >= 0 && percent <= 1)
        {
            mComsumeScrollPercent = percent;
        } else
        {
            throw new IllegalArgumentException("percent >= 0 && percent <= 1 required");
        }
    }

    @Override
    public void setDurationShowRefreshResult(int duration)
    {
        if (duration >= 0)
        {
            mDurationShowRefreshResult = duration;
        } else
        {
            throw new IllegalArgumentException("duration >= 0 required");
        }
    }

    @Override
    public void startRefreshingFromHeader()
    {
        if (mState == State.RESET)
        {
            setDirection(Direction.FROM_HEADER);
            setState(State.REFRESHING);
            flingViewByState();
        }
    }

    @Override
    public void startRefreshingFromFooter()
    {
        if (mState == State.RESET)
        {
            setDirection(Direction.FROM_FOOTER);
            setState(State.REFRESHING);
            flingViewByState();
        }
    }

    @Override
    public void stopRefreshing()
    {
        if (mState == State.REFRESHING
                || mState == State.REFRESHING_SUCCESS
                || mState == State.REFRESHING_FAILURE)
        {
            setState(State.FINISH);
            flingViewByState();
        }
    }

    @Override
    public void stopRefreshingWithResult(boolean success)
    {
        if (mState == State.REFRESHING)
        {
            if (success)
            {
                setState(State.REFRESHING_SUCCESS);
            } else
            {
                setState(State.REFRESHING_FAILURE);
            }
        }
    }

    @Override
    public boolean isRefreshing()
    {
        return mState == State.REFRESHING;
    }

    @Override
    public State getState()
    {
        return mState;
    }

    @Override
    public Mode getMode()
    {
        return mMode;
    }

    @Override
    public LoadingView getHeaderView()
    {
        return mHeaderView;
    }

    @Override
    public void setHeaderView(LoadingView headerView)
    {
        if (headerView == null || headerView == mHeaderView)
        {
            return;
        }
        if (!(headerView instanceof View))
        {
            throw new IllegalArgumentException("headerView should be instance of " + View.class);
        }

        removeView((View) mHeaderView);
        addView((View) headerView);
        mHeaderView = headerView;
    }

    @Override
    public LoadingView getFooterView()
    {
        return mFooterView;
    }

    @Override
    public void setFooterView(LoadingView footerView)
    {
        if (footerView == null || footerView == mFooterView)
        {
            return;
        }
        if (!(footerView instanceof View))
        {
            throw new IllegalArgumentException("footerView should be instance of " + View.class);
        }

        removeView((View) mFooterView);
        addView((View) footerView);
        mFooterView = footerView;
    }

    @Override
    public View getRefreshView()
    {
        return mRefreshView;
    }

    @Override
    public Direction getDirection()
    {
        return mDirection;
    }

    @Override
    public int getScrollDistance()
    {
        final LoadingView loadingView = getLoadingViewByDirection();
        if (loadingView == null)
        {
            return 0;
        }

        final int topReset = getTopLoadingViewReset(loadingView);
        return ((View) loadingView).getTop() - topReset;
    }

    //----------PullToRefreshView implements end----------

    /**
     * view是否处于静止，并且未拖动状态
     *
     * @return
     */
    protected abstract boolean isViewIdle();

    protected abstract void flingViewByState();

    /**
     * 检查{@link PullCondition}是否可以从头部刷新
     *
     * @return
     */
    protected final boolean checkPullConditionHeader()
    {
        return mPullCondition != null ? mPullCondition.canPullFromHeader() : true;
    }

    /**
     * 检查{@link PullCondition}是否可以从尾部刷新
     *
     * @return
     */
    protected final boolean checkPullConditionFooter()
    {
        return mPullCondition != null ? mPullCondition.canPullFromFooter() : true;
    }

    /**
     * 返回当前拖动方向对应的加载view
     *
     * @return
     */
    protected final LoadingView getLoadingViewByDirection()
    {
        if (mDirection == Direction.FROM_HEADER)
        {
            return mHeaderView;
        } else if (mDirection == Direction.FROM_FOOTER)
        {
            return mFooterView;
        } else
        {
            return null;
        }
    }

    private void checkDirection()
    {
        if (mDirection == Direction.NONE)
        {
            throw new RuntimeException("Direction is not specified before this");
        }
    }

    /**
     * 移动view
     *
     * @param dy
     * @param isDrag true-手指拖动，false-惯性滑动
     */
    protected final void moveViews(int dy, boolean isDrag)
    {
        if (dy == 0) return;

        final LoadingView loadingView = getLoadingViewByDirection();

        // HeaderView or FooterView
        Utils.offsetTopAndBottom((View) loadingView, dy);
        loadingView.onViewPositionChanged(this);

        // RefreshView
        if (mIsOverLayMode)
        {
            if (Utils.getZ((View) loadingView) <= Utils.getZ(mRefreshView))
            {
                Utils.setZ((View) loadingView, Utils.getZ(mRefreshView) + 1);
            }
        } else
        {
            Utils.offsetTopAndBottom(mRefreshView, dy);
        }

        if (mOnViewPositionChangedCallback != null)
        {
            mOnViewPositionChangedCallback.onViewPositionChanged(this);
        }

        if (isDrag)
        {
            updateStateByMoveDistance();
        }
    }

    /**
     * 根据移动距离刷新当前状态
     */
    private void updateStateByMoveDistance()
    {
        final int distance = Math.abs(getScrollDistance());
        final LoadingView loadingView = getLoadingViewByDirection();

        if (loadingView.canRefresh(distance))
        {
            setState(State.RELEASE_TO_REFRESH);
        } else
        {
            setState(State.PULL_TO_REFRESH);
        }
    }

    /**
     * 设置状态
     *
     * @param state
     */
    protected final void setState(State state)
    {
        if (mState == state)
        {
            return;
        }

        checkDirection();

        final State oldState = mState;
        mState = state;

        if (mIsDebug)
        {
            Log.i(getDebugTag(), "setState:" + mState);
        }

        removeCallbacks(mStopRefreshingRunnable);
        if (mState == State.REFRESHING_SUCCESS || mState == State.REFRESHING_FAILURE)
        {
            postDelayed(mStopRefreshingRunnable, mDurationShowRefreshResult);
        }

        //通知view改变状态
        final LoadingView loadingView = getLoadingViewByDirection();
        loadingView.onStateChanged(mState, oldState, this);

        //通知状态变化回调
        if (mOnStateChangedCallback != null)
        {
            mOnStateChangedCallback.onStateChanged(mState, oldState, this);
        }

        if (mState == State.RESET)
        {
            requestLayoutWhenReset();
            setDirection(Direction.NONE);
        }
    }

    protected final void notifyRefreshCallback()
    {
        checkDirection();

        if (mIsDebug)
        {
            Log.i(getDebugTag(), "notifyRefreshCallback");
        }

        if (mOnRefreshCallback != null)
        {
            if (mDirection == Direction.FROM_HEADER)
            {
                mOnRefreshCallback.onRefreshingFromHeader(this);
            } else if (mDirection == Direction.FROM_FOOTER)
            {
                mOnRefreshCallback.onRefreshingFromFooter(this);
            }
        }
    }

    private void requestLayoutWhenReset()
    {
        final LoadingView loadingView = getLoadingViewByDirection();
        if (((View) loadingView).getTop() != getTopLoadingViewReset(loadingView))
        {
            if (mIsDebug)
            {
                Log.e(getDebugTag(), "requestLayout when reset");
            }
            requestLayout();
        }
    }

    private final Runnable mStopRefreshingRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            stopRefreshing();
        }
    };

    /**
     * 设置拖动方向
     *
     * @param direction
     */
    protected final void setDirection(Direction direction)
    {
        if (mDirection == direction)
        {
            return;
        }
        if (direction != Direction.NONE)
        {
            if (mDirection == Direction.NONE)
            {
                mDirection = direction;
                if (mIsDebug)
                {
                    Log.i(getDebugTag(), "setDirection:" + mDirection);
                }
            }
        } else
        {
            mDirection = Direction.NONE;

            if (mIsDebug)
            {
                Log.i(getDebugTag(), "setDirection:" + mDirection);
            }
        }
    }

    /**
     * 真实距离根据消耗比例算出的最终距离
     *
     * @param distance
     * @return
     */
    protected final int getComsumedDistance(float distance)
    {
        distance -= distance * mComsumeScrollPercent;
        return (int) distance;
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        final int childCount = getChildCount();
        if (childCount < 3)
        {
            throw new IllegalArgumentException("you must add one child to SDPullToRefreshView in your xml file");
        }
        if (childCount > 3)
        {
            throw new IllegalArgumentException("you can only add one child to SDPullToRefreshView in your xml file");
        }

        mRefreshView = getChildAt(2);
    }

    private void addLoadingViews()
    {
        // HeaderView
        LoadingView headerView = onCreateHeaderView();
        if (headerView == null)
        {
            final String headerClassName = getResources().getString(R.string.lib_ptr_header_class);
            headerView = createLoadingViewByClassName(headerClassName);
        }
        if (headerView == null)
        {
            headerView = new SimpleTextLoadingView(getContext());
        }
        setHeaderView(headerView);

        // FooterView
        LoadingView footerView = onCreateFooterView();
        if (footerView == null)
        {
            final String footerClassName = getResources().getString(R.string.lib_ptr_footer_class);
            footerView = createLoadingViewByClassName(footerClassName);
        }
        if (footerView == null)
        {
            footerView = new SimpleTextLoadingView(getContext());
        }
        setFooterView(footerView);
    }

    private LoadingView createLoadingViewByClassName(String className)
    {
        try
        {
            Class clazz = Class.forName(className);
            Constructor constructor = clazz.getConstructor(Context.class);
            return (LoadingView) constructor.newInstance(getContext());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 可以重写返回HeaderView
     *
     * @return
     */
    protected LoadingView onCreateHeaderView()
    {
        return null;
    }

    /**
     * 可以重写返回FooterView
     *
     * @return
     */
    protected LoadingView onCreateFooterView()
    {
        return null;
    }

    private int getMinWidthInternal()
    {
        if (Build.VERSION.SDK_INT >= 16)
        {
            return getMinimumWidth();
        } else
        {
            return 0;
        }
    }

    private int getMinHeightInternal()
    {
        if (Build.VERSION.SDK_INT >= 16)
        {
            return getMinimumHeight();
        } else
        {
            return 0;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        measureChild((View) mHeaderView, widthMeasureSpec, heightMeasureSpec);
        measureChild((View) mFooterView, widthMeasureSpec, heightMeasureSpec);
        measureChild(mRefreshView, widthMeasureSpec, heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY)
        {
            int maxWidth = Math.max(((View) mHeaderView).getMeasuredWidth(), ((View) mFooterView).getMeasuredWidth());
            maxWidth = Math.max(maxWidth, mRefreshView.getMeasuredWidth());
            maxWidth += (getPaddingLeft() + getPaddingRight());

            maxWidth = Math.max(maxWidth, getMinWidthInternal());
            if (widthMode == MeasureSpec.UNSPECIFIED)
            {
                width = maxWidth;
            } else if (widthMode == MeasureSpec.AT_MOST)
            {
                width = Math.min(maxWidth, width);
            }
        }

        if (heightMode != MeasureSpec.EXACTLY)
        {
            int maxHeight = mRefreshView.getMeasuredHeight();
            if (maxHeight == 0)
            {
                //如果刷新view的高度为0，则给当前view一个默认高度，否则会出现代码触发刷新的时候HeaderView或者FooterView看不见
                maxHeight = Math.max(((View) mHeaderView).getMeasuredHeight(), ((View) mFooterView).getMeasuredHeight());
            }
            maxHeight += (getPaddingTop() + getPaddingBottom());

            maxHeight = Math.max(maxHeight, getMinHeightInternal());
            if (heightMode == MeasureSpec.UNSPECIFIED)
            {
                height = maxHeight;
            } else if (heightMode == MeasureSpec.AT_MOST)
            {
                height = Math.min(maxHeight, height);
            }
        }

        setMeasuredDimension(width, height);
    }

    /**
     * 返回与当前view顶部对齐的值
     *
     * @return
     */
    private int getTopAlignTop()
    {
        return getPaddingTop();
    }

    /**
     * 返回与当前view底部对齐的值
     *
     * @return
     */
    private int getTopAlignBottom()
    {
        return getHeight() - getPaddingBottom();
    }

    /**
     * 返回loadingView在{@link State#RESET}状态下的top值
     *
     * @param loadingView
     * @return
     */
    protected final int getTopLoadingViewReset(LoadingView loadingView)
    {
        if (loadingView == mHeaderView)
        {
            return getTopAlignTop() - ((View) mHeaderView).getMeasuredHeight();
        } else if (loadingView == mFooterView)
        {
            return getTopAlignBottom();
        } else
        {
            throw new IllegalArgumentException("Illegal loadingView:" + loadingView);
        }
    }

    /**
     * 返回loadingView在{@link State#REFRESHING}状态下的top值
     *
     * @param loadingView
     * @return
     */
    protected final int getTopLoadingViewRefreshing(LoadingView loadingView)
    {
        final int reset = getTopLoadingViewReset(loadingView);

        if (loadingView == mHeaderView)
        {
            return reset + mHeaderView.getRefreshingHeight();
        } else if (loadingView == mFooterView)
        {
            return reset - mFooterView.getRefreshingHeight();
        } else
        {
            throw new IllegalArgumentException("Illegal loadingView:" + loadingView);
        }
    }

    private int getTopLayoutHeaderView()
    {
        // 初始值
        int top = getTopLoadingViewReset(mHeaderView);

        if (mDirection == Direction.FROM_HEADER)
        {
            if (isViewIdle())
            {
                switch (mState)
                {
                    case REFRESHING:
                    case REFRESHING_SUCCESS:
                    case REFRESHING_FAILURE:
                        top += mHeaderView.getRefreshingHeight();
                        break;
                }
            } else
            {
                top = ((View) mHeaderView).getTop();
            }
        }
        return top;
    }

    private int getTopLayoutFooterView()
    {
        // 初始值
        int top = getTopLoadingViewReset(mFooterView);

        if (mDirection == Direction.FROM_FOOTER)
        {
            if (isViewIdle())
            {
                switch (mState)
                {
                    case REFRESHING:
                    case REFRESHING_SUCCESS:
                    case REFRESHING_FAILURE:
                        top -= mFooterView.getRefreshingHeight();
                        break;
                }
            } else
            {
                top = ((View) mFooterView).getTop();
            }
        }
        return top;
    }

    private int getTopLayoutRefreshView()
    {
        // 初始值
        int top = getTopAlignTop();

        if (mIsOverLayMode)
        {
        } else
        {
            if (isViewIdle())
            {
                switch (mState)
                {
                    case REFRESHING:
                    case REFRESHING_SUCCESS:
                    case REFRESHING_FAILURE:
                        if (mDirection == Direction.FROM_HEADER)
                        {
                            top += mHeaderView.getRefreshingHeight();
                        } else if (mDirection == Direction.FROM_FOOTER)
                        {
                            top -= mFooterView.getRefreshingHeight();
                        }
                        break;
                }
            } else
            {
                top = mRefreshView.getTop();
            }
        }
        return top;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        String logString = "";

        if (mIsDebug)
        {
            logString += "----------onLayout height:" + getHeight() + " " + mState + "\r\n";
        }

        int left = getPaddingLeft();
        int top = 0;
        int right = 0;
        int bottom = 0;

        // HeaderView
        top = getTopLayoutHeaderView();
        right = left + ((View) mHeaderView).getMeasuredWidth();
        bottom = top + ((View) mHeaderView).getMeasuredHeight();
        ((View) mHeaderView).layout(left, top, right, bottom);
        if (mIsDebug)
        {
            logString += "HeaderView:" + top + "," + bottom + " -> " + (bottom - top) + "\r\n";
        }

        // RefreshView
        top = getTopLayoutRefreshView();
        if (!mIsOverLayMode
                && mDirection == Direction.FROM_HEADER && bottom > top)
        {
            top = bottom;
        }
        right = left + mRefreshView.getMeasuredWidth();
        bottom = top + mRefreshView.getMeasuredHeight();
        mRefreshView.layout(left, top, right, bottom);
        if (mIsDebug)
        {
            logString += "RefreshView:" + top + "," + bottom + " -> " + (bottom - top) + "\r\n";
        }

        // FooterView
        top = getTopLayoutFooterView();
        if (!mIsOverLayMode
                && bottom <= getTopAlignBottom() && bottom > top)
        {
            top = bottom;
        }
        right = left + ((View) mFooterView).getMeasuredWidth();
        bottom = top + ((View) mFooterView).getMeasuredHeight();
        ((View) mFooterView).layout(left, top, right, bottom);
        if (mIsDebug)
        {
            logString += "FooterView:" + top + "," + bottom + " -> " + (bottom - top);
            Log.i(getDebugTag(), logString);
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        removeCallbacks(mStopRefreshingRunnable);
    }
}
