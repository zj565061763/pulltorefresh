package com.sd.demo.pulltorefresh.utils;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;

import com.fanwe.library.pulltorefresh.SDPullToRefreshView;

/**
 * Created by Administrator on 2017/7/3.
 */

public class DemoUtil
{

    /**
     * 到达底部自动加载
     *
     * @param view
     */
    public static void handleAutoRefreshingFromFooter(final SDPullToRefreshView view)
    {
        View refreshView = view.getRefreshView();
        if (refreshView instanceof AbsListView)
        {
            AbsListView listView = (AbsListView) refreshView;
            listView.setOnScrollListener(new AbsListView.OnScrollListener()
            {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int scrollState)
                {
                    if (scrollState == SCROLL_STATE_IDLE)
                    {
                        if (!ViewCompat.canScrollVertically(absListView, 1))
                        {
                            view.startRefreshingFromFooter();
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                {
                }
            });
        } else if (refreshView instanceof RecyclerView)
        {
            RecyclerView recyclerView = (RecyclerView) refreshView;
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
            {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState)
                {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (RecyclerView.SCROLL_STATE_IDLE == newState)
                    {
                        if (!ViewCompat.canScrollVertically(recyclerView, 1))
                        {
                            view.startRefreshingFromFooter();
                        }
                    }
                }
            });
        }
    }
}
