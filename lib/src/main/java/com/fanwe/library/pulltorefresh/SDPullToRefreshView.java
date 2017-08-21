package com.fanwe.library.pulltorefresh;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.fanwe.library.pulltorefresh.loadingview.SDPullToRefreshLoadingView;
import com.fanwe.library.pulltorefresh.loadingview.SimpleTextLoadingView;

/**
 * Created by Administrator on 2017/6/26.
 */

public class SDPullToRefreshView extends ViewGroup implements ISDPullToRefreshView
{
    public SDPullToRefreshView(@NonNull Context context)
    {
        super(context);
        initInternal();
    }

    public SDPullToRefreshView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        initInternal();
    }

    public SDPullToRefreshView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initInternal();
    }

    private static final String TAG = "SDPullToRefreshView";

    private SDPullToRefreshLoadingView mHeaderView;
    private SDPullToRefreshLoadingView mFooterView;
    private View mRefreshView;

    private Mode mMode = Mode.BOTH;
    private State mState = State.RESET;
    private Direction mDirection = Direction.NONE;
    private Direction mLastDirection = Direction.NONE;
    private boolean mCheckDragDegree = true;
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
    private ViewDragHelper mViewDragHelper;

    private boolean mHasOnLayout = false;
    private Runnable mUpdatePositionRunnable;

    private OnRefreshCallback mOnRefreshCallback;
    private OnStateChangedCallback mOnStateChangedCallback;
    private OnViewPositionChangedCallback mOnViewPositionChangedCallback;
    private IPullCondition mPullCondition;

    private boolean mIsDebug;

    private void initInternal()
    {
        addLoadingViews();
        initViewDragHelper();
    }

    public void setDebug(boolean debug)
    {
        mIsDebug = debug;
        mTouchHelper.setDebug(debug);
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
                    Log.i(TAG, "ViewDragHelper onViewCaptured----------");
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel)
            {
                super.onViewReleased(releasedChild, xvel, yvel);
                if (mIsDebug)
                {
                    Log.i(TAG, "ViewDragHelper onViewReleased");
                }

                if (mState == State.RELEASE_TO_REFRESH)
                {
                    setState(State.REFRESHING);
                }
                smoothScrollViewByState();
            }

            @Override
            public void onViewDragStateChanged(int state)
            {
                super.onViewDragStateChanged(state);
                if (state == ViewDragHelper.STATE_IDLE)
                {
                    switch (mState)
                    {
                        case PULL_TO_REFRESH:
                        case REFRESH_FINISH:
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
                if (child == mHeaderView)
                {
                    result = Math.max(getTopHeaderViewReset(), topConsume);
                } else if (child == mFooterView)
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

                moveViews(dy);
            }
        });
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams()
    {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    //----------ISDPullToRefreshView implements start----------

    @Override
    public void setMode(Mode mode)
    {
        if (mode != null && mMode != mode)
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
        if (mViewDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE && mState == State.RESET)
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
    public void setComsumeScrollPercent(float comsumeScrollPercent)
    {
        if (comsumeScrollPercent < 0)
        {
            comsumeScrollPercent = 0;
        }
        if (comsumeScrollPercent > 1)
        {
            comsumeScrollPercent = 1;
        }
        mComsumeScrollPercent = comsumeScrollPercent;
    }

    @Override
    public void setDurationShowRefreshResult(int durationShowRefreshResult)
    {
        if (durationShowRefreshResult < 0)
        {
            durationShowRefreshResult = DEFAULT_DURATION_SHOW_REFRESH_RESULT;
        }
        mDurationShowRefreshResult = durationShowRefreshResult;
    }

    @Override
    public void setCheckDragDegree(boolean checkDragDegree)
    {
        mCheckDragDegree = checkDragDegree;
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
            smoothScrollViewByState();
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
            smoothScrollViewByState();
        }
    }

    @Override
    public void stopRefreshing()
    {
        if (mState != State.RESET && mState != State.REFRESH_FINISH)
        {
            setState(State.REFRESH_FINISH);
            smoothScrollViewByState();
        }
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
    public SDPullToRefreshLoadingView getHeaderView()
    {
        return mHeaderView;
    }

    @Override
    public void setHeaderView(SDPullToRefreshLoadingView headerView)
    {
        if (headerView == null || headerView == mHeaderView)
        {
            return;
        }

        removeView(mHeaderView);
        mHeaderView = headerView;
        addView(mHeaderView);
    }

    @Override
    public SDPullToRefreshLoadingView getFooterView()
    {
        return mFooterView;
    }

    @Override
    public void setFooterView(SDPullToRefreshLoadingView footerView)
    {
        if (footerView == null || footerView == mFooterView)
        {
            return;
        }

        removeView(mFooterView);
        mFooterView = footerView;
        addView(mFooterView);
    }

    @Override
    public void setRefreshView(View refreshView)
    {
        if (refreshView == null || refreshView == mRefreshView)
        {
            return;
        }

        ViewGroup refreshParent = (ViewGroup) refreshView.getParent();
        ViewGroup.LayoutParams refreshParams = refreshView.getLayoutParams();
        int refreshIndex = -1;
        if (refreshParent != null)
        {
            refreshIndex = refreshParent.indexOfChild(refreshView);
            refreshParent.removeView(refreshView);
        }

        removeView(mRefreshView);
        mRefreshView = refreshView;
        addView(mRefreshView, new ViewGroup.LayoutParams(refreshParams));

        if (refreshParent != null && getParent() == null)
        {
            refreshParent.addView(this, refreshIndex, refreshParams);
        }
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

    //----------ISDPullToRefreshView implements end----------

    @Override
    public void computeScroll()
    {
        if (mViewDragHelper.continueSettling(true))
        {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private SDTouchHelper mTouchHelper = new SDTouchHelper();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (mMode == Mode.DISABLE || isRefreshing())
        {
            return false;
        }
        if (mTouchHelper.isNeedIntercept())
        {
            return true;
        }

        mTouchHelper.processTouchEvent(ev);
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mTouchHelper.setNeedIntercept(false);
                SDTouchHelper.requestDisallowInterceptTouchEvent(this, false);

                // 如果ViewDragHelper未收到过ACTION_DOWN事件，则不会处理后续的拖动逻辑
                mViewDragHelper.processTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                if (canPull())
                {
                    mTouchHelper.setNeedIntercept(true);
                    SDTouchHelper.requestDisallowInterceptTouchEvent(this, true);
                }
                break;
        }
        return mTouchHelper.isNeedIntercept();
    }

    private boolean checkMoveParams()
    {
        return (mCheckDragDegree ? mTouchHelper.getDegreeYFrom(SDTouchHelper.EVENT_DOWN) < 40 : true);
    }

    private boolean isViewReset()
    {
        if (!(mViewDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE))
        {
            return false;
        }
        if (mState != State.RESET)
        {
            return false;
        }
        return true;
    }

    private boolean canPull()
    {
        return checkMoveParams() && (canPullFromHeader() || canPullFromFooter()) && isViewReset();
    }

    private boolean canPullFromHeader()
    {
        return mTouchHelper.isMoveBottomFrom(SDTouchHelper.EVENT_DOWN)
                && (mMode == Mode.BOTH || mMode == Mode.PULL_FROM_HEADER)
                && SDTouchHelper.isScrollToTop(mRefreshView)
                && (mPullCondition != null ? mPullCondition.canPullFromHeader() : true);
    }

    private boolean canPullFromFooter()
    {
        return mTouchHelper.isMoveTopFrom(SDTouchHelper.EVENT_DOWN)
                && (mMode == Mode.BOTH || mMode == Mode.PULL_FROM_FOOTER)
                && SDTouchHelper.isScrollToBottom(mRefreshView)
                && (mPullCondition != null ? mPullCondition.canPullFromFooter() : true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mMode == Mode.DISABLE || isRefreshing())
        {
            return false;
        }

        mTouchHelper.processTouchEvent(event);
        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                if (mTouchHelper.isNeedCosume())
                {
                    processMoveEvent(event);
                } else
                {
                    if (mTouchHelper.isNeedIntercept() || canPull())
                    {
                        mTouchHelper.setNeedCosume(true);
                        mTouchHelper.setNeedIntercept(true);
                        SDTouchHelper.requestDisallowInterceptTouchEvent(this, true);
                    } else
                    {
                        mTouchHelper.setNeedCosume(false);
                        mTouchHelper.setNeedIntercept(false);
                        SDTouchHelper.requestDisallowInterceptTouchEvent(this, false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mViewDragHelper.processTouchEvent(event);

                mTouchHelper.setNeedCosume(false);
                mTouchHelper.setNeedIntercept(false);
                SDTouchHelper.requestDisallowInterceptTouchEvent(this, false);
                break;
            default:
                mViewDragHelper.processTouchEvent(event);
                break;
        }

        return mTouchHelper.isNeedCosume() || event.getAction() == MotionEvent.ACTION_DOWN;
    }

    /**
     * 处理触摸移动事件
     */
    private void processMoveEvent(MotionEvent event)
    {
        //设置方向
        if (mTouchHelper.isMoveBottomFrom(SDTouchHelper.EVENT_DOWN))
        {
            setDirection(Direction.FROM_HEADER);
        } else if (mTouchHelper.isMoveTopFrom(SDTouchHelper.EVENT_DOWN))
        {
            setDirection(Direction.FROM_FOOTER);
        }

        if (getDirection() == Direction.FROM_HEADER)
        {
            // 捕获HeaderView
            if (mViewDragHelper.getCapturedView() != mHeaderView)
            {
                mViewDragHelper.captureChildView(mHeaderView, event.getPointerId(event.getActionIndex()));
            }
        } else if (getDirection() == Direction.FROM_FOOTER)
        {
            // 捕获FooterView
            if (mViewDragHelper.getCapturedView() != mFooterView)
            {
                mViewDragHelper.captureChildView(mFooterView, event.getPointerId(event.getActionIndex()));
            }
        }

        // 处理view的拖动逻辑
        mViewDragHelper.processTouchEvent(event);
    }

    /**
     * 移动view
     *
     * @param dy 要移动的距离
     */
    private void moveViews(int dy)
    {
        if (getDirection() == Direction.FROM_HEADER)
        {
            if (mIsOverLayMode)
            {
                //覆盖模式
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
            if (mIsOverLayMode)
            {
                //覆盖模式
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
     * 更新当前状态
     */
    private void updateStateByMoveDistance()
    {
        int distance = Math.abs(getScrollDistance());
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
    private void setState(State state)
    {
        if (mState != state)
        {
            final State oldState = mState;
            mState = state;

            if (mIsDebug)
            {
                if (mState == State.RESET)
                {
                    Log.e(TAG, "setState:" + mState);
                } else
                {
                    Log.i(TAG, "setState:" + mState);
                }
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

            //通知刷新回调
            if (mState == State.REFRESHING)
            {
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

            if (mState == State.RESET)
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
                        Log.i(TAG, "requestLayout when reset");
                    }
                    requestLayout();
                }

                setDirection(Direction.NONE);
            }
        }
    }

    private Runnable mStopRefreshingRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            stopRefreshing();
        }
    };

    /**
     * 根据当前状态滚动view到对应的位置
     */
    private void smoothScrollViewByState()
    {
        if (mHasOnLayout)
        {
            smoothScrollViewByStateReal();
        } else
        {
            mUpdatePositionRunnable = new Runnable()
            {
                @Override
                public void run()
                {
                    smoothScrollViewByStateReal();
                    mUpdatePositionRunnable = null;
                }
            };
        }
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
    private int getTopHeaderViewReset()
    {
        return getTopAlignTop() - mHeaderView.getMeasuredHeight();
    }

    /**
     * 返回FooterView的Reset静止状态下top值
     *
     * @return
     */
    private int getTopFooterViewReset()
    {
        return getTopAlignBottom();
    }

    /**
     * 根据当前状态滚动view到对应的位置
     */
    private void smoothScrollViewByStateReal()
    {
        int endY = 0;
        View view = null;

        switch (mState)
        {
            case RESET:
            case PULL_TO_REFRESH:
            case REFRESH_FINISH:
                if (getDirection() == Direction.FROM_HEADER)
                {
                    view = mHeaderView;
                    endY = getTopHeaderViewReset();
                } else
                {
                    view = mFooterView;
                    endY = getTopFooterViewReset();
                }

                if (mViewDragHelper.smoothSlideViewTo(view, view.getLeft(), endY))
                {
                    if (mIsDebug)
                    {
                        Log.i(TAG, "smoothScrollViewByState:" + mState + " startScrollToY:" + endY);
                    }
                    invalidate();
                }
                break;
            case RELEASE_TO_REFRESH:
            case REFRESHING:
                if (getDirection() == Direction.FROM_HEADER)
                {
                    view = mHeaderView;
                    endY = getTopHeaderViewReset() + mHeaderView.getRefreshHeight();
                } else
                {
                    view = mFooterView;
                    endY = getTopFooterViewReset() - mFooterView.getRefreshHeight();
                }

                if (mViewDragHelper.smoothSlideViewTo(view, view.getLeft(), endY))
                {
                    if (mIsDebug)
                    {
                        Log.i(TAG, "smoothScrollViewByState:" + mState + " startScrollToY:" + endY);
                    }
                    invalidate();
                }
                break;
        }
    }

    /**
     * 设置拖动方向
     *
     * @param direction
     */
    private void setDirection(Direction direction)
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
                    Log.i(TAG, "setDirection:" + mDirection);
                }
            }
        } else
        {
            mDirection = Direction.NONE;
            if (mIsDebug)
            {
                Log.i(TAG, "setDirection:" + mDirection);
            }
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
        SDPullToRefreshLoadingView headerView = onCreateHeaderView();
        if (headerView == null)
        {
            headerView = new SimpleTextLoadingView(getContext());
        }
        setHeaderView(headerView);

        // FooterView
        SDPullToRefreshLoadingView footerView = onCreateFooterView();
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
    protected SDPullToRefreshLoadingView onCreateHeaderView()
    {
        return null;
    }

    /**
     * 可以重写返回FooterView
     *
     * @return
     */
    protected SDPullToRefreshLoadingView onCreateFooterView()
    {
        return null;
    }

    private int getMinWidth()
    {
        if (Build.VERSION.SDK_INT >= 16)
        {
            return getMinimumWidth();
        } else
        {
            return 0;
        }
    }

    private int getMinHeight()
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

        boolean needReMeasure = false;
        int widthMeasureSpecLoadingView = widthMeasureSpec;
        if (widthMode != MeasureSpec.EXACTLY)
        {
            widthMeasureSpecLoadingView = MeasureSpec.makeMeasureSpec(width, MeasureSpec.UNSPECIFIED);
            needReMeasure = true;
        }
        int heightMeasureSpecLoadingView = MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED);
        measureLoadingView(mHeaderView, widthMeasureSpecLoadingView, heightMeasureSpecLoadingView);
        measureLoadingView(mFooterView, widthMeasureSpecLoadingView, heightMeasureSpecLoadingView);

        measureChild(mRefreshView, widthMeasureSpec, heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY)
        {
            int maxWidth = Math.max(mHeaderView.getMeasuredWidth(), mFooterView.getMeasuredWidth());
            maxWidth = Math.max(maxWidth, mRefreshView.getMeasuredWidth());
            maxWidth += (getPaddingLeft() + getPaddingRight());

            maxWidth = Math.max(maxWidth, getMinWidth());
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

            maxHeight = Math.max(maxHeight, getMinHeight());
            if (heightMode == MeasureSpec.UNSPECIFIED)
            {
                height = maxHeight;
            } else if (heightMode == MeasureSpec.AT_MOST)
            {
                height = Math.min(maxHeight, height);
            }
        }

        if (needReMeasure)
        {
            widthMeasureSpecLoadingView = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            measureLoadingView(mHeaderView, widthMeasureSpecLoadingView, heightMeasureSpecLoadingView);
            measureLoadingView(mFooterView, widthMeasureSpecLoadingView, heightMeasureSpecLoadingView);
        }

        setMeasuredDimension(width, height);
    }

    private void measureLoadingView(View loadingView, int widthMeasureSpec, int heightMeasureSpec)
    {
        LayoutParams params = loadingView.getLayoutParams();
        loadingView.measure(getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), params.width),
                getChildMeasureSpec(heightMeasureSpec, 0, params.height));
    }

    private int getTopLayoutHeaderView()
    {
        // 初始值
        int top = getTopHeaderViewReset();

        if (getDirection() == Direction.FROM_HEADER)
        {
            if (mViewDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE)
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
            if (mViewDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE)
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
            if (mViewDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE)
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
            int state = mViewDragHelper.getViewDragState();
            if (state == ViewDragHelper.STATE_IDLE)
            {
                Log.i(TAG, "onLayout " + state + " totalHeight:----------" + getHeight());
            } else
            {
                Log.e(TAG, "onLayout " + state + " totalHeight:----------" + getHeight());
            }
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
            Log.i(TAG, "HeaderView:" + top + "," + bottom);
        }

        // RefreshView
        top = getTopLayoutRefreshView();
        if (!mIsOverLayMode && getDirection() == Direction.FROM_HEADER
                && bottom > top)
        {
            top = bottom;
        }
        right = left + mRefreshView.getMeasuredWidth();
        bottom = top + mRefreshView.getMeasuredHeight();
        mRefreshView.layout(left, top, right, bottom);
        if (mIsDebug)
        {
            Log.i(TAG, "RefreshView:" + top + "," + bottom);
        }

        // FooterView
        top = getTopLayoutFooterView();
        if (!mIsOverLayMode && bottom <= getTopAlignBottom()
                && bottom > top)
        {
            top = bottom;
        }
        right = left + mFooterView.getMeasuredWidth();
        bottom = top + mFooterView.getMeasuredHeight();
        mFooterView.layout(left, top, right, bottom);
        if (mIsDebug)
        {
            Log.i(TAG, "FooterView:" + top + "," + bottom);
        }

        mHasOnLayout = true;
        runUpdatePositionRunnableIfNeed();
    }

    private void runUpdatePositionRunnableIfNeed()
    {
        if (mHasOnLayout && mUpdatePositionRunnable != null)
        {
            post(mUpdatePositionRunnable);
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        removeCallbacks(mStopRefreshingRunnable);
        mHasOnLayout = false;
        mUpdatePositionRunnable = null;
        mViewDragHelper.abort();
    }
}
