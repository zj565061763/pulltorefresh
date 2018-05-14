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
package com.fanwe.lib.pulltorefresh;

import android.view.View;

import com.fanwe.lib.pulltorefresh.loadingview.BaseLoadingView;

public interface PullToRefreshView
{
    /**
     * 默认的拖动距离消耗比例
     */
    float DEFAULT_COMSUME_SCROLL_PERCENT = 0.5f;
    /**
     * 默认的显示刷新结果的时长（毫秒）
     */
    int DEFAULT_DURATION_SHOW_REFRESH_RESULT = 600;

    /**
     * 设置刷新模式
     *
     * @param mode
     */
    void setMode(Mode mode);

    /**
     * 设置刷新回调
     *
     * @param onRefreshCallback
     */
    void setOnRefreshCallback(OnRefreshCallback onRefreshCallback);

    /**
     * 设置状态变化回调
     *
     * @param onStateChangedCallback
     */
    void setOnStateChangedCallback(OnStateChangedCallback onStateChangedCallback);

    /**
     * 设置view位置变化回调
     *
     * @param onViewPositionChangedCallback
     */
    void setOnViewPositionChangedCallback(OnViewPositionChangedCallback onViewPositionChangedCallback);

    /**
     * 设置可以触发拖动的条件，设置后当view内部满足拖动，并且此对象也满足条件后才可以触发拖动
     *
     * @param pullCondition
     */
    void setPullCondition(PullCondition pullCondition);

    /**
     * 设置HeaderView和FooterView是否是覆盖的模式（默认false）
     *
     * @param overLayMode
     */
    void setOverLayMode(boolean overLayMode);

    /**
     * 是否是覆盖的模式
     *
     * @return
     */
    boolean isOverLayMode();

    /**
     * 设置拖动的时候要消耗的拖动距离比例，默认{@link #DEFAULT_COMSUME_SCROLL_PERCENT}
     *
     * @param comsumeScrollPercent [0-1]
     */
    void setComsumeScrollPercent(float comsumeScrollPercent);

    /**
     * 设置显示刷新结果的时长，默认{@link #DEFAULT_DURATION_SHOW_REFRESH_RESULT}
     *
     * @param durationShowRefreshResult
     */
    void setDurationShowRefreshResult(int durationShowRefreshResult);

    /**
     * 设置HeaderView处处于刷新状态
     */
    void startRefreshingFromHeader();

    /**
     * 设置Foot而View处处于刷新状态
     */
    void startRefreshingFromFooter();

    /**
     * 停止刷新
     */
    void stopRefreshing();

    /**
     * 停止刷新并展示刷新结果，当状态处于刷新中的时候此方法调用才有效
     *
     * @param success true-刷新成功，false-刷新失败
     */
    void stopRefreshingWithResult(boolean success);

    /**
     * 是否处于刷新中
     *
     * @return
     */
    boolean isRefreshing();

    /**
     * 返回当前的状态
     *
     * @return
     */
    State getState();

    /**
     * 返回当前的刷新模式
     *
     * @return
     */
    Mode getMode();

    /**
     * 返回HeaderView
     *
     * @return
     */
    BaseLoadingView getHeaderView();

    /**
     * 设置HeaderView
     *
     * @param headerView
     */
    void setHeaderView(BaseLoadingView headerView);

    /**
     * 返回FooterView
     *
     * @return
     */
    BaseLoadingView getFooterView();

    /**
     * 设置FooterView
     *
     * @param footerView
     */
    void setFooterView(BaseLoadingView footerView);

    /**
     * 返回要支持刷新的view
     *
     * @return
     */
    View getRefreshView();

    /**
     * 返回当前拖动方向
     *
     * @return
     */
    Direction getDirection();

    /**
     * 返回滚动的距离
     *
     * @return
     */
    int getScrollDistance();

    enum State
    {
        /**
         * 重置
         */
        RESET,
        /**
         * 下拉刷新
         */
        PULL_TO_REFRESH,
        /**
         * 松开刷新
         */
        RELEASE_TO_REFRESH,
        /**
         * 刷新中
         */
        REFRESHING,
        /**
         * 刷新结果，成功
         */
        REFRESH_SUCCESS,
        /**
         * 刷新结果，失败
         */
        REFRESH_FAILURE,
        /**
         * 刷新完成
         */
        FINISH,
    }

    enum Direction
    {
        NONE,
        FROM_HEADER,
        FROM_FOOTER,
    }

    enum Mode
    {
        /**
         * 支持上下拉
         */
        BOTH,
        /**
         * 只支持下拉
         */
        PULL_FROM_HEADER,
        /**
         * 只支持上拉
         */
        PULL_FROM_FOOTER,
        /**
         * 不支持上下拉
         */
        DISABLE,
    }

    interface OnStateChangedCallback
    {
        /**
         * 状态变化回调
         *
         * @param newState
         * @param oldState
         * @param view
         */
        void onStateChanged(State newState, State oldState, BasePullToRefreshView view);
    }

    interface OnRefreshCallback
    {
        /**
         * 下拉触发刷新回调
         *
         * @param view
         */
        void onRefreshingFromHeader(BasePullToRefreshView view);

        /**
         * 上拉触发刷新回调
         *
         * @param view
         */
        void onRefreshingFromFooter(BasePullToRefreshView view);
    }

    interface OnViewPositionChangedCallback
    {
        /**
         * view位置变化回调
         *
         * @param view
         */
        void onViewPositionChanged(BasePullToRefreshView view);
    }

    interface PullCondition
    {
        /**
         * 当View内部可以从Header处拖动条件成立并且这个方法返回true的时候触发拖动
         *
         * @return
         */
        boolean canPullFromHeader();

        /**
         * 当View内部可以从Footer处拖动条件成立并且这个方法返回true的时候触发拖动
         *
         * @return
         */
        boolean canPullFromFooter();
    }
}
