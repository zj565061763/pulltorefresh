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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

import com.fanwe.lib.gesture.FGestureManager;
import com.fanwe.lib.gesture.FScroller;
import com.fanwe.lib.gesture.FTouchHelper;
import com.fanwe.lib.gesture.tag.TagHolder;
import com.fanwe.lib.pulltorefresh.loadingview.LoadingView;

public class FPullToRefreshView extends BasePullToRefreshView
{
    public FPullToRefreshView(Context context)
    {
        super(context);
    }

    public FPullToRefreshView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FPullToRefreshView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    private FGestureManager mGestureManager;
    private FScroller mScroller;

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
                public void onComputeScrollOffset(int lastX, int lastY, int currX, int currY)
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
                    if (mIsDebug)
                        Log.i(getDebugTag(), "shouldInterceptEvent:" + shouldInterceptEvent);

                    return shouldInterceptEvent;
                }

                @Override
                public boolean shouldConsumeEvent(MotionEvent event)
                {
                    final boolean shouldConsumeEvent = canPull();
                    if (mIsDebug)
                        Log.i(getDebugTag(), "shouldConsumeEvent:" + shouldConsumeEvent);

                    return shouldConsumeEvent;
                }

                @Override
                public boolean onEventConsume(MotionEvent event)
                {
                    saveDirectionWhenMove();

                    final int dy = (int) getGestureManager().getTouchHelper().getDeltaY();
                    final int dyConsumed = getComsumedDistance(dy);
                    moveViews(dyConsumed, true);

                    return true;
                }

                @Override
                public void onEventFinish(MotionEvent event, boolean hasConsumeEvent, VelocityTracker velocityTracker)
                {
                    if (hasConsumeEvent)
                    {
                        if (mIsDebug)
                            Log.e(getDebugTag(), "onConsumeEventFinish:" + event.getAction() + " " + getState());

                        if (getState() == State.RELEASE_TO_REFRESH)
                            setState(State.REFRESHING);

                        smoothSlideViewByState();
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
        final boolean checkDegree = getGestureManager().getTouchHelper().getDegreeYFromDown() < 30;
        final boolean checkPull = canPullFromHeader() || canPullFromFooter();
        final boolean checkState = getState() == State.RESET;

        return checkDegree && checkPull && checkState;
    }

    private boolean canPullFromHeader()
    {
        final boolean checkIsMoveBottom = getGestureManager().getTouchHelper().getDeltaYFromDown() > 0;
        final boolean checkMode = getMode() == Mode.PULL_BOTH || getMode() == Mode.PULL_FROM_HEADER;
        final boolean checkIsScrollToTop = FTouchHelper.isScrollToTop(getRefreshView());
        final boolean checkPullCondition = checkPullConditionHeader();

        return checkIsMoveBottom && checkMode && checkIsScrollToTop && checkPullCondition;
    }

    private boolean canPullFromFooter()
    {
        final boolean checkIsMoveTop = getGestureManager().getTouchHelper().getDeltaYFromDown() < 0;
        final boolean checkMode = getMode() == Mode.PULL_BOTH || getMode() == Mode.PULL_FROM_FOOTER;
        final boolean checkIsScrollToBottom = FTouchHelper.isScrollToBottom(getRefreshView());
        final boolean checkPullCondition = checkPullConditionFooter();

        return checkIsMoveTop && checkMode && checkIsScrollToBottom && checkPullCondition;
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

        return checkScrollerFinished && checkNotDragging;
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
}
