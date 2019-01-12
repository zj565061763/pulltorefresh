package com.sd.lib.pulltorefresh.loadingview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.sd.lib.pulltorefresh.PullToRefreshView;

public class SimpleTextLoadingView extends BaseLoadingView
{
    public SimpleTextLoadingView(Context context)
    {
        super(context);
        init();
    }

    public SimpleTextLoadingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public SimpleTextLoadingView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private TextView tv_content;

    private void init()
    {
        LayoutInflater.from(getContext()).inflate(com.sd.lib.pulltorefresh.R.layout.lib_ptr_view_simple_text_loading, this, true);
        tv_content = findViewById(com.sd.lib.pulltorefresh.R.id.tv_content);
    }

    public TextView getTextView()
    {
        return tv_content;
    }

    @Override
    public void onStateChanged(PullToRefreshView.State oldState, PullToRefreshView.State newState, PullToRefreshView view)
    {
        switch (newState)
        {
            case RESET:
            case PULL_TO_REFRESH:
                if (this == getPullToRefreshView().getHeaderView())
                {
                    getTextView().setText(getResources().getString(com.sd.lib.pulltorefresh.R.string.lib_ptr_state_pull_to_refresh_header));
                } else if (this == getPullToRefreshView().getFooterView())
                {
                    getTextView().setText(getResources().getString(com.sd.lib.pulltorefresh.R.string.lib_ptr_state_pull_to_refresh_footer));
                }
                break;
            case RELEASE_TO_REFRESH:
                if (this == getPullToRefreshView().getHeaderView())
                {
                    getTextView().setText(getResources().getString(com.sd.lib.pulltorefresh.R.string.lib_ptr_state_release_to_refresh_header));
                } else if (this == getPullToRefreshView().getFooterView())
                {
                    getTextView().setText(getResources().getString(com.sd.lib.pulltorefresh.R.string.lib_ptr_state_release_to_refresh_footer));
                }
                break;
            case REFRESHING:
                if (this == getPullToRefreshView().getHeaderView())
                {
                    getTextView().setText(getResources().getString(com.sd.lib.pulltorefresh.R.string.lib_ptr_state_refreshing_header));
                } else if (this == getPullToRefreshView().getFooterView())
                {
                    getTextView().setText(getResources().getString(com.sd.lib.pulltorefresh.R.string.lib_ptr_state_refreshing_footer));
                }
                break;
            case REFRESHING_SUCCESS:
                if (this == getPullToRefreshView().getHeaderView())
                {
                    getTextView().setText(getResources().getString(com.sd.lib.pulltorefresh.R.string.lib_ptr_state_refreshing_success_header));
                } else if (this == getPullToRefreshView().getFooterView())
                {
                    getTextView().setText(getResources().getString(com.sd.lib.pulltorefresh.R.string.lib_ptr_state_refreshing_success_footer));
                }
                break;
            case REFRESHING_FAILURE:
                if (this == getPullToRefreshView().getHeaderView())
                {
                    getTextView().setText(getResources().getString(com.sd.lib.pulltorefresh.R.string.lib_ptr_state_refreshing_failure_header));
                } else if (this == getPullToRefreshView().getFooterView())
                {
                    getTextView().setText(getResources().getString(com.sd.lib.pulltorefresh.R.string.lib_ptr_state_refreshing_failure_footer));
                }
                break;
            case FINISH:
                if (oldState == PullToRefreshView.State.REFRESHING)
                {
                    if (this == getPullToRefreshView().getHeaderView())
                    {
                        getTextView().setText(getResources().getString(com.sd.lib.pulltorefresh.R.string.lib_ptr_state_pull_to_refresh_header));
                    } else if (this == getPullToRefreshView().getFooterView())
                    {
                        getTextView().setText(getResources().getString(com.sd.lib.pulltorefresh.R.string.lib_ptr_state_pull_to_refresh_footer));
                    }
                }
        }
    }
}
