/*
 * Copyright (C) 2017 zhengjun, fanwe (http://www.fanwe.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fanwe.lib.pulltorefresh.loadingview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.fanwe.lib.pulltorefresh.R;

public abstract class SimpleImageLoadingView extends FPullToRefreshLoadingView
{
    public SimpleImageLoadingView(@NonNull Context context)
    {
        super(context);
        init();
    }

    public SimpleImageLoadingView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public SimpleImageLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
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
