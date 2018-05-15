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

import com.fanwe.lib.pulltorefresh.PullToRefreshView;

/**
 * 加载view
 */
public interface LoadingView extends PullToRefreshView.OnViewPositionChangedCallback,
        PullToRefreshView.OnStateChangedCallback
{
    /**
     * 返回是否满足刷新条件（默认滚动距离大于等于view的测量高度的时候满足）
     *
     * @param scrollDistance 已经滚动的距离
     * @return
     */
    boolean canRefresh(int scrollDistance);

    /**
     * 返回view处于刷新中的时候需要显示的高度（默认view的测量高度）
     *
     * @return
     */
    int getRefreshingHeight();

    /**
     * 默认返回当前view的parent对象
     *
     * @return
     */
    PullToRefreshView getPullToRefreshView();
}
