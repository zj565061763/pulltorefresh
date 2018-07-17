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
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.fanwe.lib.gesture.FTouchHelper;
import com.fanwe.lib.pulltorefresh.loadingview.LoadingView;
import com.fanwe.lib.pulltorefresh.loadingview.SimpleTextLoadingView;

import java.lang.reflect.Constructor;

public abstract class BasePullToRefreshView extends ViewGroup implements PullToRefreshView
{
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
    private OnStateChangeCallback mOnStateChangeCallback;
    private OnViewPositionChangeCallback mOnViewPositionChangeCallback;
    private PullCondition mPullCondition;

    protected boolean mIsDebug;
    private boolean mIsDebugLayout;

    public BasePullToRefreshView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        addLoadingViews();
    }

    /**
     * 设置是否打印日志
     *
     * @param debug
     */
    public final void setDebug(boolean debug)
    {
        mIsDebug = debug;
    }

    /**
     * 设置是否打印{@link #onLayout(boolean, int, int, int, int)}日志
     *
     * @param debugLayout
     */
    public final void setDebugLayout(boolean debugLayout)
    {
        mIsDebugLayout = debugLayout;
    }

    protected final String getDebugTag()
    {
        return getClass().getSimpleName();
    }

    @Override
    protected final LayoutParams generateDefaultLayoutParams()
    {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    //----------PullToRefreshView implements start----------

    @Override
    public void setMode(Mode mode)
    {
        if (mode == null)
            throw new NullPointerException("mode is null");

        mMode = mode;
    }

    @Override
    public void setOnRefreshCallback(OnRefreshCallback onRefreshCallback)
    {
        mOnRefreshCallback = onRefreshCallback;
    }

    @Override
    public void setOnStateChangeCallback(OnStateChangeCallback onStateChangeCallback)
    {
        mOnStateChangeCallback = onStateChangeCallback;
    }

    @Override
    public void setOnViewPositionChangeCallback(OnViewPositionChangeCallback onViewPositionChangeCallback)
    {
        mOnViewPositionChangeCallback = onViewPositionChangeCallback;
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
            mIsOverLayMode = overLayMode;
    }

    @Override
    public boolean isOverLayMode()
    {
        return mIsOverLayMode;
    }

    @Override
    public void setComsumeScrollPercent(float percent)
    {
        if (percent < 0 || percent > 1)
            throw new IllegalArgumentException("percent >= 0 && percent <= 1 required");

        mComsumeScrollPercent = percent;
    }

    @Override
    public void setDurationShowRefreshResult(int duration)
    {
        if (duration < 0)
            throw new IllegalArgumentException("duration >= 0 required");

        mDurationShowRefreshResult = duration;
    }

    @Override
    public void startRefreshingFromHeader()
    {
        if (mState == State.RESET)
        {
            setDirection(Direction.FROM_HEADER);
            setState(State.REFRESHING);
            smoothSlideViewByState();
        }
    }

    @Override
    public void startRefreshingFromFooter()
    {
        if (mState == State.RESET)
        {
            setDirection(Direction.FROM_FOOTER);
            setState(State.REFRESHING);
            smoothSlideViewByState();
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
            smoothSlideViewByState();
        }
    }

    @Override
    public void stopRefreshingWithResult(boolean success)
    {
        if (mState == State.REFRESHING)
        {
            if (success)
                setState(State.REFRESHING_SUCCESS);
            else
                setState(State.REFRESHING_FAILURE);
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
            return;

        if (!(headerView instanceof View))
            throw new IllegalArgumentException("headerView must be instance of " + View.class);

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
            return;

        if (!(footerView instanceof View))
            throw new IllegalArgumentException("footerView must be instance of " + View.class);

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
            return 0;

        final int topReset = getTopLoadingViewReset(loadingView);
        final int distance = ((View) loadingView).getTop() - topReset;
        return Math.abs(distance);
    }

    //----------PullToRefreshView implements end----------

    /**
     * view是否处于空闲状态（静止且未被拖动状态）
     *
     * @return
     */
    protected abstract boolean isViewIdle();

    /**
     * 执行滑动逻辑
     *
     * @param startY
     * @param endY
     * @return true-触发滑动
     */
    protected abstract boolean onSmoothSlide(int startY, int endY);

    /**
     * 根据当前状态滑动view到某个位置
     */
    protected final void smoothSlideViewByState()
    {
        final LoadingView loadingView = getLoadingViewByDirection();

        final int startY = ((View) loadingView).getTop();
        int endY = 0;

        switch (getState())
        {
            case RESET:
            case PULL_TO_REFRESH:
            case FINISH:
                endY = getTopLoadingViewReset(loadingView);
                break;
            case RELEASE_TO_REFRESH:
            case REFRESHING:
                endY = getTopLoadingViewRefreshing(loadingView);
                break;
        }

        if (mIsDebug)
        {
            final String headerOrFooter = loadingView == mHeaderView ? "Header" : "Footer";
            Log.i(getDebugTag(), "smoothSlideViewByState " + headerOrFooter + " " + startY + " -> " + endY + " " + getState());
        }

        final boolean slide = onSmoothSlide(startY, endY);

        if (slide)
        {
            invalidate();
        } else
        {
            dealViewIdle();
        }
    }

    /**
     * 处理view由忙碌变成空闲{@link #isViewIdle()}时候需要执行的逻辑
     */
    protected final void dealViewIdle()
    {
        if (isViewIdle())
        {
            if (mIsDebug)
                Log.i(getDebugTag(), "dealViewIdle:" + mState);

            switch (getState())
            {
                case REFRESHING:
                    requestLayoutIfNeed();
                    notifyRefreshCallback();
                    break;
                case PULL_TO_REFRESH:
                case FINISH:
                    setState(State.RESET);
                    break;
                case RESET:
                    resetIfNeed();
                    break;
            }
        } else
        {
            if (mIsDebug)
                Log.e(getDebugTag(), "try dealViewIdle when view is busy");
        }
    }

    private void notifyRefreshCallback()
    {
        if (mState != State.REFRESHING)
            throw new RuntimeException("Illegal state:" + mState);

        if (mIsDebug)
            Log.i(getDebugTag(), "notifyRefreshCallback:" + mDirection);

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

    /**
     * 检查{@link PullCondition}是否可以从Header处触发刷新
     *
     * @return
     */
    protected final boolean checkPullConditionHeader()
    {
        return mPullCondition != null ? mPullCondition.canPullFromHeader(this) : true;
    }

    /**
     * 检查{@link PullCondition}是否可以从Footer处触发刷新
     *
     * @return
     */
    protected final boolean checkPullConditionFooter()
    {
        return mPullCondition != null ? mPullCondition.canPullFromFooter(this) : true;
    }

    private void checkDirection()
    {
        if (mDirection == Direction.NONE)
            throw new RuntimeException("The direction has not been specified before this");
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

    /**
     * 移动view
     *
     * @param delta
     * @param isDrag true-手指拖动，false-惯性滑动
     * @return
     */
    protected final boolean moveViews(int delta, boolean isDrag)
    {
        if (delta == 0)
            return false;

        if (isDrag)
        {
            delta = getComsumedDistance(delta);
            if (delta == 0)
                return false;
        }

        checkDirection();

        final LoadingView loadingView = getLoadingViewByDirection();
        final int top = ((View) loadingView).getTop();
        final int topReset = getTopLoadingViewReset(loadingView);

        if (loadingView == mHeaderView)
        {
            delta = FTouchHelper.getLegalDelta(top, topReset, Integer.MAX_VALUE, delta);
        } else if (loadingView == mFooterView)
        {
            delta = FTouchHelper.getLegalDelta(top, Integer.MIN_VALUE, topReset, delta);
        }

        if (delta == 0)
            return false;

        // HeaderView or FooterView
        ViewCompat.offsetTopAndBottom((View) loadingView, delta);
        loadingView.onViewPositionChanged(this);

        // RefreshView
        if (mIsOverLayMode)
        {
            if (ViewCompat.getZ((View) loadingView) <= ViewCompat.getZ(mRefreshView))
                ViewCompat.setZ((View) loadingView, ViewCompat.getZ(mRefreshView) + 1);
        } else
        {
            ViewCompat.offsetTopAndBottom(mRefreshView, delta);
        }

        if (mOnViewPositionChangeCallback != null)
            mOnViewPositionChangeCallback.onViewPositionChanged(this);

        if (isDrag)
            updateStateByMoveDistance();

        return true;
    }

    /**
     * 根据移动距离刷新当前状态
     */
    private void updateStateByMoveDistance()
    {
        final LoadingView loadingView = getLoadingViewByDirection();
        final int scrollDistance = getScrollDistance();
        if (loadingView.canRefresh(scrollDistance))
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
            return;

        checkDirection();

        final State oldState = mState;
        mState = state;

        if (mIsDebug)
            Log.i(getDebugTag(), "setState:" + mState);

        removeCallbacks(mStopRefreshingRunnable);
        if (mState == State.REFRESHING_SUCCESS || mState == State.REFRESHING_FAILURE)
            postDelayed(mStopRefreshingRunnable, mDurationShowRefreshResult);

        //通知view改变状态
        final LoadingView loadingView = getLoadingViewByDirection();
        loadingView.onStateChanged(mState, oldState, this);

        //通知状态变化回调
        if (mOnStateChangeCallback != null)
            mOnStateChangeCallback.onStateChanged(mState, oldState, this);

        resetIfNeed();
    }

    private void resetIfNeed()
    {
        if (mState == State.RESET)
        {
            requestLayoutIfNeed();
            setDirection(Direction.NONE);
        }
    }

    /**
     * view处于空闲{@link #isViewIdle()}时候，如果位置不对，调用此方法修正位置
     */
    private void requestLayoutIfNeed()
    {
        final LoadingView loadingView = getLoadingViewByDirection();
        if (loadingView == null)
            return;

        if (isViewIdle())
        {
            boolean layout = false;
            switch (mState)
            {
                case REFRESHING:
                    if (((View) loadingView).getTop() != getTopLoadingViewRefreshing(loadingView))
                        layout = true;
                    break;
                case RESET:
                    if (((View) loadingView).getTop() != getTopLoadingViewReset(loadingView))
                        layout = true;
                    break;
            }

            if (layout)
            {
                if (mIsDebug)
                    Log.e(getDebugTag(), "requestLayout with state:" + mState);

                requestLayout();
            }
        } else
        {
            if (mIsDebug)
                Log.e(getDebugTag(), "try requestLayoutIfNeed when view is busy");
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
        if (direction == null)
            throw new NullPointerException("direction is null");
        if (mDirection == direction)
            return;

        if (direction != Direction.NONE)
        {
            if (mDirection == Direction.NONE)
            {
                mDirection = direction;
                if (mIsDebug)
                    Log.i(getDebugTag(), "setDirection:" + mDirection);
            }
        } else
        {
            mDirection = Direction.NONE;
            if (mIsDebug)
                Log.i(getDebugTag(), "setDirection:" + mDirection);
        }
    }

    /**
     * 真实距离根据消耗比例算出的最终距离
     *
     * @param distance
     * @return
     */
    private int getComsumedDistance(float distance)
    {
        distance -= distance * mComsumeScrollPercent;
        return (int) distance;
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        final int count = getChildCount();
        if (count < 3)
            throw new IllegalArgumentException("you must add one child to SDPullToRefreshView in your xml file");

        if (count > 3)
            throw new IllegalArgumentException("you can only add one child to SDPullToRefreshView in your xml file");

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

            maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
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

            maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
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
    private int getTopLoadingViewReset(LoadingView loadingView)
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
    private int getTopLoadingViewRefreshing(LoadingView loadingView)
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

        if (mIsDebugLayout)
            logString += "----------onLayout height:" + getHeight() + " " + mState + "\r\n";

        int left = getPaddingLeft();
        int top = 0;
        int right = 0;
        int bottom = 0;

        // HeaderView
        top = getTopLayoutHeaderView();
        right = left + ((View) mHeaderView).getMeasuredWidth();
        bottom = top + ((View) mHeaderView).getMeasuredHeight();
        ((View) mHeaderView).layout(left, top, right, bottom);
        if (mIsDebugLayout)
            logString += "HeaderView:" + top + "," + bottom + " -> " + (bottom - top) + "\r\n";

        // RefreshView
        top = getTopLayoutRefreshView();
        if (!mIsOverLayMode && mDirection == Direction.FROM_HEADER && bottom > top)
        {
            top = bottom;
        }
        right = left + mRefreshView.getMeasuredWidth();
        bottom = top + mRefreshView.getMeasuredHeight();
        mRefreshView.layout(left, top, right, bottom);
        if (mIsDebugLayout)
            logString += "RefreshView:" + top + "," + bottom + " -> " + (bottom - top) + "\r\n";

        // FooterView
        top = getTopLayoutFooterView();
        if (!mIsOverLayMode && bottom <= getTopAlignBottom() && bottom > top)
        {
            top = bottom;
        }
        right = left + ((View) mFooterView).getMeasuredWidth();
        bottom = top + ((View) mFooterView).getMeasuredHeight();
        ((View) mFooterView).layout(left, top, right, bottom);
        if (mIsDebugLayout)
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
