package com.fanwe.library.pulltorefresh;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

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
     * Reset状态下mRootLayout的top值
     */
    private int mRootTopReset;
    /**
     * 触发拦截拖动的最小移动距离，默认0
     */
    private int mTouchSlop;
    /**
     * 设置拖动的时候要消耗的拖动距离比例
     */
    private float mComsumeScrollPercent = DEFAULT_COMSUME_SCROLL_PERCENT;

    private SDScroller mScroller;

    private boolean mHasOnLayout;
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
        mScroller.setMaxScrollDistance(1000);
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
    public void startRefreshingFromHeader()
    {
        if (mMode == Mode.DISABLE)
        {
            return;
        }
        if (isRefreshing())
        {
            return;
        }
        setDirection(Direction.FROM_HEADER);
        setState(State.REFRESHING);
        updateViewPositionByState();
    }

    @Override
    public void startRefreshingFromFooter()
    {
        if (mMode == Mode.DISABLE)
        {
            return;
        }
        if (isRefreshing())
        {
            return;
        }
        setDirection(Direction.FROM_FOOTER);
        setState(State.REFRESHING);
        updateViewPositionByState();
    }

    @Override
    public void stopRefreshing()
    {
        if (mState != State.RESET)
        {
            setState(State.RESET);
            updateViewPositionByState();
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
        mHeaderView.setLoadingViewType(LoadingViewType.HEADER);
        mHeaderView.setPullToRefreshView(this);
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
        mFooterView.setLoadingViewType(LoadingViewType.FOOTER);
        mFooterView.setPullToRefreshView(this);
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
        return mRefreshView.getTop();
    }

    //----------ISDPullToRefreshView implements end----------

    @Override
    public void computeScroll()
    {
        if (mScroller.computeScrollOffset())
        {
            moveViews(mScroller.getMoveY());
            invalidate();
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
        return Math.abs(mTouchHelper.getDistanceDownY()) > mTouchSlop && mTouchHelper.getDegreeY() < 30;
    }

    private boolean canPull()
    {
        return checkMoveParams() && (canPullFromHeader() || canPullFromFooter());
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
        if (mState == State.PULL_TO_REFRESH)
        {
            setState(State.RESET);
        } else if (mState == State.RELEASE_TO_REFRESH)
        {
            setState(State.REFRESHING);
        }
        updateViewPositionByState();
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

        boolean canMove = false;
        float distanceY = getComsumedDistance(mTouchHelper.getDistanceMoveY());
        if (getDirection() == Direction.FROM_HEADER)
        {
            if (mRefreshView.getTop() + distanceY >= 0)
            {
                canMove = true;
            }
        } else
        {
            if (mRefreshView.getTop() + distanceY <= 0)
            {
                canMove = true;
            }
        }

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
            mState = state;

            invalidate();
            if (mIsDebug)
            {
                Log.i(TAG, "setState:" + mState);
            }

            //通知view改变状态
            if (getDirection() == Direction.FROM_HEADER)
            {
                mHeaderView.onStateChanged(mState, this);
            } else
            {
                mFooterView.onStateChanged(mState, this);
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

            //通知状态变化回调
            if (mOnStateChangedCallback != null)
            {
                mOnStateChangedCallback.onStateChanged(mState, this);
            }

            if (mState == State.RESET)
            {
                setDirection(Direction.NONE);
            }
        }
    }

    private float getComsumeScrollPercent()
    {
        return mComsumeScrollPercent;
    }

    private int getTopReset()
    {
        return 0;
    }

    /**
     * 根据状态更新view的位置
     */
    private void updateViewPositionByState()
    {
        if (mHasOnLayout)
        {
            updateViewPositionByStateReal();
        } else
        {
            mUpdatePositionRunnable = new Runnable()
            {
                @Override
                public void run()
                {
                    updateViewPositionByStateReal();
                }
            };
        }
    }

    /**
     * 根据状态更新view的位置
     */
    private void updateViewPositionByStateReal()
    {
        int startY = mRefreshView.getTop();
        int endY = 0;

        switch (mState)
        {
            case RESET:
            case PULL_TO_REFRESH:
                endY = getTopReset();
                if (mIsDebug)
                {
                    Log.i(TAG, "startScrollToY:" + mState + ":" + startY + "," + endY);
                }
                mScroller.startScrollToY(startY, endY);
                break;
            case RELEASE_TO_REFRESH:
            case REFRESHING:
                if (getDirection() == Direction.FROM_HEADER)
                {
                    endY = getTopReset() + mHeaderView.getRefreshHeight();
                } else
                {
                    endY = getTopReset() - mFooterView.getRefreshHeight();
                }
                if (mIsDebug)
                {
                    Log.i(TAG, "startScrollToY:" + mState + ":" + startY + "," + endY);
                }
                mScroller.startScrollToY(startY, endY);
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
        if (direction != Direction.NONE)
        {
            if (mDirection == Direction.NONE)
            {
                mDirection = direction;
                mLastDirection = direction;
            }
        } else
        {
            mDirection = Direction.NONE;
        }
    }

    private float getComsumedDistance(float distance)
    {
        distance -= distance * getComsumeScrollPercent();
        return distance;
    }

    private void moveViews(float distance)
    {
        if (getDirection() == Direction.FROM_HEADER)
        {
            mHeaderView.offsetTopAndBottom((int) distance);
            mRefreshView.offsetTopAndBottom((int) distance);
        } else
        {
            mRefreshView.offsetTopAndBottom((int) distance);
            mFooterView.offsetTopAndBottom((int) distance);
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
        setHeaderView(new SDSimpleTextLoadingView(getContext()));
        setFooterView(new SDSimpleTextLoadingView(getContext()));
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
        }

        mScroller.setMaxScrollDistance(mHeaderView.getMeasuredHeight() * 5);
        setMeasuredDimension(width, height);
    }

    private void measureLoadingView(View loadingView, int widthMeasureSpec, int heightMeasureSpec)
    {
        LayoutParams params = loadingView.getLayoutParams();
        loadingView.measure(getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), params.width),
                getChildMeasureSpec(heightMeasureSpec, 0, params.height));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        int leftHeader = getPaddingLeft();
        int topHeader = -mHeaderView.getMeasuredHeight() + getPaddingTop();
        int rightHeader = leftHeader + mHeaderView.getMeasuredWidth();
        int bottomHeader = topHeader + mHeaderView.getMeasuredHeight();
        mHeaderView.layout(leftHeader, topHeader, rightHeader, bottomHeader);

        int topRefresh = getPaddingTop();
        int rightRefresh = leftHeader + mRefreshView.getMeasuredWidth();
        int bottomRefresh = topRefresh + mRefreshView.getMeasuredHeight();
        mRefreshView.layout(leftHeader, topRefresh, rightRefresh, bottomRefresh);

        int topFooter = getMeasuredHeight() - getPaddingBottom();
        int rightFooter = leftHeader + mFooterView.getMeasuredWidth();
        int bottomFooter = topFooter + mFooterView.getMeasuredHeight();
        mFooterView.layout(leftHeader, topFooter, rightFooter, bottomFooter);

        if (mIsDebug)
        {
            Log.i(TAG, "onLayout views totalHeight:----------" + getHeight());
            Log.i(TAG, "HeaderView:" + topHeader + "," + bottomHeader);
            Log.i(TAG, "RefreshView:" + topRefresh + "," + bottomRefresh);
            Log.i(TAG, "FooterView:" + topFooter + "," + bottomFooter);
        }

        mHasOnLayout = true;
        runUpdatePositionRunnableIfNeed();
    }

    private void runUpdatePositionRunnableIfNeed()
    {
        if (mHasOnLayout && mUpdatePositionRunnable != null)
        {
            mUpdatePositionRunnable.run();
            mUpdatePositionRunnable = null;
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        mHasOnLayout = false;
        mUpdatePositionRunnable = null;
    }
}
