package com.fanwe.library.pulltorefresh;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2017/6/26.
 */

public abstract class SDPullToRefreshLoadingView extends FrameLayout implements SDPullToRefreshView.IPullToRefreshLoadingView
{
    public SDPullToRefreshLoadingView(@NonNull Context context)
    {
        super(context);
    }

    public SDPullToRefreshLoadingView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SDPullToRefreshLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    private SDPullToRefreshView.LoadingViewType mLoadingViewType;

    @Override
    public SDPullToRefreshView.LoadingViewType getLoadingViewType()
    {
        return mLoadingViewType;
    }

    final void setLoadingViewType(SDPullToRefreshView.LoadingViewType loadingViewType)
    {
        mLoadingViewType = loadingViewType;
    }

    @Override
    public void onViewPositionChanged(SDPullToRefreshView view)
    {

    }

    @Override
    public int getRefreshHeight()
    {
        return getHeight();
    }
}
