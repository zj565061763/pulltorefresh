package com.fanwe.lib.pulltorefresh;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by zhengjun on 2017/9/7.
 */
class LogUtils
{
    private String mDefaultTag;

    private boolean mIsDebug;
    private String mDebugTag;

    public LogUtils(Class clazz)
    {
        this(clazz.getSimpleName());
    }

    public LogUtils(String defaultTag)
    {
        if (TextUtils.isEmpty(defaultTag))
        {
            throw new IllegalArgumentException("defaultTag must not be null or empty");
        }
        mDefaultTag = defaultTag;
    }

    public boolean isDebug()
    {
        return mIsDebug;
    }

    public void setDebug(boolean debug)
    {
        mIsDebug = debug;
    }

    public void setDebugTag(String debugTag)
    {
        mDebugTag = debugTag;
    }

    private String getDebugTag()
    {
        if (!TextUtils.isEmpty(mDebugTag))
        {
            return mDebugTag;
        }
        if (!TextUtils.isEmpty(mDefaultTag))
        {
            return mDefaultTag;
        }
        return "";
    }

    public void i(String msg)
    {
        if (mIsDebug)
        {
            Log.i(getDebugTag(), msg);
        }
    }

    public void e(String msg)
    {
        if (mIsDebug)
        {
            Log.e(getDebugTag(), msg);
        }
    }
}
