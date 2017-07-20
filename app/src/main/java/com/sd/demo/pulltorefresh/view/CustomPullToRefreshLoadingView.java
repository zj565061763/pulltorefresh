package com.sd.demo.pulltorefresh.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.fanwe.library.pulltorefresh.ISDPullToRefreshView;
import com.fanwe.library.pulltorefresh.SDPullToRefreshView;
import com.fanwe.library.pulltorefresh.loadingview.SDSimpleImageLoadingView;
import com.fanwe.library.utils.SDViewUtil;
import com.sd.demo.pulltorefresh.R;

/**
 * Created by Administrator on 2017/6/30.
 */

public class CustomPullToRefreshLoadingView extends SDSimpleImageLoadingView
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
    public void onStateChanged(ISDPullToRefreshView.State state, SDPullToRefreshView view)
    {
        switch (state)
        {
            case RESET:
            case PULL_TO_REFRESH:
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
