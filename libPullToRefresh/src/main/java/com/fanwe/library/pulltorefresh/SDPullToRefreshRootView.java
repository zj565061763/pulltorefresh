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

    private ISDPullToRefreshView mPullToRefreshView;

    private LinearLayout ll_header;
    private LinearLayout ll_content;
    private LinearLayout ll_footer;

    private SDPullToRefreshLoadingView mHeaderView;
    private View mContentView;
    private SDPullToRefreshLoadingView mFooterView;

    private void init()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.view_pull_to_refresh_root, this, true);

        ll_header = (LinearLayout) findViewById(R.id.ll_header);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        ll_footer = (LinearLayout) findViewById(R.id.ll_footer);
    }

    public void setPullToRefreshView(ISDPullToRefreshView pullToRefreshView)
    {
        mPullToRefreshView = pullToRefreshView;
    }

    public ISDPullToRefreshView getPullToRefreshView()
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
            headerView.setLoadingViewType(SDPullToRefreshView.LoadingViewType.HEADER);
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
     * 设置内容view
     *
     * @param contentView
     */
    public void setContentView(View contentView)
    {
        if (contentView != null)
        {
            ll_content.removeAllViews();
            mContentView = contentView;
            ll_content.addView(contentView);
        }
    }

    /**
     * 返回内容view
     *
     * @return
     */
    public View getContentView()
    {
        return mContentView;
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
            footerView.setLoadingViewType(SDPullToRefreshView.LoadingViewType.FOOTER);
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
        return mHeaderView.getHeight();
    }

    /**
     * 获得FooterView高度
     *
     * @return
     */
    public int getFooterHeight()
    {
        return mFooterView.getHeight();
    }

    @Override
    public void onStateChanged(ISDPullToRefreshView.State state, SDPullToRefreshView view)
    {
        if (getPullToRefreshView().getLastDirection() == ISDPullToRefreshView.Direction.HEADER_TO_FOOTER)
        {
            mHeaderView.onStateChanged(state, view);
        } else if (getPullToRefreshView().getLastDirection() == ISDPullToRefreshView.Direction.FOOTER_TO_HEADER)
        {
            mFooterView.onStateChanged(state, view);
        }
    }

    @Override
    public void onViewPositionChanged(SDPullToRefreshView view)
    {
        if (getPullToRefreshView().getLastDirection() == ISDPullToRefreshView.Direction.HEADER_TO_FOOTER)
        {
            mHeaderView.onViewPositionChanged(view);
        } else if (getPullToRefreshView().getLastDirection() == ISDPullToRefreshView.Direction.FOOTER_TO_HEADER)
        {
            mFooterView.onViewPositionChanged(view);
        }
    }
}
