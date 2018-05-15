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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.fanwe.lib.pulltorefresh.PullToRefreshView;

import java.lang.reflect.Constructor;

public abstract class BaseLoadingView extends FrameLayout implements
        LoadingView,
        PullToRefreshView.OnStateChangedCallback,
        PullToRefreshView.OnViewPositionChangedCallback
{
    public BaseLoadingView(Context context)
    {
        super(context);
    }

    public BaseLoadingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BaseLoadingView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public final boolean isHeaderView()
    {
        return this == getPullToRefreshView().getHeaderView();
    }

    @Override
    public final boolean isFooterView()
    {
        return this == getPullToRefreshView().getFooterView();
    }

    @Override
    public final PullToRefreshView getPullToRefreshView()
    {
        return (PullToRefreshView) getParent();
    }

    @Override
    public void onViewPositionChanged(PullToRefreshView view)
    {

    }

    @Override
    public boolean canRefresh(int scrollDistance)
    {
        return scrollDistance >= getMeasuredHeight();
    }

    @Override
    public int getRefreshHeight()
    {
        return getMeasuredHeight();
    }

    public static BaseLoadingView getInstanceByClassName(String className, Context context)
    {
        if (TextUtils.isEmpty(className) || context == null)
        {
            return null;
        }

        try
        {
            Class clazz = Class.forName(className);
            Constructor constructor = clazz.getConstructor(Context.class);
            return (BaseLoadingView) constructor.newInstance(context);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
