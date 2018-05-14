package com.sd.demo.pulltorefresh.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.fanwe.lib.pulltorefresh.BasePullToRefreshView;
import com.fanwe.lib.pulltorefresh.PullToRefreshView;
import com.fanwe.lib.pulltorefresh.FPullToRefreshView;
import com.fanwe.library.activity.SDBaseActivity;
import com.sd.demo.pulltorefresh.R;

public class ScrollViewActivity extends SDBaseActivity
{
    private static final String TAG = "ScrollViewActivity";

    private FPullToRefreshView view_pull;
    private Button btn;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_scrollview);
        view_pull = (FPullToRefreshView) findViewById(R.id.view_pull);
        btn = (Button) findViewById(R.id.btn);

        view_pull.setDebug(true);
        view_pull.setOnStateChangedCallback(new PullToRefreshView.OnStateChangedCallback()
        {
            @Override
            public void onStateChanged(PullToRefreshView.State newState, PullToRefreshView.State oldState, BasePullToRefreshView view)
            {
                //状态变化回调
                btn.setText(String.valueOf(view.getDirection()) + "->" + String.valueOf(newState));
            }
        });
        view_pull.setOnViewPositionChangedCallback(new PullToRefreshView.OnViewPositionChangedCallback()
        {
            @Override
            public void onViewPositionChanged(BasePullToRefreshView view)
            {
                //view被拖动回调
                Log.i(TAG, "onViewPositionChanged getScrollDistance:" + view.getScrollDistance());
            }
        });
        view_pull.setOnRefreshCallback(new PullToRefreshView.OnRefreshCallback()
        {
            @Override
            public void onRefreshingFromHeader(final BasePullToRefreshView view)
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
            public void onRefreshingFromFooter(final BasePullToRefreshView view)
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
