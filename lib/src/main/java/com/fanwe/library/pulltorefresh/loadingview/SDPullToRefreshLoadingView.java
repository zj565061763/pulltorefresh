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
package com.fanwe.library.pulltorefresh.loadingview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.fanwe.library.pulltorefresh.ISDPullToRefreshView;
import com.fanwe.library.pulltorefresh.SDPullToRefreshView;

import java.lang.reflect.Constructor;

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
        return scrollDistance >= getMeasuredHeight();
    }

    @Override
    public int getRefreshHeight()
    {
        return getMeasuredHeight();
    }

    public static SDPullToRefreshLoadingView getInstanceByClassName(String className, Context context)
    {
        if (TextUtils.isEmpty(className) || context == null)
        {
            return null;
        }

        try
        {
            Class clazz = Class.forName(className);
            Constructor constructor = clazz.getConstructor(Context.class);
            return (SDPullToRefreshLoadingView) constructor.newInstance(context);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
