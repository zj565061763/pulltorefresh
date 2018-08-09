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
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.fanwe.lib.gesture.FGestureManager;
import com.fanwe.lib.gesture.FScroller;
import com.fanwe.lib.gesture.FTouchHelper;
import com.fanwe.lib.gesture.tag.TagHolder;
import com.fanwe.lib.pulltorefresh.loadingview.LoadingView;

public class FPullToRefreshView extends BasePullToRefreshView implements NestedScrollingParent, NestedScrollingChild
{
    private FGestureManager mGestureManager;
    private FScroller mScroller;
    private final int mTouchSlop;

    public FPullToRefreshView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setNestedScrollingEnabled(true);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private FScroller getScroller()
    {
        if (mScroller == null)
        {
            mScroller = new FScroller(new Scroller(getContext()));
            mScroller.setCallback(new FScroller.Callback()
            {
                @Override
                public void onScrollStateChanged(boolean isFinished)
                {
                    if (isFinished)
                    {
                        if (mIsDebug)
                            Log.i(getDebugTag(), "onScroll finished:" + " " + getState());

                        dealViewIdle();
                    }
                }

                @Override
                public void onScroll(int lastX, int lastY, int currX, int currY)
                {
                    final int dy = currY - lastY;

                    moveViews(dy, false);
                    if (mIsDebug)
                    {
                        final LoadingView loadingView = getLoadingViewByDirection();
                        final int top = ((View) loadingView).getTop();
                        Log.i(getDebugTag(), "onScroll:" + top + " " + getState());
                    }
                }
            });
        }
        return mScroller;
    }

    private FGestureManager getGestureManager()
    {
        if (mGestureManager == null)
        {
            mGestureManager = new FGestureManager(new FGestureManager.Callback()
            {
                @Override
                public boolean shouldInterceptEvent(MotionEvent event)
                {
                    final boolean shouldInterceptEvent = canPull();
                    return shouldInterceptEvent;
                }

                @Override
                public boolean shouldConsumeEvent(MotionEvent event)
                {
                    final boolean shouldConsumeEvent = canPull();
                    return shouldConsumeEvent;
                }

                @Override
                public boolean onEventConsume(MotionEvent event)
                {
                    saveDirectionWhenMove();

                    final int dy = (int) getGestureManager().getTouchHelper().getDeltaY();
                    moveViews(dy, true);
                    return true;
                }

                @Override
                public void onEventFinish(boolean hasConsumeEvent, VelocityTracker velocityTracker, MotionEvent event)
                {
                    if (hasConsumeEvent)
                    {
                        if (mIsDebug)
                            Log.e(getDebugTag(), "onConsumeEventFinish:" + event.getAction() + " " + getState());

                        processDragFinish();
                    }
                }
            });
            mGestureManager.getTagHolder().setCallback(new TagHolder.Callback()
            {
                @Override
                public void onTagInterceptChanged(boolean tag)
                {
                    FTouchHelper.requestDisallowInterceptTouchEvent(FPullToRefreshView.this, tag);
                }

                @Override
                public void onTagConsumeChanged(boolean tag)
                {
                    FTouchHelper.requestDisallowInterceptTouchEvent(FPullToRefreshView.this, tag);
                }
            });
        }
        return mGestureManager;
    }

    private void processDragFinish()
    {
        if (getState() == State.RELEASE_TO_REFRESH)
            setState(State.REFRESHING);

        smoothSlideViewByState();
    }

    private void saveDirectionWhenMove()
    {
        if (getGestureManager().getTouchHelper().getDeltaYFromDown() > 0)
        {
            setDirection(Direction.FROM_HEADER);
            getScroller().setMaxScrollDistance(((View) getHeaderView()).getHeight());
        } else if (getGestureManager().getTouchHelper().getDeltaYFromDown() < 0)
        {
            setDirection(Direction.FROM_FOOTER);
            getScroller().setMaxScrollDistance(((View) getFooterView()).getHeight());
        }
    }

    private boolean canPull()
    {
        // 为了调试方便，让每个条件都执行后把值都列出来

        final boolean checkDegree = getGestureManager().getTouchHelper().getDegreeYFromDown() < 30;

        final int deltaY = (int) getGestureManager().getTouchHelper().getDeltaYFromDown();
        final boolean checkPullDelta = Math.abs(deltaY) > mTouchSlop;
        final boolean checkPull = (canPullFromHeader() && deltaY > 0) || (canPullFromFooter() && deltaY < 0);

        final boolean checkState = getState() == State.RESET;
        final boolean checkNestedScroll = !mIsNestedScrollStarted;

        return checkDegree && checkPullDelta && checkPull && checkState && checkNestedScroll;
    }

