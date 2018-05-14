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
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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

    private ViewDragHelper mViewDragHelper;

    private void init(AttributeSet attrs)
    {
        initViewDragHelper();
    }

    private void initViewDragHelper()
    {
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback()
        {
            @Override
            public boolean tryCaptureView(View child, int pointerId)
            {
                return false;
            }

            @Override
            public void onViewCaptured(View capturedChild, int activePointerId)
            {
                super.onViewCaptured(capturedChild, activePointerId);
                if (mIsDebug)
                {
                    Log.i(getDebugTag(), "ViewDragHelper onViewCaptured----------");
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel)
            {
                super.onViewReleased(releasedChild, xvel, yvel);
                if (mIsDebug)
                {
                    Log.i(getDebugTag(), "ViewDragHelper onViewReleased----------");
                }

                if (getState() == State.RELEASE_TO_REFRESH)
                {
                    setState(State.REFRESHING);
                }
                scrollViewByState();
            }

            @Override
            public void onViewDragStateChanged(int state)
            {
                super.onViewDragStateChanged(state);
                if (state == ViewDragHelper.STATE_IDLE)
                {
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
            public int clampViewPositionHorizontal(View child, int left, int dx)
            {
                return child.getLeft();
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy)
            {
                final int dyConsume = getComsumedDistance(dy);
                final int topConsume = top - dyConsume;

                int result = child.getTop();
                if (child == getHeaderView())
                {
                    result = Math.max(getTopHeaderViewReset(), topConsume);
                } else if (child == getFooterView())
                {
                    result = Math.min(getTopFooterViewReset(), topConsume);
                }
                return result;
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy)
            {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                if (mViewDragHelper.getViewDragState() == ViewDragHelper.STATE_DRAGGING)
                {
                    updateStateByMoveDistance();
                }

                moveViews(dy, false);
            }
        });
    }

    @Override
    public void computeScroll()
    {
        if (mViewDragHelper.continueSettling(true))
        {
            if (mIsDebug)
            {
                int top = 0;
                if (getDirection() == Direction.FROM_HEADER)
                {
                    top = getHeaderView().getTop();
                } else
                {
                    top = getFooterView().getTop();
                }
                Log.i(getDebugTag(), "computeScroll:" + top + " " + getState());
            }

            invalidate();
        } else
        {
            Log.i(getDebugTag(), "computeScroll finish:" + getState());
        }
    }

    private final FTouchHelper mTouchHelper = new FTouchHelper();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (getMode() == Mode.DISABLE || isRefreshing())
        {
            return false;
        }
        if (mTouchHelper.isTagIntercept())
        {
            return true;
        }

        mTouchHelper.processTouchEvent(ev);
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                // 如果ViewDragHelper未收到过ACTION_DOWN事件，则不会处理后续的拖动逻辑
                mViewDragHelper.processTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                if (canPull())
                {
                    mTouchHelper.setTagIntercept(true);
                    FTouchHelper.requestDisallowInterceptTouchEvent(this, true);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTouchHelper.setTagIntercept(false);
                FTouchHelper.requestDisallowInterceptTouchEvent(this, false);
                break;
        }
        return mTouchHelper.isTagIntercept();
    }

    private boolean canPull()
    {
        return mTouchHelper.getDegreeYFrom(FTouchHelper.EVENT_DOWN) < 40
                && (canPullFromHeader() || canPullFromFooter())
                && getState() == State.RESET
                && isViewIdle();
    }

    private boolean canPullFromHeader()
    {
        return mTouchHelper.isMoveBottomFrom(FTouchHelper.EVENT_DOWN)
                && (getMode() == Mode.BOTH || getMode() == Mode.PULL_FROM_HEADER)
                && FTouchHelper.isScrollToTop(getRefreshView())
                && (mPullCondition != null ? mPullCondition.canPullFromHeader() : true);
    }

    private boolean canPullFromFooter()
    {
        return mTouchHelper.isMoveTopFrom(FTouchHelper.EVENT_DOWN)
                && (getMode() == Mode.BOTH || getMode() == Mode.PULL_FROM_FOOTER)
                && FTouchHelper.isScrollToBottom(getRefreshView())
                && (mPullCondition != null ? mPullCondition.canPullFromFooter() : true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (getMode() == Mode.DISABLE || isRefreshing())
        {
            return false;
        }

        mTouchHelper.processTouchEvent(event);
        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                if (mTouchHelper.isTagConsume())
                {
                    processMoveEvent(event);
                } else
                {
                    if (mTouchHelper.isTagIntercept() || canPull())
                    {
                        mTouchHelper.setTagConsume(true);
                        mTouchHelper.setTagIntercept(true);
                        FTouchHelper.requestDisallowInterceptTouchEvent(this, true);
                    } else
                    {
                        mTouchHelper.setTagConsume(false);
                        mTouchHelper.setTagIntercept(false);
                        FTouchHelper.requestDisallowInterceptTouchEvent(this, false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mViewDragHelper.processTouchEvent(event);

                mTouchHelper.setTagConsume(false);
                mTouchHelper.setTagIntercept(false);
                FTouchHelper.requestDisallowInterceptTouchEvent(this, false);
                break;
            default:
                mViewDragHelper.processTouchEvent(event);
                break;
        }

        return mTouchHelper.isTagConsume() || event.getAction() == MotionEvent.ACTION_DOWN;
    }

    /**
     * 处理触摸移动事件
     */
    private void processMoveEvent(MotionEvent event)
    {
        //设置方向
        if (mTouchHelper.isMoveBottomFrom(FTouchHelper.EVENT_DOWN))
        {
            setDirection(Direction.FROM_HEADER);
        } else if (mTouchHelper.isMoveTopFrom(FTouchHelper.EVENT_DOWN))
        {
            setDirection(Direction.FROM_FOOTER);
        }

        if (getDirection() == Direction.FROM_HEADER)
        {
            // 捕获HeaderView
            if (mViewDragHelper.getCapturedView() != getHeaderView())
            {
                mViewDragHelper.captureChildView(getHeaderView(), event.getPointerId(event.getActionIndex()));
            }
        } else if (getDirection() == Direction.FROM_FOOTER)
        {
            // 捕获FooterView
            if (mViewDragHelper.getCapturedView() != getFooterView())
            {
                mViewDragHelper.captureChildView(getFooterView(), event.getPointerId(event.getActionIndex()));
            }
        }

        // 处理view的拖动逻辑
        mViewDragHelper.processTouchEvent(event);
    }

    @Override
    protected boolean isViewIdle()
    {
        return mViewDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE;
    }

    @Override
    protected void smoothScrollViewByState()
    {
        int endY = 0;
        View view = null;

        boolean smoothScrollViewStarted = false;
        switch (getState())
        {
            case RESET:
            case PULL_TO_REFRESH:
            case FINISH:
                if (getDirection() == Direction.FROM_HEADER)
                {
                    view = getHeaderView();
                    endY = getTopHeaderViewReset();
                } else
                {
                    view = getFooterView();
                    endY = getTopFooterViewReset();
                }

                if (mViewDragHelper.smoothSlideViewTo(view, view.getLeft(), endY))
                {
                    if (mIsDebug)
                    {
                        Log.i(getDebugTag(), "smoothScrollViewByState:" + view.getTop() + "," + endY + " " + getState());
                    }

                    smoothScrollViewStarted = true;
                    invalidate();
                }
                break;
            case RELEASE_TO_REFRESH:
            case REFRESHING:
                if (getDirection() == Direction.FROM_HEADER)
                {
                    view = getHeaderView();
                    endY = getTopHeaderViewReset() + getHeaderView().getRefreshHeight();
                } else
                {
                    view = getFooterView();
                    endY = getTopFooterViewReset() - getFooterView().getRefreshHeight();
                }

                if (mViewDragHelper.smoothSlideViewTo(view, view.getLeft(), endY))
                {
                    if (mIsDebug)
                    {
                        Log.i(getDebugTag(), "smoothScrollViewByState:" + view.getTop() + "," + endY + " " + getState());
                    }

                    smoothScrollViewStarted = true;
                    invalidate();
                }
                break;
        }

        //通知刷新回调
        if (getState() == State.REFRESHING)
        {
            if (smoothScrollViewStarted)
            {
                //如果滚动触发成功，则滚动结束会通知刷新回调
            } else
            {
                //如果滚动未触发成功，则立即通知刷新回调
                notifyRefreshCallback();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        mViewDragHelper.abort();
    }
}
