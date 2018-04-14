package com.sd.demo.pulltorefresh.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanwe.lib.adapter.FSimpleRecyclerAdapter;
import com.fanwe.lib.adapter.viewholder.FRecyclerViewHolder;
import com.fanwe.lib.pulltorefresh.FIPullToRefreshView;
import com.fanwe.lib.pulltorefresh.FPullToRefreshView;
import com.fanwe.library.activity.SDBaseActivity;
import com.fanwe.library.view.SDRecyclerView;
import com.sd.demo.pulltorefresh.R;
import com.sd.demo.pulltorefresh.loadingview.CustomPullToRefreshLoadingView;
import com.sd.demo.pulltorefresh.model.DataModel;

public class RecyclerViewActivity extends SDBaseActivity
{
    private FPullToRefreshView view_pull;
    private SDRecyclerView mRecyclerView;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_recyclerview);
        view_pull = findViewById(R.id.view_pull);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(mAdapter);

        view_pull.setDebug(true);
        view_pull.setHeaderView(new CustomPullToRefreshLoadingView(this)); //自定义HeaderView
        view_pull.setOnRefreshCallback(new FIPullToRefreshView.OnRefreshCallback()
        {
            @Override
            public void onRefreshingFromHeader(final FPullToRefreshView view)
            {
                //头部刷新回调
                view.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mAdapter.getDataHolder().setData(DataModel.getListModel(3));
                        view.stopRefreshing();
                    }
                }, 1000);
            }

            @Override
            public void onRefreshingFromFooter(final FPullToRefreshView view)
            {
                //底部加载回调
                view.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mAdapter.getDataHolder().appendData(DataModel.getListModel(3));
                        view.stopRefreshing();
                    }
                }, 1000);
            }
        });
        view_pull.startRefreshingFromHeader();
    }

    private FSimpleRecyclerAdapter<DataModel> mAdapter = new FSimpleRecyclerAdapter<DataModel>(this)
    {
        @Override
        public int getLayoutId(ViewGroup parent, int viewType)
        {
            return R.layout.item_textview;
        }

        @Override
        public void onBindData(FRecyclerViewHolder holder, int position, DataModel model)
        {
            TextView tv_content = (TextView) holder.get(R.id.tv_content);
            tv_content.setText(String.valueOf(model.getName()));
        }
    };
}
