package com.sd.demo.pulltorefresh.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.fanwe.library.activity.SDBaseActivity;
import com.fanwe.library.adapter.SDSimpleAdapter;
import com.fanwe.library.pulltorefresh.ISDPullToRefreshView;
import com.fanwe.library.pulltorefresh.SDPullToRefreshView;
import com.sd.demo.pulltorefresh.R;
import com.sd.demo.pulltorefresh.loadingview.CustomPullToRefreshLoadingView;
import com.sd.demo.pulltorefresh.model.DataModel;

public class ListViewActivity extends SDBaseActivity
{
    private SDPullToRefreshView view_pull;
    private ListView mListView;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_listview);
        view_pull = (SDPullToRefreshView) findViewById(R.id.view_pull);
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);

        view_pull.setDebug(true);
        view_pull.setDebugTag(getClass().getSimpleName());
        view_pull.setFooterView(new CustomPullToRefreshLoadingView(this)); //自定义FooterView
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
                        mAdapter.updateData(DataModel.getListModel(20));
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
                        mAdapter.appendData(DataModel.getListModel(10));
                        view.stopRefreshing();
                    }
                }, 1000);
            }
        });
        view_pull.startRefreshingFromHeader();
    }

    private SDSimpleAdapter<DataModel> mAdapter = new SDSimpleAdapter<DataModel>(null, this)
    {
        @Override
        public int getLayoutId(int position, View convertView, ViewGroup parent)
        {
            return R.layout.item_textview;
        }

        @Override
        public void bindData(int position, View convertView, ViewGroup parent, DataModel model)
        {
            TextView tv_content = get(R.id.tv_content, convertView);
            tv_content.setText(String.valueOf(model.getName()));
        }
    };

}
