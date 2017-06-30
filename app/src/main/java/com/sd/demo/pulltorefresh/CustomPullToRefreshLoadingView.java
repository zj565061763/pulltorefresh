package com.sd.demo.pulltorefresh;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.fanwe.library.pulltorefresh.ISDPullToRefreshView;
import com.fanwe.library.pulltorefresh.SDPullToRefreshLoadingView;
import com.fanwe.library.utils.SDViewUtil;

/**
 * Created by Administrator on 2017/6/30.
 */

public class CustomPullToRefreshLoadingView extends SDPullToRefreshLoadingView
{
    public CustomPullToRefreshLoadingView(@NonNull Context context)
    {
        super(context);
        init();
    }

    public CustomPullToRefreshLoadingView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CustomPullToRefreshLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private ImageView iv_image;

    private void init()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.view_custom_loading, this, true);
        iv_image = (ImageView) findViewById(R.id.iv_image);
    }

    @Override
    public void onStateChanged(ISDPullToRefreshView.State state)
    {
        switch (state)
        {
            case RESET:
            case PULL_TO_REFRESH:
                iv_image.setImageResource(R.drawable.ic_pull_refresh_normal);
                break;
            case RELEASE_TO_REFRESH:
                iv_image.setImageResource(R.drawable.ic_pull_refresh_ready);
                break;
            case REFRESHING:
                iv_image.setImageResource(R.drawable.ic_pull_refresh_refreshing);
                SDViewUtil.startAnimationDrawable(iv_image.getDrawable());
                break;
        }
    }
}
