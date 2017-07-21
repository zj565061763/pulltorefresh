package com.fanwe.library.pulltorefresh;

import android.content.Context;
import android.widget.Scroller;

class SDScroller extends Scroller
{
    /**
     * 移动像素每毫秒
     */
    public static final float DEFAULT_SPEED = 0.5f;

    private float mScrollSpeed = DEFAULT_SPEED;

    private int mLastX;
    private int mLastY;

    /**
     * 两次computeScrollOffset()之间x移动的距离
     */
    private int mMoveX;
    /**
     * 两次computeScrollOffset()之间y移动的距离
     */
    private int mMoveY;

    public SDScroller(Context context)
    {
        super(context);
    }

    public void setScrollSpeed(float scrollSpeed)
    {
        mScrollSpeed = scrollSpeed;
    }

    // scroll
    public void startScrollX(int startX, int dx, long duration)
    {
        startScroll(startX, 0, dx, 0, (int) duration);
    }

    public void startScrollY(int startY, int dy, long duration)
    {
        startScroll(0, startY, 0, dy, (int) duration);
    }

    // scrollTo
    public void startScrollToX(int startX, int endX, long duration)
    {
        startScrollTo(startX, 0, endX, 0, duration);
    }

    public void startScrollToY(int startY, int endY, long duration)
    {
        startScrollTo(0, startY, 0, endY, duration);
    }

    public void startScrollTo(int startX, int startY, int endX, int endY, long duration)
    {
        int dx = endX - startX;
        int dy = endY - startY;

        startScroll(startX, startY, dx, dy, (int) duration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration)
    {
        //最终调用的方法

        mLastX = startX;
        mLastY = startY;

        if (duration < 0)
        {
            duration = (int) getDurationCalculate(dx, dy);
        }

        super.startScroll(startX, startY, dx, dy, duration);
    }

    @Override
    public boolean computeScrollOffset()
    {
        boolean result = super.computeScrollOffset();

        int currX = getCurrX();
        int currY = getCurrY();

        mMoveX = currX - mLastX;
        mMoveY = currY - mLastY;

        mLastX = currX;
        mLastY = currY;
        return result;
    }

    /**
     * 两次computeScrollOffset()之间x移动的距离
     *
     * @return
     */
    public int getMoveX()
    {
        return mMoveX;
    }

    /**
     * 两次computeScrollOffset()之间y移动的距离
     *
     * @return
     */
    public int getMoveY()
    {
        return mMoveY;
    }

    /**
     * 返回根据滚动距离和滚动速度算出的滚动时长
     *
     * @param dx x滚动距离
     * @param dy y滚动距离
     * @return
     */
    public long getDurationCalculate(float dx, float dy)
    {
        float distance = (float) Math.sqrt(Math.abs(dx * dx) + Math.abs(dy * dy));
        float duration = distance / mScrollSpeed;

        return (long) duration;
    }
}
