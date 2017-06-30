package com.sd.demo.pulltorefresh;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fanwe.library.SDLibrary;
import com.fanwe.library.activity.SDBaseActivity;
import com.fanwe.library.adapter.SDSimpleAdapter;
import com.fanwe.library.adapter.SDSimpleRecyclerAdapter;
import com.fanwe.library.adapter.viewholder.SDRecyclerViewHolder;
import com.fanwe.library.common.SDHandlerManager;
import com.fanwe.library.pulltorefresh.ISDPullToRefreshView;
import com.fanwe.library.pulltorefresh.SDPullToRefreshView;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.view.SDRecyclerView;

public class MainActivity extends SDBaseActivity
{
    private Button btn_stop_refresh;
    private SDPullToRefreshView view_pull;
    private SDRecyclerView rv_content;
    private ListView lv_content;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        SDLibrary.getInstance().init(getApplication());
        setContentView(R.layout.activity_main);
        btn_stop_refresh = (Button) findViewById(R.id.btn_stop_refresh);
        view_pull = (SDPullToRefreshView) findViewById(R.id.view_pull);
        rv_content = (SDRecyclerView) findViewById(R.id.rv_content);
        lv_content = (ListView) findViewById(R.id.lv_content);

        btn_stop_refresh.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                view_pull.stopRefreshing();
            }
        });

        view_pull.setDebug(true);
        view_pull.setHeaderView(new CustomPullToRefreshLoadingView(this));
        view_pull.setOnRefreshCallback(new ISDPullToRefreshView.OnRefreshCallback()
        {
            @Override
            public void onRefreshingFromHeader(final SDPullToRefreshView view)
            {
                SDHandlerManager.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mAdapterRecyclerView.updateData(DataModel.getListModel(30));
                        mAdapterListView.updateData(DataModel.getListModel(30));
                        view.stopRefreshing();
                    }
                }, 1000);
            }

            @Override
            public void onRefreshingFromFooter(final SDPullToRefreshView view)
            {
                SDHandlerManager.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mAdapterRecyclerView.appendData(DataModel.getListModel(10));
                        mAdapterListView.appendData(DataModel.getListModel(10));
                        view.stopRefreshing();
                    }
                }, 1000);
            }
        });
        view_pull.setOnStateChangedCallback(new SDPullToRefreshView.OnStateChangedCallback()
        {
            @Override
            public void onStateChanged(ISDPullToRefreshView.State state, SDPullToRefreshView view)
            {
                LogUtil.i("onStateChanged:" + state);
            }
        });

        testRecyclerView();
        testListView();

        view_pull.startRefreshingFromHeader();
    }

    private void testListView()
    {
        lv_content.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                if (scrollState == SCROLL_STATE_IDLE)
                {
                    if (!ViewCompat.canScrollVertically(view, 1))
                    {
//                        view_pull.startRefreshingFromFooter();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
            }
        });

        lv_content.setAdapter(mAdapterListView);
    }

    private void testRecyclerView()
    {
        rv_content.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    if (!ViewCompat.canScrollVertically(recyclerView, 1))
                    {
//                        view_pull.startRefreshingFromFooter();
                    }
                }
            }
        });

        rv_content.setAdapter(mAdapterRecyclerView);
    }

    private SDSimpleRecyclerAdapter<DataModel> mAdapterRecyclerView = new SDSimpleRecyclerAdapter<DataModel>(null, this)
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

    private SDSimpleAdapter<DataModel> mAdapterListView = new SDSimpleAdapter<DataModel>(null, this)
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
