package com.fanwe.library.pulltorefresh;

import android.content.Context;
import android.widget.Scroller;

class SDScroller extends Scroller
{
    /**
     * 移动像素每毫秒
     */
    public static final float DEFAULT_SPEED = 0.5f;

    private int mScrollDuration;
    private int mMaxScrollDistance;

    private int mLastX;
    private int mLastY;

    private int mMoveX;
    private int mMoveY;

    public SDScroller(Context context)
    {
        super(context);
    }

    public void setScrollDuration(int scrollDuration)
    {
        mScrollDuration = scrollDuration;
    }

    public void setMaxScrollDistance(int maxScrollDistance)
    {
        mMaxScrollDistance = maxScrollDistance;
        setScrollDuration((int) (mMaxScrollDistance / DEFAULT_SPEED));
    }

    // scroll
    public void startScrollX(int startX, int dx)
    {
        int duration = getDurationPercent(dx, 0);
        startScroll(startX, 0, dx, 0, duration);
    }

    public void startScrollY(int startY, int dy)
    {
        int duration = getDurationPercent(dy, 0);
        startScroll(0, startY, 0, dy, duration);
    }

    // scrollTo
    public void startScrollToX(int startX, int endX)
    {
        startScrollTo(startX, 0, endX, 0);
    }

    public void startScrollToY(int startY, int endY)
    {
        startScrollTo(0, startY, 0, endY);
    }

    public void startScrollTo(int startX, int startY, int endX, int endY)
    {
        int dx = 0;
        int dy = 0;

        dx = endX - startX;
        dy = endY - startY;

        int duration = getDurationPercent(dx, dy);
        startScroll(startX, startY, dx, dy, duration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration)
    {
        mLastX = startX;
        mLastY = startY;
        super.startScroll(startX, startY, dx, dy, duration);
    }

    @Override
    public boolean computeScrollOffset()
    {
        boolean result = super.computeScrollOffset();

        mMoveX = getCurrX() - mLastX;
        mMoveY = getCurrY() - mLastY;

        mLastX = getCurrX();
        mLastY = getCurrY();
        return result;
    }

    public int getMoveX()
    {
        return mMoveX;
    }

    public int getMoveY()
    {
        return mMoveY;
    }

    public int getDurationPercent(float dx, float dy)
    {
        return getDurationPercent(dx, dy, mMaxScrollDistance, mScrollDuration);
    }

    public static int getDurationPercent(float dx, float dy, float maxDistance, long maxDuration)
    {
        int result = 0;
        float distance = (float) Math.sqrt(Math.abs(dx * dx) + Math.abs(dy * dy));
        float percent = Math.abs(distance) / Math.abs(maxDistance);
        float duration = percent * (float) maxDuration;

        result = (int) duration;
        return result;
    }

}
