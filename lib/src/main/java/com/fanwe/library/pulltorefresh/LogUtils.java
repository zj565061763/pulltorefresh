package com.fanwe.library.pulltorefresh;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by zhengjun on 2017/9/7.
 */
public class LogUtils
{
    private String mDefaultTag;

    private boolean mIsDebug;
    private String mTag;

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

    public void setTag(String tag)
    {
        mTag = tag;
    }

    private String getTag()
    {
        if (!TextUtils.isEmpty(mTag))
        {
            return mTag;
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
            Log.i(getTag(), msg);
        }
    }

    public void e(String msg)
    {
        if (mIsDebug)
        {
            Log.e(getTag(), msg);
        }
    }
}
