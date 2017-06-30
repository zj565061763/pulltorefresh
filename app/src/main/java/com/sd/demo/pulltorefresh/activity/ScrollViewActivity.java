package com.sd.demo.pulltorefresh.activity;

import android.os.Bundle;
import android.util.Log;

import com.fanwe.library.activity.SDBaseActivity;
import com.fanwe.library.pulltorefresh.ISDPullToRefreshView;
import com.fanwe.library.pulltorefresh.SDPullToRefreshView;
import com.fanwe.library.utils.SDToast;
import com.sd.demo.pulltorefresh.R;

public class ScrollViewActivity extends SDBaseActivity
{
    private static final String TAG = "ScrollViewActivity";

    private SDPullToRefreshView view_pull;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_scrollview);
        view_pull = (SDPullToRefreshView) findViewById(R.id.view_pull);

        view_pull.setDebug(true);
        view_pull.setOnStateChangedCallback(new ISDPullToRefreshView.OnStateChangedCallback()
        {
            @Override
            public void onStateChanged(ISDPullToRefreshView.State state, SDPullToRefreshView view)
            {
                //状态变化回调
                SDToast.showToast(String.valueOf(view.getDirection()) + "->" + String.valueOf(state));
            }
        });
        view_pull.setOnViewPositionChangedCallback(new ISDPullToRefreshView.OnViewPositionChangedCallback()
        {
            @Override
            public void onViewPositionChanged(SDPullToRefreshView view)
            {
                //view被拖动回调
                Log.i(TAG, "onViewPositionChanged getScrollDistance:" + view.getScrollDistance());
            }
        });
        view_pull.setOnRefreshCallback(new ISDPullToRefreshView.OnRefreshCallback()
        {
            @Override
            public void onRefreshingFromHeader(final SDPullToRefreshView view)
            {
                //头部刷新回调
                view.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        view.stopRefreshing();
                    }
                }, 1000);
            }

            @Override
            public void onRefreshingFromFooter(final SDPullToRefreshView view)
            {
                //底部加载回调
                view.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        view.stopRefreshing();
                    }
                }, 1000);
            }
        });
        view_pull.startRefreshingFromFooter();
    }

}
