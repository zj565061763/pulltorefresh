package com.sd.demo.pulltorefresh.loadingview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.fanwe.lib.pulltorefresh.FIPullToRefreshView;
import com.fanwe.lib.pulltorefresh.FPullToRefreshView;
import com.fanwe.lib.pulltorefresh.loadingview.SimpleImageLoadingView;
import com.fanwe.library.utils.SDViewUtil;
import com.sd.demo.pulltorefresh.R;

/**
 * Created by Administrator on 2017/6/30.
 */

public class CustomPullToRefreshLoadingView extends SimpleImageLoadingView
{
    public CustomPullToRefreshLoadingView(@NonNull Context context)
    {
        super(context);
    }

    public CustomPullToRefreshLoadingView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomPullToRefreshLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init()
    {
        super.init();
        SDViewUtil.setHeight(getImageView(), SDViewUtil.dp2px(35));
    }

    @Override
    public void onStateChanged(FIPullToRefreshView.State newState, FIPullToRefreshView.State oldState, FPullToRefreshView view)
    {
        switch (newState)
        {
            case RESET:
            case PULL_TO_REFRESH:
            case REFRESH_FINISH:
                getImageView().setImageResource(R.drawable.ic_pull_refresh_normal);
                break;
            case RELEASE_TO_REFRESH:
                getImageView().setImageResource(R.drawable.ic_pull_refresh_ready);
                break;
            case REFRESHING:
                getImageView().setImageResource(R.drawable.ic_pull_refresh_refreshing);
                SDViewUtil.startAnimationDrawable(getImageView().getDrawable());
                break;
        }
    }
}
