package com.fanwe.library.pulltorefresh.loadingview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.fanwe.library.pulltorefresh.R;

/**
 * Created by Administrator on 2017/6/30.
 */

public abstract class SDSimpleImageLoadingView extends SDPullToRefreshLoadingView
{
    public SDSimpleImageLoadingView(@NonNull Context context)
    {
        super(context);
        init();
    }

    public SDSimpleImageLoadingView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public SDSimpleImageLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private ImageView iv_image;

    protected void init()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.view_simple_image_loading, this, true);
        iv_image = (ImageView) findViewById(R.id.iv_image);
    }

    public ImageView getImageView()
    {
        return iv_image;
    }
}
