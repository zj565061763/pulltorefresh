package com.fanwe.library.pulltorefresh;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/6/27.
 */

public class SDSimplePullToRefreshLoadingView extends SDPullToRefreshLoadingView
{
    public SDSimplePullToRefreshLoadingView(@NonNull Context context)
    {
        super(context);
        init();
    }

    public SDSimplePullToRefreshLoadingView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public SDSimplePullToRefreshLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private TextView tv_content;

    private void init()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.view_simple_pull_to_refresh_loading, this, true);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content.setTextColor(Color.GRAY);
    }

    public TextView getTextViewContent()
    {
        return tv_content;
    }

    @Override
    public void onStateChanged(SDPullToRefreshView.State state)
    {
        switch (state)
        {
            case RESET:
            case PULL_TO_REFRESH:
                if (getLoadingViewType() == SDPullToRefreshView.LoadingViewType.HEADER)
                {
                    tv_content.setText(getResources().getString(R.string.state_pull_to_refresh_header));
                } else if (getLoadingViewType() == SDPullToRefreshView.LoadingViewType.FOOTER)
                {
                    tv_content.setText(getResources().getString(R.string.state_pull_to_refresh_footer));
                }
                break;
            case RELEASE_TO_REFRESH:
                if (getLoadingViewType() == SDPullToRefreshView.LoadingViewType.HEADER)
                {
                    tv_content.setText(getResources().getString(R.string.state_release_to_refresh_header));
                } else if (getLoadingViewType() == SDPullToRefreshView.LoadingViewType.FOOTER)
                {
                    tv_content.setText(getResources().getString(R.string.state_release_to_refresh_footer));
                }
                break;
            case REFRESHING:
                if (getLoadingViewType() == SDPullToRefreshView.LoadingViewType.HEADER)
                {
                    tv_content.setText(getResources().getString(R.string.state_refreshing_header));
                } else if (getLoadingViewType() == SDPullToRefreshView.LoadingViewType.FOOTER)
                {
                    tv_content.setText(getResources().getString(R.string.state_refreshing_footer));
                }
                break;
        }
    }
}
