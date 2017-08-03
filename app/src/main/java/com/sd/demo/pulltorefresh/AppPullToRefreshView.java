package com.sd.demo.pulltorefresh;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.fanwe.library.pulltorefresh.SDPullToRefreshView;
import com.fanwe.library.pulltorefresh.loadingview.SDPullToRefreshLoadingView;
import com.sd.demo.pulltorefresh.loadingview.CustomPullToRefreshLoadingView;

/**
 * Created by Administrator on 2017/8/3.
 */

public class AppPullToRefreshView extends SDPullToRefreshView
{
    public AppPullToRefreshView(@NonNull Context context)
    {
        super(context);
    }

    public AppPullToRefreshView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AppPullToRefreshView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected SDPullToRefreshLoadingView onCreateHeaderView()
    {
        return new CustomPullToRefreshLoadingView(getContext());
    }

    @Override
    protected SDPullToRefreshLoadingView onCreateFooterView()
    {
        return new CustomPullToRefreshLoadingView(getContext());
    }
}
