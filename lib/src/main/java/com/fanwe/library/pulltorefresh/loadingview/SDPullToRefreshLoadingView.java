package com.fanwe.library.pulltorefresh.loadingview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.fanwe.library.pulltorefresh.ISDPullToRefreshView;
import com.fanwe.library.pulltorefresh.SDPullToRefreshView;

/**
 * Created by Administrator on 2017/6/26.
 */

public abstract class SDPullToRefreshLoadingView extends FrameLayout implements ISDPullToRefreshView.IPullToRefreshLoadingView
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

    @Override
    public final ISDPullToRefreshView.LoadingViewType getLoadingViewType()
    {
        if (getPullToRefreshView().getHeaderView() == this)
        {
            return ISDPullToRefreshView.LoadingViewType.HEADER;
        } else if (getPullToRefreshView().getFooterView() == this)
        {
            return ISDPullToRefreshView.LoadingViewType.FOOTER;
        } else
        {
            return null;
        }
    }

    @Override
    public final SDPullToRefreshView getPullToRefreshView()
    {
        return (SDPullToRefreshView) getParent();
    }

    @Override
    public void onViewPositionChanged(SDPullToRefreshView view)
    {

    }

    @Override
    public boolean canRefresh(int scrollDistance)
    {
        return scrollDistance >= getHeight();
    }

    @Override
    public int getRefreshHeight()
    {
        return getHeight();
    }
}
