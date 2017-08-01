package com.fanwe.library.pulltorefresh;

import android.content.Context;
import android.os.Build;
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
    private SDScroller mScroller;
    private boolean mIsScrollerStarted = false;

    private boolean mHasOnLayout = false;
    private Runnable mUpdatePositionRunnable;

    private OnRefreshCallback mOnRefreshCallback;
    private OnStateChangedCallback mOnStateChangedCallback;
    private OnViewPositionChangedCallback mOnViewPositionChangedCallback;
    private IPullCondition mPullCondition;

    private boolean mIsDebug;

    private void initInternal()
    {
        initScroller();
    }

    public void setDebug(boolean debug)
    {
        mIsDebug = debug;
        mTouchHelper.setDebug(debug);
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
    public void setPullCondition(IPullCondition pullCondition)
    {
        mPullCondition = pullCondition;
    }

    @Override
    public void setOverLayMode(boolean overLayMode)
    {
        if (mScroller.isFinished() && mState == State.RESET)
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
            moveViews(mScroller.getDistanceMoveY());
            ViewCompat.postInvalidateOnAnimation(this);
        } else
        {
            if (mIsScrollerStarted)
            {
                mIsScrollerStarted = false;
                switch (mState)
                {
                    case PULL_TO_REFRESH:
                    case REFRESH_FINISH:
                        setState(State.RESET);
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
                SDTouchHelper.requestDisallowInterceptTouchEvent(this, false);
                break;
            case MotionEvent.ACTION_MOVE:
                if (canPull())
                {
                    mTouchHelper.setNeedIntercept(true);
                    SDTouchHelper.requestDisallowInterceptTouchEvent(this, true);
                    if (mIsDebug)
                    {
                        Log.e(TAG, "onInterceptTouchEvent Intercept success when isMoveDown:" + mTouchHelper.isMoveDown(true));
                    }
                }
                break;
        }
        return mTouchHelper.isNeedIntercept();
    }

    private boolean checkMoveParams()
    {
        return (mCheckDragDegree ? mTouchHelper.getDegreeY(true) < 40 : true);
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
        return mTouchHelper.isMoveDown(true)
                && (mMode == Mode.BOTH || mMode == Mode.PULL_FROM_HEADER)
                && SDTouchHelper.isScrollToTop(mRefreshView)
                && (mPullCondition != null ? mPullCondition.canPullFromHeader() : true);
    }

    private boolean canPullFromFooter()
    {
        return mTouchHelper.isMoveUp(true)
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
                if (mTouchHelper.isNeedIntercept())
                {
                    // 已经满足拖动条件，直接处理拖动逻辑
                    mTouchHelper.setNeedCosume(true);
                }

                if (mTouchHelper.isNeedCosume())
                {
                    processMoveEvent();
                } else
                {
                    if (canPull())
                    {
                        mTouchHelper.setNeedCosume(true);
                        mTouchHelper.setNeedIntercept(true);
                        SDTouchHelper.requestDisallowInterceptTouchEvent(this, true);
                    } else
                    {
                        mTouchHelper.setNeedIntercept(false);
                        SDTouchHelper.requestDisallowInterceptTouchEvent(this, false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onActionUp();

                mTouchHelper.setNeedCosume(false);
                mTouchHelper.setNeedIntercept(false);
                SDTouchHelper.requestDisallowInterceptTouchEvent(this, false);
                break;
        }

        return mTouchHelper.isNeedCosume() || event.getAction() == MotionEvent.ACTION_DOWN;
    }

    private void onActionUp()
    {
        if (mState == State.RELEASE_TO_REFRESH)
        {
            setState(State.REFRESHING);
        }
        smoothScrollViewByState();
    }

    /**
     * 处理触摸移动事件
     */
    private void processMoveEvent()
    {
        //设置方向
        if (mTouchHelper.isMoveDown(true))
        {
            setDirection(Direction.FROM_HEADER);
        } else if (mTouchHelper.isMoveUp(true))
        {
            setDirection(Direction.FROM_FOOTER);
        }

        int dy = getComsumedDistance(mTouchHelper.getDistanceY(false));
        if (getDirection() == Direction.FROM_HEADER)
        {
            dy = mTouchHelper.getLegalDistanceY(mHeaderView, getTopHeaderViewReset(), Integer.MAX_VALUE, dy);
        } else
        {
            dy = mTouchHelper.getLegalDistanceY(mFooterView, Integer.MIN_VALUE, getTopFooterViewReset(), dy);
        }

        if (dy != 0)
        {
            moveViews(dy);
            updateStateByMoveDistance();
        }
    }

    /**
     * 移动view
     *
     * @param dy 要移动的距离
     */
    private void moveViews(int dy)
    {
        if (dy == 0)
        {
            return;
        }

        if (getDirection() == Direction.FROM_HEADER)
        {
            ViewCompat.offsetTopAndBottom(mHeaderView, dy);
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
            ViewCompat.offsetTopAndBottom(mFooterView, dy);
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
                    mIsScrollerStarted = true;
                    if (mIsDebug)
                    {
                        Log.i(TAG, "smoothScrollViewByState:" + mState + " startScrollToY:" + startY + "," + endY);
                    }
                } else
                {
                    setState(State.RESET);
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
                    mIsScrollerStarted = true;
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
                        top += mHeaderView.getRefreshHeight();
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
                        top -= mFooterView.getRefreshHeight();
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
                            top += mHeaderView.getRefreshHeight();
                        } else if (getDirection() == Direction.FROM_FOOTER)
                        {
                            top -= mFooterView.getRefreshHeight();
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
