package com.fanwe.library.pulltorefresh.loadingview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.fanwe.library.pulltorefresh.ISDPullToRefreshView;
import com.fanwe.library.pulltorefresh.R;
import com.fanwe.library.pulltorefresh.SDPullToRefreshView;

/**
 * Created by Administrator on 2017/6/27.
 */

public class SimpleTextLoadingView extends SDPullToRefreshLoadingView
{
    public SimpleTextLoadingView(@NonNull Context context)
    {
        super(context);
        init();
    }

    public SimpleTextLoadingView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public SimpleTextLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private TextView tv_content;

    private void init()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.view_simple_text_loading, this, true);
        tv_content = (TextView) findViewById(R.id.tv_content);
    }

    public TextView getTextView()
    {
        return tv_content;
    }

    @Override
    public void onStateChanged(ISDPullToRefreshView.State state, SDPullToRefreshView view)
    {
        switch (state)
        {
            case RESET:
            case PULL_TO_REFRESH:
                if (getLoadingViewType() == ISDPullToRefreshView.LoadingViewType.HEADER)
                {
                    getTextView().setText(getResources().getString(R.string.lib_ptr_state_pull_to_refresh_header));
                } else if (getLoadingViewType() == ISDPullToRefreshView.LoadingViewType.FOOTER)
                {
                    getTextView().setText(getResources().getString(R.string.lib_ptr_state_pull_to_refresh_footer));
                }
                break;
            case RELEASE_TO_REFRESH:
                if (getLoadingViewType() == ISDPullToRefreshView.LoadingViewType.HEADER)
                {
                    getTextView().setText(getResources().getString(R.string.lib_ptr_state_release_to_refresh_header));
                } else if (getLoadingViewType() == ISDPullToRefreshView.LoadingViewType.FOOTER)
                {
                    getTextView().setText(getResources().getString(R.string.lib_ptr_state_release_to_refresh_footer));
                }
                break;
            case REFRESHING:
                if (getLoadingViewType() == ISDPullToRefreshView.LoadingViewType.HEADER)
                {
                    getTextView().setText(getResources().getString(R.string.lib_ptr_state_refreshing_header));
                } else if (getLoadingViewType() == ISDPullToRefreshView.LoadingViewType.FOOTER)
                {
                    getTextView().setText(getResources().getString(R.string.lib_ptr_state_refreshing_footer));
                }
                break;
        }
    }
}
