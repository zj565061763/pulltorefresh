package com.sd.demo.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2017/6/30.
 */

public class TestViewGroup extends FrameLayout
{
    public TestViewGroup(Context context)
    {
        super(context);
    }

    public TestViewGroup(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TestViewGroup(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        boolean result = super.dispatchTouchEvent(event);
        Log.e("TouchEvent", "FrameLayout dispatchTouchEvent:" + event.getAction() + "," + result);
        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        boolean result = super.onInterceptTouchEvent(ev);
        Log.e("TouchEvent", "FrameLayout onInterceptTouchEvent:" + ev.getAction() + "," + result);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean result = super.onTouchEvent(event);
        Log.e("TouchEvent", "FrameLayout onTouchEvent:" + event.getAction() + "," + result);
        return result;
    }
}
