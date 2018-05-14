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
package com.fanwe.lib.pulltorefresh.pullcondition;

import android.support.v4.view.ViewCompat;
import android.view.View;

import com.fanwe.lib.pulltorefresh.PullToRefreshView;

import java.lang.ref.WeakReference;

public class SimpleViewPullCondition implements PullToRefreshView.PullCondition
{
    private WeakReference<View> mView;

    public SimpleViewPullCondition(View view)
    {
        mView = new WeakReference<>(view);
    }

    public View getView()
    {
        if (mView != null)
        {
            return mView.get();
        } else
        {
            return null;
        }
    }

    @Override
    public boolean canPullFromHeader()
    {
        if (getView() == null)
        {
            return true;
        } else
        {
            return !ViewCompat.canScrollVertically(getView(), -1);
        }
    }

    @Override
    public boolean canPullFromFooter()
    {
        if (getView() == null)
        {
            return true;
        } else
        {
            return !ViewCompat.canScrollVertically(getView(), 1);
        }
    }
}
