package com.sd.demo.pulltorefresh.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fanwe.library.activity.SDBaseActivity;
import com.fanwe.library.pulltorefresh.ISDPullToRefreshView;
import com.fanwe.library.pulltorefresh.SDPullToRefreshView;
import com.fanwe.library.utils.LogUtil;
import com.sd.demo.pulltorefresh.R;
import com.sd.demo.pulltorefresh.view.CustomPullToRefreshLoadingView;

public class ButtonActivity extends SDBaseActivity
{
    private SDPullToRefreshView view_pull;
    private Button btn;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_button);
        view_pull = (SDPullToRefreshView) findViewById(R.id.view_pull);
        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                view_pull.setOverLayMode(!view_pull.isOverLayMode());
                btn.setText(view_pull.isOverLayMode() ? "覆盖模式" : "拖拽模式");
            }
        });

        view_pull.setDebug(true);
        view_pull.setHeaderView(new CustomPullToRefreshLoadingView(this));
        view_pull.setOverLayMode(true); //设置LoadingView是覆盖模式，还是拖拽模式
        view_pull.setOnRefreshCallback(new ISDPullToRefreshView.OnRefreshCallback()
        {
            @Override
            public void onRefreshingFromHeader(SDPullToRefreshView view)
            {
                stopRefreshingDelayed(1000);
            }

            @Override
            public void onRefreshingFromFooter(SDPullToRefreshView view)
            {
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
                ViewGroup.LayoutParams params = btn.getLayoutParams();
                params.height = btn.getHeight() + btn.getHeight() / 3;
                btn.setLayoutParams(params);

                view_pull.stopRefreshing();
            }
        }, delay);
    }

}
