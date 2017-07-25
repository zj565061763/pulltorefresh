package com.fanwe.library.pulltorefresh;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
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
    /**
     * HeaderView和FooterView是否是覆盖的模式
     */
    private boolean mIsOverLayMode = false;
    private Boolean mTempIsOverLayMode = null;
    /**
     * 拖动的时候要消耗的拖动距离比例
     */
    private float mComsumeScrollPercent = DEFAULT_COMSUME_SCROLL_PERCENT;
    /**
     * 显示刷新结果的时长
     */
    private int mShowRefreshResultDuration = DEFAULT_SHOW_REFRESH_RESULT_DURATION;
    private SDScroller mScroller;
    private boolean mIsScrollerStartSuccess = false;

    private boolean mHasOnLayout = false;
    private Runnable mUpdatePositionRunnable;

    private OnRefreshCallback mOnRefreshCallback;
    private OnStateChangedCallback mOnStateChangedCallback;
    private OnViewPositionChangedCallback mOnViewPositionChangedCallback;

    private boolean mIsDebug;

    private void initInternal()
    {
        initScroller();
    }

    public void setDebug(boolean debug)
    {
        mIsDebug = debug;
    }

    private void initScroller()
    {
        mScroller = new SDScroller(getContext());
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
    public void setOverLayMode(boolean overLayMode)
    {
        if (mScroller.isFinished() && mState == State.RESET)
        {
            mIsOverLayMode = overLayMode;
        } else
        {
            mTempIsOverLayMode = overLayMode;
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
    public void setShowRefreshResultDuration(int showRefreshResultDuration)
    {
        if (showRefreshResultDuration < 0)
        {
            showRefreshResultDuration = DEFAULT_SHOW_REFRESH_RESULT_DURATION;
        }
        mShowRefreshResultDuration = showRefreshResultDuration;
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
        if (headerView == null)
        {
            return;
        }
        if (mHeaderView != null)
        {
            removeView(mHeaderView);
        }
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
        if (footerView == null)
        {
            return;
        }
        if (mFooterView != null)
        {
            removeView(mFooterView);
        }
        mFooterView = footerView;
        addView(mFooterView);
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
            return mHeaderView.getTop() - getTopScrollReset();
        } else
        {
            return mFooterView.getTop() - getTopScrollReset();
        }
    }

    //----------ISDPullToRefreshView implements end----------

    @Override
    public void computeScroll()
    {
        if (mScroller.computeScrollOffset())
        {
            moveViews(mScroller.getMoveY());
            ViewCompat.postInvalidateOnAnimation(this);
        } else
        {
            if (mIsScrollerStartSuccess)
            {
                final boolean isScrollerStartSuccess = mIsScrollerStartSuccess;
                mIsScrollerStartSuccess = false;
                switch (mState)
                {
                    case PULL_TO_REFRESH:
                    case REFRESH_FINISH:
                        setState(State.RESET);
                        break;
                    case RESET:
                        if (isScrollerStartSuccess)
                        {
                            if (mIsDebug)
                            {
                                Log.i(TAG, "requestLayout when state reset and scroller finished");
                            }
                            requestLayout();
                        }
                        if (mTempIsOverLayMode != null)
                        {
                            mIsOverLayMode = mTempIsOverLayMode;
                            mTempIsOverLayMode = null;

                            if (mIsDebug)
                            {
                                Log.i(TAG, "tempIsOverLayMode is not null update isOverLayMode:" + mIsOverLayMode);
                            }
                        }
                        break;
                }
            }
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
            if (mIsDebug)
            {
                Log.e(TAG, "onInterceptTouchEvent Intercept success because isNeedIntercept is true with action----------" + ev.getAction());
            }
            return true;
        }

        mTouchHelper.processTouchEvent(ev);
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mTouchHelper.setNeedIntercept(false);
                break;
            case MotionEvent.ACTION_MOVE:
                if (canPull())
                {
                    mTouchHelper.setNeedIntercept(true);
                    if (mIsDebug)
                    {
                        Log.e(TAG, "onInterceptTouchEvent Intercept success when isMoveDown:" + mTouchHelper.isMoveDown());
                    }
                }
                break;
        }
        return mTouchHelper.isNeedIntercept();
    }

    private boolean checkMoveParams()
    {
        return mTouchHelper.getDegreeY() < 30;
    }

    private boolean isViewReset()
    {
        if (!mScroller.isFinished())
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
        return mTouchHelper.isMoveDown()
                && (mMode == Mode.BOTH || mMode == Mode.PULL_FROM_HEADER)
                && SDTouchHelper.isScrollToTop(mRefreshView);
    }

    private boolean canPullFromFooter()
    {
        return mTouchHelper.isMoveUp()
                && (mMode == Mode.BOTH || mMode == Mode.PULL_FROM_FOOTER)
                && SDTouchHelper.isScrollToBottom(mRefreshView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mMode == Mode.DISABLE || isRefreshing())
        {
            return super.onTouchEvent(event);
        }

        mTouchHelper.processTouchEvent(event);
        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                if (mTouchHelper.isNeedCosume())
                {
                    processMoveEvent();
                } else
                {
                    if (canPull())
                    {
                        mTouchHelper.setNeedCosume(true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onActionUp();

                mTouchHelper.setNeedCosume(false);
                mTouchHelper.setNeedIntercept(false);
                break;
        }

        return super.onTouchEvent(event) || mTouchHelper.isNeedCosume() || event.getAction() == MotionEvent.ACTION_DOWN;
    }

    private void onActionUp()
    {
        if (mState == State.RELEASE_TO_REFRESH)
        {
            setState(State.REFRESHING);
        }
        smoothScrollViewByState();
    }

    private boolean checkMoveRange(int distanceY)
    {
        boolean canMove = false;

        int topReset = getTopScrollReset();
        int topFuture = 0;

        if (getDirection() == Direction.FROM_HEADER)
        {
            topFuture = mHeaderView.getTop() + distanceY;
            if (topFuture >= topReset)
            {
                canMove = true;
            }
        } else
        {
            topFuture = mFooterView.getTop() + distanceY;
            if (topFuture <= topReset)
            {
                canMove = true;
            }
        }
        return canMove;
    }

    /**
     * 处理触摸移动事件
     */
    private void processMoveEvent()
    {
        //设置方向
        if (mTouchHelper.isMoveDown())
        {
            setDirection(Direction.FROM_HEADER);
        } else if (mTouchHelper.isMoveUp())
        {
            setDirection(Direction.FROM_FOOTER);
        }

        int distanceY = getComsumedDistance(mTouchHelper.getDistanceMoveY());
        boolean canMove = checkMoveRange(distanceY);

        if (canMove)
        {
            moveViews(distanceY);
            updateStateByMoveDistance();
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
            if (distance < mHeaderView.getRefreshHeight())
            {
                setState(State.PULL_TO_REFRESH);
            } else if (distance >= mHeaderView.getRefreshHeight())
            {
                setState(State.RELEASE_TO_REFRESH);
            }
        } else
        {
            if (distance < mFooterView.getRefreshHeight())
            {
                setState(State.PULL_TO_REFRESH);
            } else if (distance >= mFooterView.getRefreshHeight())
            {
                setState(State.RELEASE_TO_REFRESH);
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
                postDelayed(mStopRefreshingRunnable, mShowRefreshResultDuration);
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

    private float getComsumeScrollPercent()
    {
        return mComsumeScrollPercent;
    }

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
     * 返回滚动到Reset静止状态下的位置
     *
     * @return
     */
    private int getTopScrollReset()
    {
        if (getDirection() == Direction.FROM_HEADER)
        {
            return getTopHeaderViewReset();
        } else
        {
            return getTopFooterViewReset();
        }
    }

    /**
     * 返回滚动的起始位置
     *
     * @return
     */
    private int getTopScrollStart()
    {
        if (getDirection() == Direction.FROM_HEADER)
        {
            return mHeaderView.getTop();
        } else
        {
            return mFooterView.getTop();
        }
    }

    /**
     * 根据当前状态滚动view到对应的位置
     */
    private void smoothScrollViewByStateReal()
    {
        int startY = getTopScrollStart();
        int endY = 0;
        int topReset = getTopScrollReset();

        switch (mState)
        {
            case RESET:
            case PULL_TO_REFRESH:
            case REFRESH_FINISH:
                endY = topReset;

                if (mScroller.startScrollToY(startY, endY, -1))
                {
                    mIsScrollerStartSuccess = true;
                    if (mIsDebug)
                    {
                        Log.i(TAG, "smoothScrollViewByState:" + mState + " startScrollToY:" + startY + "," + endY);
                    }
                }
                break;
            case RELEASE_TO_REFRESH:
            case REFRESHING:
                if (getDirection() == Direction.FROM_HEADER)
                {
                    endY = topReset + mHeaderView.getRefreshHeight();
                } else
                {
                    endY = topReset - mFooterView.getRefreshHeight();
                }

                if (mScroller.startScrollToY(startY, endY, -1))
                {
                    mIsScrollerStartSuccess = true;
                    if (mIsDebug)
                    {
                        Log.i(TAG, "smoothScrollViewByState:" + mState + " startScrollToY:" + startY + "," + endY);
                    }
                }
                break;
        }
        invalidate();
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
        distance -= distance * getComsumeScrollPercent();
        return (int) distance;
    }

    /**
     * 移动view
     *
     * @param distance 要移动的距离
     */
    private void moveViews(float distance)
    {
        if (getDirection() == Direction.FROM_HEADER)
        {
            ViewCompat.offsetTopAndBottom(mHeaderView, (int) distance);
            if (mIsOverLayMode)
            {
                //覆盖模式
                if (ViewCompat.getZ(mHeaderView) <= ViewCompat.getZ(mRefreshView))
                {
                    ViewCompat.setZ(mHeaderView, ViewCompat.getZ(mRefreshView) + 1);
                }
            } else
            {
                ViewCompat.offsetTopAndBottom(mRefreshView, (int) distance);
            }
            mHeaderView.onViewPositionChanged(this);
        } else
        {
            ViewCompat.offsetTopAndBottom(mFooterView, (int) distance);
            if (mIsOverLayMode)
            {
                //覆盖模式
                if (ViewCompat.getZ(mFooterView) <= ViewCompat.getZ(mRefreshView))
                {
                    ViewCompat.setZ(mFooterView, ViewCompat.getZ(mRefreshView) + 1);
                }
            } else
            {
                ViewCompat.offsetTopAndBottom(mRefreshView, (int) distance);
            }
            mFooterView.onViewPositionChanged(this);
        }

        if (mOnViewPositionChangedCallback != null)
        {
            mOnViewPositionChangedCallback.onViewPositionChanged(this);
        }
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        if (getChildCount() <= 0)
        {
            throw new IllegalArgumentException("you must add one child to SDPullToRefreshView in your xml file");
        } else if (getChildCount() > 1)
        {
            throw new IllegalArgumentException("you can only add one child to SDPullToRefreshView in your xml file");
        }

        mRefreshView = getChildAt(0);
        addLoadingView();
    }

    private void addLoadingView()
    {
        setHeaderView(new SimpleTextLoadingView(getContext()));
        setFooterView(new SimpleTextLoadingView(getContext()));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int widthMeasureSpecLoadingView = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int heightMeasureSpecLoadingView = MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED);

        measureLoadingView(mHeaderView, widthMeasureSpecLoadingView, heightMeasureSpecLoadingView);
        measureLoadingView(mFooterView, widthMeasureSpecLoadingView, heightMeasureSpecLoadingView);

        measureChild(mRefreshView, widthMeasureSpec, heightMeasureSpec);

        if (heightMode != MeasureSpec.EXACTLY)
        {
            height = mRefreshView.getMeasuredHeight();
            if (height == 0)
            {
                //如果刷新view的高度为0，则给当前view一个默认高度，否则会出现代码触发刷新的时候HeaderView或者FooterView看不见
                height = Math.max(mHeaderView.getMeasuredHeight(), mFooterView.getMeasuredHeight());
            }
            height += (getPaddingTop() + getPaddingBottom());
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
            if (!mScroller.isFinished() || mTouchHelper.isNeedCosume())
            {
                top = mHeaderView.getTop();
            } else
            {
                switch (mState)
                {
                    case REFRESHING:
                    case REFRESH_SUCCESS:
                    case REFRESH_FAILURE:
                        top = getTopAlignTop();
                        break;
                }
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
            if (!mScroller.isFinished() || mTouchHelper.isNeedCosume())
            {
                top = mFooterView.getTop();
            } else
            {
                switch (mState)
                {
                    case REFRESHING:
                    case REFRESH_SUCCESS:
                    case REFRESH_FAILURE:
                        top = getTopAlignBottom() - mFooterView.getMeasuredHeight();
                        break;
                }
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
            if (!mScroller.isFinished() || mTouchHelper.isNeedCosume())
            {
                top = mRefreshView.getTop();
            } else
            {
                switch (mState)
                {
                    case REFRESHING:
                    case REFRESH_SUCCESS:
                    case REFRESH_FAILURE:
                        if (getDirection() == Direction.FROM_HEADER)
                        {
                            top += mHeaderView.getMeasuredHeight();
                        } else if (getDirection() == Direction.FROM_FOOTER)
                        {
                            top -= mFooterView.getMeasuredHeight();
                        }
                        break;
                }
            }
        }
        return top;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        if (mIsDebug)
        {
            Log.i(TAG, "onLayout " + mState + " totalHeight:----------" + getHeight());
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

        int maxScrollDistance = Math.max(mHeaderView.getMeasuredHeight(), mFooterView.getMeasuredHeight());
        mScroller.setMaxScrollDistance(maxScrollDistance);

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
        if (!mScroller.isFinished())
        {
            mScroller.abortAnimation();
        }
    }
}
