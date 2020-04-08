package com.sd.demo.pulltorefresh.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.demo.pulltorefresh.R;
import com.sd.demo.pulltorefresh.loadingview.CustomPullToRefreshLoadingView;
import com.sd.demo.pulltorefresh.model.DataModel;
import com.sd.lib.adapter.FSimpleRecyclerAdapter;
import com.sd.lib.adapter.viewholder.FRecyclerViewHolder;
import com.sd.lib.pulltorefresh.FPullToRefreshView;
import com.sd.lib.pulltorefresh.PullToRefreshView;

public class NestedRecyclerViewActivity extends AppCompatActivity
{
    private FPullToRefreshView view_pull;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_recyclerview);
        view_pull = findViewById(R.id.view_pull);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        view_pull.setDebug(true);
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
                        mAdapter.getDataHolder().setData(DataModel.getListModel(30));
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
                        mAdapter.getDataHolder().addData(DataModel.getListModel(30));
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
