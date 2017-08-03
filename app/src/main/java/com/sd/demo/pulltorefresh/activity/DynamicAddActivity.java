package com.sd.demo.pulltorefresh.activity;

import android.os.Bundle;

import com.fanwe.library.activity.SDBaseActivity;
import com.fanwe.library.pulltorefresh.ISDPullToRefreshView;
import com.fanwe.library.pulltorefresh.SDPullToRefreshView;
import com.sd.demo.pulltorefresh.AppPullToRefreshView;
import com.sd.demo.pulltorefresh.R;

/**
 * Created by Administrator on 2017/8/3.
 */

public class DynamicAddActivity extends SDBaseActivity
{
    private AppPullToRefreshView mPullToRefreshView;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_dynamic_add);

        mPullToRefreshView = new AppPullToRefreshView(this);
        mPullToRefreshView.setRefreshView(findViewById(R.id.btn));
        mPullToRefreshView.setOnRefreshCallback(new ISDPullToRefreshView.OnRefreshCallback()
        {
            @Override
            public void onRefreshingFromHeader(final SDPullToRefreshView view)
            {
                view.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        view.stopRefreshing();
                    }
                }, 1500);
            }

            @Override
            public void onRefreshingFromFooter(final SDPullToRefreshView view)
            {
                view.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        view.stopRefreshing();
                    }
                }, 1500);
            }
        });
    }
}
