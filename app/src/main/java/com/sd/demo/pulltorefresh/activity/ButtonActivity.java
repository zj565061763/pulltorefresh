package com.sd.demo.pulltorefresh.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fanwe.library.activity.SDBaseActivity;
import com.fanwe.library.pulltorefresh.SDPullToRefreshView;
import com.sd.demo.pulltorefresh.R;

public class ButtonActivity extends SDBaseActivity
{
    private SDPullToRefreshView view_pull;
    private Button btn_stop_refresh;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_button);
        view_pull = (SDPullToRefreshView) findViewById(R.id.view_pull);
        btn_stop_refresh = (Button) findViewById(R.id.btn_stop_refresh);

        btn_stop_refresh.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                view_pull.stopRefreshing();
            }
        });

        view_pull.setDebug(true);
        view_pull.startRefreshingFromHeader();
    }

}
