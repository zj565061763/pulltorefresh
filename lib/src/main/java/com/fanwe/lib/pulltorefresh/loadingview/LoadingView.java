package com.fanwe.lib.pulltorefresh.loadingview;

import com.fanwe.lib.pulltorefresh.FPullToRefreshView;

/**
 * 加载view
 */
public interface LoadingView
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
     * 返回当前view是否是下拉刷新控件的HeaderView
     *
     * @return
     */
    boolean isHeaderView();

    /**
     * 返回当前view是否是下拉刷新控件的FooterView
     *
     * @return
     */
    boolean isFooterView();

    FPullToRefreshView getPullToRefreshView();
}