    private boolean canPullFromHeader()
    {
        return (getMode() == Mode.PULL_BOTH || getMode() == Mode.PULL_FROM_HEADER)
                && (FTouchHelper.isScrollToTop(getRefreshView()))
                && (checkPullConditionHeader());
    }

    private boolean canPullFromFooter()
    {
        return (getMode() == Mode.PULL_BOTH || getMode() == Mode.PULL_FROM_FOOTER)
                && (FTouchHelper.isScrollToBottom(getRefreshView()))
                && (checkPullConditionFooter());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        return getGestureManager().onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return getGestureManager().onTouchEvent(event);
    }

    @Override
    public void computeScroll()
    {
        if (getScroller().computeScrollOffset())
            invalidate();
    }

    @Override
    protected boolean isViewIdle()
    {
        final boolean checkScrollerFinished = getScroller().isFinished();
        final boolean checkNotDragging = !getGestureManager().getTagHolder().isTagConsume();
        final boolean checkNotNestedScroll = !mIsNestedScrollStarted;

        return checkScrollerFinished && checkNotDragging && checkNotNestedScroll;
    }

    @Override
    protected boolean onSmoothSlide(int startY, int endY)
    {
        return getScroller().scrollToY(startY, endY, -1);
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        getScroller().abortAnimation();
    }

    private final NestedScrollingParentHelper mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    private final NestedScrollingChildHelper mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);

    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];

    private boolean mIsNestedScrollStarted;
    private boolean mNeedConsumeNestedScroll;

    //---------- NestedScrollingParent Start ----------

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes)
    {
        final boolean checkState = getState() == State.RESET;
        final boolean checkMode = getMode() != Mode.PULL_DISABLE;
        final boolean checkIsScrollToBound = FTouchHelper.isScrollToTop(getRefreshView()) || FTouchHelper.isScrollToBottom(getRefreshView());
        final boolean checkDirection = getDirection() == Direction.NONE;

        final boolean checkTarget = target == getRefreshView();
        final boolean checkNestedScrollAxes = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;

        return checkState && checkMode && checkIsScrollToBound && checkDirection && checkTarget && checkNestedScrollAxes;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes)
    {
        if (mIsDebug)
            Log.i(getDebugTag(), "onNestedScrollAccepted----------");

        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mIsNestedScrollStarted = true;
    }

    @Override
    public void onStopNestedScroll(View child)
    {
        if (mIsDebug)
            Log.e(getDebugTag(), "onStopNestedScroll:" + getDirection());

        mNestedScrollingParentHelper.onStopNestedScroll(child);
        mIsNestedScrollStarted = false;

        if (mNeedConsumeNestedScroll)
        {
            mNeedConsumeNestedScroll = false;
            processDragFinish();
        }

        stopNestedScroll();
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed)
    {
        if (mNeedConsumeNestedScroll)
        {
            consumed[1] = dy;
            moveViews(-dy, true);
        }

        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], mParentScrollConsumed, null))
        {
            consumed[0] += mParentScrollConsumed[0];
            consumed[1] += mParentScrollConsumed[1];
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed)
    {
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mParentOffsetInWindow);

        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy != 0)
        {
            if (getDirection() == Direction.NONE)
            {
                if (dy < 0)
                {
                    // header
                    if (canPullFromHeader())
                    {
                        setDirection(Direction.FROM_HEADER);
                        getScroller().setMaxScrollDistance(((View) getHeaderView()).getHeight());
                    }
                } else if (dy > 0)
                {
                    // footer
                    if (canPullFromFooter())
                    {
                        setDirection(Direction.FROM_FOOTER);
                        getScroller().setMaxScrollDistance(((View) getFooterView()).getHeight());
                    }
                }

                if (getDirection() != Direction.NONE)
                {
                    mNeedConsumeNestedScroll = true;
                    if (mIsDebug)
                        Log.i(getDebugTag(), "onNestedScroll need consume");
                }
            }
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY)
    {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed)
    {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public int getNestedScrollAxes()
    {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    //---------- NestedScrollingParent End ----------

    //---------- NestedScrollingChild Start ----------

    @Override
    public void setNestedScrollingEnabled(boolean enabled)
    {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled()
    {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes)
    {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll()
    {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent()
    {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow)
    {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow)
    {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY)
    {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed)
    {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    //---------- NestedScrollingChild End ----------
}
