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
import com.fanwe.lib.pulltorefresh.loadingview.LoadingView;

public class FPullToRefreshView extends BasePullToRefreshView
{
    public FPullToRefreshView(Context context)
    {
        super(context);
        init(null);
    }

    public FPullToRefreshView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public FPullToRefreshView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private FGestureManager mGestureManager;
    private FScroller mScroller;

    private void init(AttributeSet attrs)
    {
        initScroller();
        initGestureManager();
    }

    private void initScroller()
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
                    {
                        Log.e(getDebugTag(), "onScroll finished:" + " " + getState());
                    }

                    switch (getState())
                    {
                        case REFRESHING:
                            notifyRefreshCallback();
                            break;
                        case PULL_TO_REFRESH:
                        case FINISH:
                            setState(State.RESET);
                            break;
                    }
                }
            }

            @Override
            public void onScroll(int dx, int dy)
            {
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

    private void initGestureManager()
    {
        mGestureManager = new FGestureManager(new FGestureManager.Callback()
        {
            @Override
            public boolean shouldInterceptTouchEvent(MotionEvent event)
            {
                final boolean canPull = canPull();
                if (mIsDebug)
                {
                    Log.i(getDebugTag(), "shouldInterceptTouchEvent:" + canPull);
                }
                return canPull;
            }

            @Override
            public void onTagInterceptChanged(boolean tagIntercept)
            {
                FTouchHelper.requestDisallowInterceptTouchEvent(FPullToRefreshView.this, tagIntercept);
            }

            @Override
            public boolean shouldConsumeTouchEvent(MotionEvent event)
            {
                final boolean canPull = canPull();
                if (mIsDebug)
                {
                    Log.i(getDebugTag(), "shouldConsumeTouchEvent:" + canPull);
                }
                return canPull;
            }

            @Override
            public void onTagConsumeChanged(boolean tagConsume)
            {
                FTouchHelper.requestDisallowInterceptTouchEvent(FPullToRefreshView.this, tagConsume);
            }

            @Override
            public boolean onConsumeEvent(MotionEvent event)
            {
                saveDirectionWhenMove();

                final int dy = (int) mGestureManager.getTouchHelper().getDeltaYFrom(FTouchHelper.EVENT_LAST);
                final int dyConsumed = getComsumedDistance(dy);

                moveViews(dyConsumed, true);
                return true;
            }

            @Override
            public void onConsumeEventFinish(MotionEvent event, VelocityTracker velocityTracker)
            {
                if (mIsDebug)
                {
                    Log.e(getDebugTag(), "onConsumeEventFinish:" + event.getAction());
                }

                if (getState() == State.RELEASE_TO_REFRESH)
                {
                    setState(State.REFRESHING);
                }
                smoothSlideViewByState();
            }
        });
    }

    private void saveDirectionWhenMove()
    {
        if (mGestureManager.getTouchHelper().isMoveBottomFrom(FTouchHelper.EVENT_DOWN))
        {
            setDirection(Direction.FROM_HEADER);
            mScroller.setMaxScrollDistance(((View) getHeaderView()).getHeight());
        } else if (mGestureManager.getTouchHelper().isMoveTopFrom(FTouchHelper.EVENT_DOWN))
        {
            setDirection(Direction.FROM_FOOTER);
            mScroller.setMaxScrollDistance(((View) getFooterView()).getHeight());
        }
    }

    private boolean canPull()
    {
        final boolean checkDegree = mGestureManager.getTouchHelper().getDegreeYFrom(FTouchHelper.EVENT_DOWN) < 30;
        final boolean checkPull = canPullFromHeader() || canPullFromFooter();
        final boolean checkState = getState() == State.RESET;

        return checkDegree && checkPull && checkState;
    }

    private boolean canPullFromHeader()
    {
        final boolean checkIsMoveBottom = mGestureManager.getTouchHelper().isMoveBottomFrom(FTouchHelper.EVENT_DOWN);
        final boolean checkMode = getMode() == Mode.PULL_BOTH || getMode() == Mode.PULL_FROM_HEADER;
        final boolean checkIsScrollToTop = FTouchHelper.isScrollToTop(getRefreshView());
        final boolean checkPullCondition = checkPullConditionHeader();

        return checkIsMoveBottom && checkMode && checkIsScrollToTop && checkPullCondition;
    }

    private boolean canPullFromFooter()
    {
        final boolean checkIsMoveTop = mGestureManager.getTouchHelper().isMoveTopFrom(FTouchHelper.EVENT_DOWN);
        final boolean checkMode = getMode() == Mode.PULL_BOTH || getMode() == Mode.PULL_FROM_FOOTER;
        final boolean checkIsScrollToBottom = FTouchHelper.isScrollToBottom(getRefreshView());
        final boolean checkPullCondition = checkPullConditionFooter();

        return checkIsMoveTop && checkMode && checkIsScrollToBottom && checkPullCondition;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        return mGestureManager.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return mGestureManager.onTouchEvent(event);
    }

    @Override
    public void computeScroll()
    {
        if (mScroller.computeScrollOffset())
        {
            invalidate();
        }
    }

    @Override
    protected boolean isViewIdle()
    {
        return mScroller.isFinished() && !mGestureManager.isTagConsume();
    }

    @Override
    protected boolean onSmoothSlide(int startY, int endY)
    {
        return mScroller.scrollToY(startY, endY, -1);
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        mScroller.abortAnimation();
    }
}
