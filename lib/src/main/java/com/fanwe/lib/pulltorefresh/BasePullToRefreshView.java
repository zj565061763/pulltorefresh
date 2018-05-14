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
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.fanwe.lib.pulltorefresh.loadingview.BasePullToRefreshLoadingView;
import com.fanwe.lib.pulltorefresh.loadingview.SimpleTextLoadingView;

public abstract class BasePullToRefreshView extends ViewGroup implements FIPullToRefreshView
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

    private BasePullToRefreshLoadingView mHeaderView;
    private BasePullToRefreshLoadingView mFooterView;
    private View mRefreshView;

    private Mode mMode = Mode.BOTH;
    private State mState = State.RESET;
    private Direction mDirection = Direction.NONE;
    private Direction mLastDirection = Direction.NONE;
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
    protected IPullCondition mPullCondition;

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

    //----------FIPullToRefreshView implements start----------

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
    public void setPullCondition(IPullCondition pullCondition)
    {
        mPullCondition = pullCondition;
    }

    @Override
    public void setOverLayMode(boolean overLayMode)
    {
        if (isViewIdle() && mState == State.RESET)
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
        if (mMode == Mode.DISABLE)
        {
            return;
        }

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
        if (mMode == Mode.DISABLE)
        {
            return;
        }

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
        if (mState == State.RESET || mState == State.FINISH)
        {
            return;
        }

        setState(State.FINISH);
        flingViewByState();
    }

    @Override
    public void stopRefreshingWithResult(boolean success)
    {
        if (mState == State.REFRESHING)
        {
            if (success)
            {
                setState(State.REFRESH_SUCCESS);
            } else
            {
                setState(State.REFRESH_FAILURE);
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
    public BasePullToRefreshLoadingView getHeaderView()
    {
        return mHeaderView;
    }

    @Override
    public void setHeaderView(BasePullToRefreshLoadingView headerView)
    {
        if (headerView == null || headerView == mHeaderView)
        {
            return;
        }

        removeView(mHeaderView);
        mHeaderView = headerView;
        addView(headerView);
    }

    @Override
    public BasePullToRefreshLoadingView getFooterView()
    {
        return mFooterView;
    }

    @Override
    public void setFooterView(BasePullToRefreshLoadingView footerView)
    {
        if (footerView == null || footerView == mFooterView)
        {
            return;
        }

        removeView(mFooterView);
        mFooterView = footerView;
        addView(footerView);
    }

    @Override
    public View getRefreshView()
    {
        return mRefreshView;
    }

    @Override
    public Direction getDirection()
    {
        return mLastDirection;
    }

    @Override
    public int getScrollDistance()
    {
        if (getDirection() == Direction.FROM_HEADER)
        {
            return mHeaderView.getTop() - getTopHeaderViewReset();
        } else
        {
            return mFooterView.getTop() - getTopFooterViewReset();
        }
    }

    //----------FIPullToRefreshView implements end----------

    /**
     * view是否处于静止的状态
     *
     * @return
     */
    protected abstract boolean isViewIdle();

    protected abstract void flingViewByState();

    /**
     * 移动view
     *
     * @param dy
     * @param moveHeaderOrFooter
     */
    protected final void moveViews(int dy, boolean moveHeaderOrFooter)
    {
        if (getDirection() == Direction.FROM_HEADER)
        {
            if (moveHeaderOrFooter) ViewCompat.offsetTopAndBottom(mHeaderView, dy);

            if (mIsOverLayMode)
            {
                if (ViewCompat.getZ(mHeaderView) <= ViewCompat.getZ(mRefreshView))
                {
                    ViewCompat.setZ(mHeaderView, ViewCompat.getZ(mRefreshView) + 1);
                }
            } else
            {
                ViewCompat.offsetTopAndBottom(mRefreshView, dy);
            }
            mHeaderView.onViewPositionChanged(this);
        } else
        {
            if (moveHeaderOrFooter) ViewCompat.offsetTopAndBottom(mFooterView, dy);

            if (mIsOverLayMode)
            {
                if (ViewCompat.getZ(mFooterView) <= ViewCompat.getZ(mRefreshView))
                {
                    ViewCompat.setZ(mFooterView, ViewCompat.getZ(mRefreshView) + 1);
                }
            } else
            {
                ViewCompat.offsetTopAndBottom(mRefreshView, dy);
            }
            mFooterView.onViewPositionChanged(this);
        }

        if (mOnViewPositionChangedCallback != null)
        {
            mOnViewPositionChangedCallback.onViewPositionChanged(this);
        }
    }

    /**
     * 根据移动距离刷新当前状态
     */
    protected final void updateStateByMoveDistance()
    {
        final int distance = Math.abs(getScrollDistance());
        if (getDirection() == Direction.FROM_HEADER)
        {
            if (mHeaderView.canRefresh(distance))
            {
                setState(State.RELEASE_TO_REFRESH);
            } else
            {
                setState(State.PULL_TO_REFRESH);
            }
        } else
        {
            if (mFooterView.canRefresh(distance))
            {
                setState(State.RELEASE_TO_REFRESH);
            } else
            {
                setState(State.PULL_TO_REFRESH);
            }
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

        final State oldState = mState;
        mState = state;

        if (mIsDebug)
        {
            Log.i(getDebugTag(), "setState:" + mState);
        }

        removeCallbacks(mStopRefreshingRunnable);
        if (mState == State.REFRESH_SUCCESS || mState == State.REFRESH_FAILURE)
        {
            postDelayed(mStopRefreshingRunnable, mDurationShowRefreshResult);
        }

        //通知view改变状态
        if (getDirection() == Direction.FROM_HEADER)
        {
            mHeaderView.onStateChanged(mState, oldState, this);
        } else
        {
            mFooterView.onStateChanged(mState, oldState, this);
        }

        //通知状态变化回调
        if (mOnStateChangedCallback != null)
        {
            mOnStateChangedCallback.onStateChanged(mState, oldState, this);
        }

        if (mState == State.RESET)
        {
            requestLayoutIfNeed();
            setDirection(Direction.NONE);
        }
    }

    protected final void notifyRefreshCallback()
    {
        if (mIsDebug)
        {
            Log.i(getDebugTag(), "notifyRefreshCallback");
        }

        if (mOnRefreshCallback != null)
        {
            if (getDirection() == Direction.FROM_HEADER)
            {
                mOnRefreshCallback.onRefreshingFromHeader(this);
            } else
            {
                mOnRefreshCallback.onRefreshingFromFooter(this);
            }
        }
    }

    private void requestLayoutIfNeed()
    {
        boolean needRequestLayout = false;
        if (getDirection() == Direction.FROM_HEADER)
        {
            if (mHeaderView.getTop() != getTopHeaderViewReset())
            {
                needRequestLayout = true;
            }
        } else if (getDirection() == Direction.FROM_FOOTER)
        {
            if (mFooterView.getTop() != getTopFooterViewReset())
            {
                needRequestLayout = true;
            }
        }
        if (needRequestLayout)
        {
            if (mIsDebug)
            {
                Log.i(getDebugTag(), "requestLayout when reset");
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
                mLastDirection = direction;

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
        BasePullToRefreshLoadingView headerView = onCreateHeaderView();
        if (headerView == null)
        {
            String headerClassName = getResources().getString(R.string.lib_ptr_header_class);
            if (!TextUtils.isEmpty(headerClassName))
            {
                headerView = BasePullToRefreshLoadingView.getInstanceByClassName(headerClassName, getContext());
            }
        }
        if (headerView == null)
        {
            headerView = new SimpleTextLoadingView(getContext());
        }
        setHeaderView(headerView);

        // FooterView
        BasePullToRefreshLoadingView footerView = onCreateFooterView();
        if (footerView == null)
        {
            String footerClassName = getResources().getString(R.string.lib_ptr_footer_class);
            if (footerClassName != null)
            {
                footerView = BasePullToRefreshLoadingView.getInstanceByClassName(footerClassName, getContext());
            }
        }
        if (footerView == null)
        {
            footerView = new SimpleTextLoadingView(getContext());
        }
        setFooterView(footerView);
    }

    /**
     * 可以重写返回HeaderView
     *
     * @return
     */
    protected BasePullToRefreshLoadingView onCreateHeaderView()
    {
        return null;
    }

    /**
     * 可以重写返回FooterView
     *
     * @return
     */
    protected BasePullToRefreshLoadingView onCreateFooterView()
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

        measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
        measureChild(mFooterView, widthMeasureSpec, heightMeasureSpec);
        measureChild(mRefreshView, widthMeasureSpec, heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY)
        {
            int maxWidth = Math.max(mHeaderView.getMeasuredWidth(), mFooterView.getMeasuredWidth());
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
                maxHeight = Math.max(mHeaderView.getMeasuredHeight(), mFooterView.getMeasuredHeight());
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
     * 返回HeaderView的Reset静止状态下top值
     *
     * @return
     */
    protected final int getTopHeaderViewReset()
    {
        return getTopAlignTop() - mHeaderView.getMeasuredHeight();
    }

    /**
     * 返回FooterView的Reset静止状态下top值
     *
     * @return
     */
    protected final int getTopFooterViewReset()
    {
        return getTopAlignBottom();
    }

    private int getTopLayoutHeaderView()
    {
        // 初始值
        int top = getTopHeaderViewReset();

        if (getDirection() == Direction.FROM_HEADER)
        {
            if (isViewIdle())
            {
                switch (mState)
                {
                    case REFRESHING:
                    case REFRESH_SUCCESS:
                    case REFRESH_FAILURE:
                        top += mHeaderView.getRefreshHeight();
                        break;
                }
            } else
            {
                top = mHeaderView.getTop();
            }
        }
        return top;
    }

    private int getTopLayoutFooterView()
    {
        // 初始值
        int top = getTopFooterViewReset();

        if (getDirection() == Direction.FROM_FOOTER)
        {
            if (isViewIdle())
            {
                switch (mState)
                {
                    case REFRESHING:
                    case REFRESH_SUCCESS:
                    case REFRESH_FAILURE:
                        top -= mFooterView.getRefreshHeight();
                        break;
                }
            } else
            {
                top = mFooterView.getTop();
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
                    case REFRESH_SUCCESS:
                    case REFRESH_FAILURE:
                        if (getDirection() == Direction.FROM_HEADER)
                        {
                            top += mHeaderView.getRefreshHeight();
                        } else if (getDirection() == Direction.FROM_FOOTER)
                        {
                            top -= mFooterView.getRefreshHeight();
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
        if (mIsDebug)
        {
            Log.i(getDebugTag(), "onLayout " + mState + " totalHeight:----------" + getHeight());
        }

        int left = getPaddingLeft();
        int top = 0;
        int right = 0;
        int bottom = 0;

        // HeaderView
        top = getTopLayoutHeaderView();
        right = left + mHeaderView.getMeasuredWidth();
        bottom = top + mHeaderView.getMeasuredHeight();
        mHeaderView.layout(left, top, right, bottom);
        if (mIsDebug)
        {
            Log.i(getDebugTag(), "HeaderView:" + top + "," + bottom);
        }

        // RefreshView
        top = getTopLayoutRefreshView();
        if (!mIsOverLayMode
                && getDirection() == Direction.FROM_HEADER && bottom > top)
        {
            top = bottom;
        }
        right = left + mRefreshView.getMeasuredWidth();
        bottom = top + mRefreshView.getMeasuredHeight();
        mRefreshView.layout(left, top, right, bottom);
        if (mIsDebug)
        {
            Log.i(getDebugTag(), "RefreshView:" + top + "," + bottom);
        }

        // FooterView
        top = getTopLayoutFooterView();
        if (!mIsOverLayMode
                && bottom <= getTopAlignBottom() && bottom > top)
        {
            top = bottom;
        }
        right = left + mFooterView.getMeasuredWidth();
        bottom = top + mFooterView.getMeasuredHeight();
        mFooterView.layout(left, top, right, bottom);
        if (mIsDebug)
        {
            Log.i(getDebugTag(), "FooterView:" + top + "," + bottom);
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        removeCallbacks(mStopRefreshingRunnable);
    }
}
