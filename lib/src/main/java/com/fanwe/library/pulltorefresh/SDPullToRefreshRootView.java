package com.fanwe.library.pulltorefresh;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;


/**
 * Created by Administrator on 2017/6/27.
 */

class SDPullToRefreshRootView extends LinearLayout implements
        SDPullToRefreshView.OnStateChangedCallback,
        ISDPullToRefreshView.OnViewPositionChangedCallback
{
    public SDPullToRefreshRootView(Context context)
    {
        super(context);
        init();
    }

    public SDPullToRefreshRootView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public SDPullToRefreshRootView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private SDPullToRefreshView mPullToRefreshView;

    private LinearLayout ll_header;
    private LinearLayout ll_refresh;
    private LinearLayout ll_footer;

    private SDPullToRefreshLoadingView mHeaderView;
    private View mRefreshView;
    private SDPullToRefreshLoadingView mFooterView;

    private void init()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.view_pull_to_refresh_root, this, true);

        ll_header = (LinearLayout) findViewById(R.id.ll_header);
        ll_refresh = (LinearLayout) findViewById(R.id.ll_refresh);
        ll_footer = (LinearLayout) findViewById(R.id.ll_footer);
    }

    public void setPullToRefreshView(SDPullToRefreshView pullToRefreshView)
    {
        mPullToRefreshView = pullToRefreshView;
    }

    public SDPullToRefreshView getPullToRefreshView()
    {
        return mPullToRefreshView;
    }

    /**
     * 设置HeaderView
     *
     * @param headerView
     */
    public void setHeaderView(SDPullToRefreshLoadingView headerView)
    {
        if (headerView != null)
        {
            ll_header.removeAllViews();
            mHeaderView = headerView;
            headerView.setLoadingViewType(ISDPullToRefreshView.LoadingViewType.HEADER);
            headerView.setPullToRefreshView(getPullToRefreshView());
            ll_header.addView(headerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * 返回HeaderView
     *
     * @return
     */
    public SDPullToRefreshLoadingView getHeaderView()
    {
        return mHeaderView;
    }

    /**
     * 设置要支持刷新的view
     *
     * @param refreshView
     */
    public void setRefreshView(View refreshView)
    {
        if (refreshView != null)
        {
            ll_refresh.removeAllViews();
            mRefreshView = refreshView;
            ll_refresh.addView(refreshView);
        }
    }

    /**
     * 返回要支持刷新的view
     *
     * @return
     */
    public View getRefreshView()
    {
        return mRefreshView;
    }

    /**
     * 设置FooterView
     *
     * @param footerView
     */
    public void setFooterView(SDPullToRefreshLoadingView footerView)
    {
        if (footerView != null)
        {
            ll_footer.removeAllViews();
            mFooterView = footerView;
            footerView.setLoadingViewType(ISDPullToRefreshView.LoadingViewType.FOOTER);
            footerView.setPullToRefreshView(getPullToRefreshView());
            ll_footer.addView(footerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * 返回FooterView
     *
     * @return
     */
    public SDPullToRefreshLoadingView getFooterView()
    {
        return mFooterView;
    }

    /**
     * 返回触发Header刷新的高度
     *
     * @return
     */
    public int getHeaderRefreshHeight()
    {
        return mHeaderView.getRefreshHeight();
    }

    /**
     * 返回触发Footer刷新的高度
     *
     * @return
     */
    public int getFooterRefreshHeight()
    {
        return mFooterView.getRefreshHeight();
    }

    /**
     * 返回HeaderView高度
     *
     * @return
     */
    public int getHeaderHeight()
    {
        return mHeaderView.getMeasuredHeight();
    }

    /**
     * 获得FooterView高度
     *
     * @return
     */
    public int getFooterHeight()
    {
        return mFooterView.getMeasuredHeight();
    }

    @Override
    public void onStateChanged(ISDPullToRefreshView.State state, SDPullToRefreshView view)
    {
        if (getPullToRefreshView().getDirection() == ISDPullToRefreshView.Direction.FROM_HEADER)
        {
            mHeaderView.onStateChanged(state, view);
        } else if (getPullToRefreshView().getDirection() == ISDPullToRefreshView.Direction.FROM_FOOTER)
        {
            mFooterView.onStateChanged(state, view);
        }
    }

    @Override
    public void onViewPositionChanged(SDPullToRefreshView view)
    {
        if (getPullToRefreshView().getDirection() == ISDPullToRefreshView.Direction.FROM_HEADER)
        {
            mHeaderView.onViewPositionChanged(view);
        } else if (getPullToRefreshView().getDirection() == ISDPullToRefreshView.Direction.FROM_FOOTER)
        {
            mFooterView.onViewPositionChanged(view);
        }
    }
}
