package com.sd.demo.pulltorefresh.activity;

import android.os.Bundle;

import com.fanwe.library.activity.SDBaseActivity;
import com.fanwe.library.pulltorefresh.ISDPullToRefreshView;
import com.fanwe.library.pulltorefresh.SDPullToRefreshView;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDToast;
import com.sd.demo.pulltorefresh.R;

public class ButtonActivity extends SDBaseActivity
{
    private SDPullToRefreshView view_pull;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_button);
        view_pull = (SDPullToRefreshView) findViewById(R.id.view_pull);

        view_pull.setDebug(true);
        view_pull.setOnRefreshCallback(new ISDPullToRefreshView.OnRefreshCallback()
        {
            @Override
            public void onRefreshingFromHeader(SDPullToRefreshView view)
            {
                SDToast.showToast("头部刷新");
                stopRefreshingDelayed(1000);
            }

            @Override
            public void onRefreshingFromFooter(SDPullToRefreshView view)
            {
                SDToast.showToast("尾部刷新");
                stopRefreshingDelayed(1000);
            }
        });
        view_pull.setOnViewPositionChangedCallback(new ISDPullToRefreshView.OnViewPositionChangedCallback()
        {
            @Override
            public void onViewPositionChanged(SDPullToRefreshView view)
            {
                LogUtil.i("scroll distance:" + view.getScrollDistance());
            }
        });
        view_pull.startRefreshingFromHeader();
    }

    private void stopRefreshingDelayed(long delay)
    {
        view_pull.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                view_pull.stopRefreshing();
            }
        }, delay);
    }

}
