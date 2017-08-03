package com.fanwe.library.pulltorefresh;

import android.view.View;

import com.fanwe.library.pulltorefresh.loadingview.SDPullToRefreshLoadingView;

/**
 * Created by Administrator on 2017/6/28.
 */

public interface ISDPullToRefreshView
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
    void setPullCondition(IPullCondition pullCondition);

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
     * 设置是否判断拖动角度，默认判断拖动方向与y轴的夹角必须小于40度
     *
     * @param checkDragDegree true-判断拖动角度，false-不判断
     */
    void setCheckDragDegree(boolean checkDragDegree);

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
     * 设置RefreshView
     *
     * @param refreshView
     */
    void setRefreshView(View refreshView);

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
        REFRESH_FINISH,
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
         * @param newState
         * @param oldState
         * @param view
         */
        void onStateChanged(State newState, State oldState, SDPullToRefreshView view);
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

    interface IPullCondition
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

    /**
     * 加载view基类接口
     */
    interface IPullToRefreshLoadingView extends OnStateChangedCallback, OnViewPositionChangedCallback
    {
        /**
         * 返回view处于刷新中的时候需要显示的高度（默认view的测量高度）
         *
         * @return
         */
        int getRefreshHeight();

        /**
         * 返回是否可以触发刷新（默认大于等于view的测量高度的时候触发刷新）
         *
         * @param scrollDistance 已经滚动的距离
         * @return
         */
        boolean canRefresh(int scrollDistance);

        /**
         * 返回加载view类型
         *
         * @return
         */
        LoadingViewType getLoadingViewType();

        SDPullToRefreshView getPullToRefreshView();
    }
}
