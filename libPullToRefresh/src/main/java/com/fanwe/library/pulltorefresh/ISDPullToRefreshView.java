package com.fanwe.library.pulltorefresh;

/**
 * Created by Administrator on 2017/6/28.
 */

public interface ISDPullToRefreshView
{
    /**
     * 默认的拖动消耗比例
     */
    float DEFAULT_COMSUME_SCROLL_PERCENT = 0.6f;

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
     * 设置拖动的时候要消耗的拖动距离比例，默认是0.6
     *
     * @param comsumeScrollPercent [0-1]
     */
    void setComsumeScrollPercent(float comsumeScrollPercent);

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
     * 返回HeaderView
     *
     * @return
     */
    SDPullToRefreshLoadingView getHeaderView();

    /**
     * 设置HeaderView
     *
     * @param headerView
     */
    void setHeaderView(SDPullToRefreshLoadingView headerView);

    /**
     * 返回FooterView
     *
     * @return
     */
    SDPullToRefreshLoadingView getFooterView();

    /**
     * 设置FooterView
     *
     * @param footerView
     */
    void setFooterView(SDPullToRefreshLoadingView footerView);

    /**
     * 返回当前拖动方向
     *
     * @return
     */
    Direction getDirection();

    /**
     * 返回最后一次拖动的方向
     *
     * @return
     */
    Direction getLastDirection();

    /**
     * 返回滚动的距离
     *
     * @return
     */
    int getScrollDistance();

    enum State
    {
        RESET,
        PULL_TO_REFRESH,
        RELEASE_TO_REFRESH,
        REFRESHING,
    }

    enum Direction
    {
        NONE,
        HEADER_TO_FOOTER,
        FOOTER_TO_HEADER,
    }

    enum Mode
    {
        BOTH,
        PULL_FROM_HEADER,
        PULL_FROM_FOOTER,
        DISABLE,
    }

    enum LoadingViewType
    {
        HEADER,
        FOOTER,
    }

    interface OnStateChangedCallback
    {
        /**
         * 状态变化回调
         *
         * @param state
         */
        void onStateChanged(State state);
    }

    interface OnRefreshCallback
    {
        /**
         * 下拉触发刷新回调
         *
         * @param view
         */
        void onRefreshingFromHeader(SDPullToRefreshView view);

        /**
         * 上拉触发刷新回调
         *
         * @param view
         */
        void onRefreshingFromFooter(SDPullToRefreshView view);
    }

    interface OnViewPositionChangedCallback
    {
        /**
         * view位置变化回调
         *
         * @param view
         */
        void onViewPositionChanged(SDPullToRefreshView view);
    }

    interface IPullToRefreshLoadingView extends OnStateChangedCallback, OnViewPositionChangedCallback
    {
        /**
         * 返回触发刷新条件的高度
         *
         * @return
         */
        int getRefreshHeight();

        LoadingViewType getLoadingViewType();

        void setLoadingViewType(LoadingViewType loadingViewType);
    }
}
