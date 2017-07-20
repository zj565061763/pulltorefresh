package com.sd.demo.pulltorefresh.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanwe.library.activity.SDBaseActivity;
import com.fanwe.library.adapter.SDSimpleRecyclerAdapter;
import com.fanwe.library.adapter.viewholder.SDRecyclerViewHolder;
import com.fanwe.library.pulltorefresh.ISDPullToRefreshView;
import com.fanwe.library.pulltorefresh.SDPullToRefreshView;
import com.fanwe.library.view.SDRecyclerView;
import com.sd.demo.pulltorefresh.R;
import com.sd.demo.pulltorefresh.model.DataModel;
import com.sd.demo.pulltorefresh.view.CustomPullToRefreshLoadingView;

public class RecyclerViewActivity extends SDBaseActivity
{
    private SDPullToRefreshView view_pull;
    private SDRecyclerView mRecyclerView;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_recyclerview);
        view_pull = (SDPullToRefreshView) findViewById(R.id.view_pull);
        mRecyclerView = (SDRecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(mAdapter);

        view_pull.setDebug(true);
        view_pull.setHeaderView(new CustomPullToRefreshLoadingView(this)); //自定义HeaderView
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
                        mAdapter.updateData(DataModel.getListModel(5));
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

    private SDSimpleRecyclerAdapter<DataModel> mAdapter = new SDSimpleRecyclerAdapter<DataModel>(null, this)
    {
        @Override
        public int getLayoutId(ViewGroup parent, int viewType)
        {
            return R.layout.item_textview;
        }

        @Override
        public void onBindData(SDRecyclerViewHolder holder, int position, DataModel model)
        {
            TextView tv_content = (TextView) holder.get(R.id.tv_content);
            tv_content.setText(String.valueOf(model.getName()));
        }
    };

}
