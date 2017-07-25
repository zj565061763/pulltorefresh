package com.sd.demo.pulltorefresh.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fanwe.library.activity.SDBaseActivity;
import com.fanwe.library.pulltorefresh.ISDPullToRefreshView;
import com.fanwe.library.pulltorefresh.SDPullToRefreshView;
import com.sd.demo.pulltorefresh.R;

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
                updateBtnMode();
            }
        });

        view_pull.setDebug(true);
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
        updateBtnMode();
    }

    private void stopRefreshingDelayed(long delay)
    {
        view_pull.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                changeViewHeight(btn, 100);
                view_pull.stopRefreshingWithResult(true);
            }
        }, delay);
    }

    private void updateBtnMode()
    {
        btn.setText(view_pull.isOverLayMode() ? "覆盖模式" : "拖拽模式");
    }

    private void changeViewHeight(View view, int changeHeight)
    {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = view.getHeight() + changeHeight;
        view.setLayoutParams(params);
    }

}
