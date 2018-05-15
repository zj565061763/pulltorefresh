/*
 * Copyright (C) 2017 zhengjun, fanwe (http://www.fanwe.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fanwe.lib.pulltorefresh.loadingview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.fanwe.lib.pulltorefresh.PullToRefreshView;
import com.fanwe.lib.pulltorefresh.R;

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
        LayoutInflater.from(getContext()).inflate(R.layout.lib_ptr_view_simple_text_loading, this, true);
        tv_content = findViewById(R.id.tv_content);
    }

    public TextView getTextView()
    {
        return tv_content;
    }

    @Override
    public void onStateChanged(PullToRefreshView.State newState, PullToRefreshView.State oldState, PullToRefreshView view)
    {
        switch (newState)
        {
            case RESET:
            case PULL_TO_REFRESH:
                if (this == getPullToRefreshView().getHeaderView())
                {
                    getTextView().setText(getResources().getString(R.string.lib_ptr_state_pull_to_refresh_header));
                } else if (this == getPullToRefreshView().getFooterView())
                {
                    getTextView().setText(getResources().getString(R.string.lib_ptr_state_pull_to_refresh_footer));
                }
                break;
            case RELEASE_TO_REFRESH:
                if (this == getPullToRefreshView().getHeaderView())
                {
                    getTextView().setText(getResources().getString(R.string.lib_ptr_state_release_to_refresh_header));
                } else if (this == getPullToRefreshView().getFooterView())
                {
                    getTextView().setText(getResources().getString(R.string.lib_ptr_state_release_to_refresh_footer));
                }
                break;
            case REFRESHING:
                if (this == getPullToRefreshView().getHeaderView())
                {
                    getTextView().setText(getResources().getString(R.string.lib_ptr_state_refreshing_header));
                } else if (this == getPullToRefreshView().getFooterView())
                {
                    getTextView().setText(getResources().getString(R.string.lib_ptr_state_refreshing_footer));
                }
                break;
            case REFRESHING_SUCCESS:
                if (this == getPullToRefreshView().getHeaderView())
                {
                    getTextView().setText(getResources().getString(R.string.lib_ptr_state_refreshing_success_header));
                } else if (this == getPullToRefreshView().getFooterView())
                {
                    getTextView().setText(getResources().getString(R.string.lib_ptr_state_refreshing_success_footer));
                }
                break;
            case REFRESHING_FAILURE:
                if (this == getPullToRefreshView().getHeaderView())
                {
                    getTextView().setText(getResources().getString(R.string.lib_ptr_state_refreshing_failure_header));
                } else if (this == getPullToRefreshView().getFooterView())
                {
                    getTextView().setText(getResources().getString(R.string.lib_ptr_state_refreshing_failure_footer));
                }
                break;
            case FINISH:
                if (oldState == PullToRefreshView.State.REFRESHING)
                {
                    if (this == getPullToRefreshView().getHeaderView())
                    {
                        getTextView().setText(getResources().getString(R.string.lib_ptr_state_pull_to_refresh_header));
                    } else if (this == getPullToRefreshView().getFooterView())
                    {
                        getTextView().setText(getResources().getString(R.string.lib_ptr_state_pull_to_refresh_footer));
                    }
                }
        }
    }
}
