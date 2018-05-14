package com.sd.demo.pulltorefresh.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.fanwe.lib.adapter.FSimpleAdapter;
import com.fanwe.lib.pulltorefresh.FPullToRefreshView;
import com.fanwe.lib.pulltorefresh.PullToRefreshView;
import com.fanwe.library.activity.SDBaseActivity;
import com.sd.demo.pulltorefresh.R;
import com.sd.demo.pulltorefresh.loadingview.CustomPullToRefreshLoadingView;
import com.sd.demo.pulltorefresh.model.DataModel;

public class ListViewActivity extends SDBaseActivity
{
    private FPullToRefreshView view_pull;
    private ListView mListView;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_listview);
        view_pull = findViewById(R.id.view_pull);
        mListView = findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);

        view_pull.setDebug(true);
        view_pull.setFooterView(new CustomPullToRefreshLoadingView(this)); //自定义FooterView
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
                        mAdapter.getDataHolder().setData(DataModel.getListModel(20));
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
                        mAdapter.getDataHolder().appendData(DataModel.getListModel(10));
                        view.stopRefreshing();
                    }
                }, 1000);
            }
        });
        view_pull.startRefreshingFromHeader();
    }

    private FSimpleAdapter<DataModel> mAdapter = new FSimpleAdapter<DataModel>(this)
    {
        @Override
        public int getLayoutId(int position, View convertView, ViewGroup parent)
        {
            return R.layout.item_textview;
        }

        @Override
        public void onBindData(int position, View convertView, ViewGroup parent, DataModel model)
        {
            TextView tv_content = get(R.id.tv_content, convertView);
            tv_content.setText(String.valueOf(model.getName()));
        }
    };

}
