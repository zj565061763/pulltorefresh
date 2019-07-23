package com.sd.demo.pulltorefresh.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sd.lib.pulltorefresh.FPullToRefreshView;
import com.sd.lib.pulltorefresh.PullToRefreshView;
import com.sd.demo.pulltorefresh.R;
import com.sd.demo.pulltorefresh.loadingview.google.GoogleLoadingView;

public class ButtonActivity extends AppCompatActivity
{
    private FPullToRefreshView view_pull;
    private Button btn_overlay, btn_stop, btn_stop_success, btn_stop_failure;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);
        view_pull = findViewById(R.id.view_pull);
        btn_overlay = findViewById(R.id.btn_overlay);
        btn_stop = findViewById(R.id.btn_stop);
        btn_stop_success = findViewById(R.id.btn_stop_success);
        btn_stop_failure = findViewById(R.id.btn_stop_failure);

        btn_stop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //停止刷新
                view_pull.stopRefreshing();
            }
        });
        btn_stop_success.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //停止刷新->成功
                view_pull.stopRefreshingWithResult(true);
            }
        });
        btn_stop_failure.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //停止刷新->失败
                view_pull.stopRefreshingWithResult(false);
            }
        });
        btn_overlay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                view_pull.setOverLayMode(!view_pull.isOverLayMode());
                updateBtnMode();
            }
        });

        view_pull.setDebug(true);
        view_pull.setHeaderView(new GoogleLoadingView(this));
        view_pull.setOnRefreshCallback(new PullToRefreshView.OnRefreshCallback()
        {
            @Override
            public void onRefreshingFromHeader(PullToRefreshView view)
            {
            }

            @Override
            public void onRefreshingFromFooter(PullToRefreshView view)
            {
            }
        });
        updateBtnMode();
    }

    private void updateBtnMode()
    {
        btn_overlay.setText(view_pull.isOverLayMode() ? "覆盖模式" : "拖拽模式");
    }

    public static void changeViewHeight(View view, int changeHeight)
    {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = view.getHeight() + changeHeight;
        view.setLayoutParams(params);
    }

}
