package com.sd.demo.pulltorefresh;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2017/6/30.
 */

public class TestView extends View
{
    public TestView(Context context)
    {
        super(context);
    }

    public TestView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        boolean result = super.dispatchTouchEvent(event);
        Log.i("TouchEvent", "view dispatchTouchEvent:" + event.getAction() + "," + result);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean result = super.onTouchEvent(event);
        Log.i("TouchEvent", "view onTouchEvent:" + event.getAction() + "," + result);
        return result;
    }
}
