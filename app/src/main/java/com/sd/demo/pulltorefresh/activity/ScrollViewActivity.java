package com.sd.demo.pulltorefresh.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.sd.lib.pulltorefresh.FPullToRefreshView;
import com.sd.lib.pulltorefresh.PullToRefreshView;
import com.sd.demo.pulltorefresh.R;

public class ScrollViewActivity extends AppCompatActivity
{
    private static final String TAG = ScrollViewActivity.class.getSimpleName();

    private FPullToRefreshView view_pull;
    private Button btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollview);
        view_pull = findViewById(R.id.view_pull);
        btn = findViewById(R.id.btn);

        view_pull.setDebug(true);
        view_pull.setOnStateChangeCallback(new PullToRefreshView.OnStateChangeCallback()
        {
            @Override
            public void onStateChanged(PullToRefreshView.State newState, PullToRefreshView.State oldState, PullToRefreshView view)
            {
                //状态变化回调
                btn.setText(String.valueOf(view.getDirection()) + "->" + String.valueOf(newState));
            }
        });
        view_pull.setOnViewPositionChangeCallback(new PullToRefreshView.OnViewPositionChangeCallback()
        {
            @Override
            public void onViewPositionChanged(PullToRefreshView view)
            {
                //view被拖动回调
                Log.i(TAG, "onViewPositionChanged getScrollDistance:" + view.getScrollDistance());
            }
        });
        view_pull.setOnRefreshCallback(new PullToRefreshView.OnRefreshCallback()
        {
            @Override
            public void onRefreshingFromHeader(final PullToRefreshView view)
            {
                //头部刷新回调
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        view.stopRefreshing();
                    }
                }, 1000);
            }

            @Override
            public void onRefreshingFromFooter(final PullToRefreshView view)
            {
                //底部加载回调
                new Handler().postDelayed(new Runnable()
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
