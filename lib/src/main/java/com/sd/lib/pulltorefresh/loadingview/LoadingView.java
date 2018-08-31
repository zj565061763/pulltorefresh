package com.sd.lib.pulltorefresh.loadingview;

import com.sd.lib.pulltorefresh.PullToRefreshView;

/**
 * 加载view
 */
public interface LoadingView extends PullToRefreshView.OnViewPositionChangeCallback,
        PullToRefreshView.OnStateChangeCallback
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
