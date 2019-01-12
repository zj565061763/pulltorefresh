package com.sd.demo.pulltorefresh.loadingview;

import android.content.Context;
import android.util.AttributeSet;

import com.sd.demo.pulltorefresh.R;
import com.sd.lib.pulltorefresh.PullToRefreshView;
import com.sd.lib.pulltorefresh.loadingview.SimpleImageLoadingView;
import com.sd.lib.utils.FViewUtil;
import com.sd.lib.utils.context.FResUtil;

/**
 * Created by Administrator on 2017/6/30.
 */

public class CustomPullToRefreshLoadingView extends SimpleImageLoadingView
{
    public CustomPullToRefreshLoadingView(Context context)
    {
        super(context);
    }

    public CustomPullToRefreshLoadingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomPullToRefreshLoadingView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init()
    {
        super.init();
        FViewUtil.setHeight(getImageView(), FResUtil.dp2px(35));
    }

    @Override
    public void onStateChanged(PullToRefreshView.State oldState, PullToRefreshView.State newState, PullToRefreshView view)
    {
        switch (newState)
        {
            case RESET:
            case PULL_TO_REFRESH:
            case FINISH:
                getImageView().setImageResource(R.drawable.ic_pull_refresh_normal);
                break;
            case RELEASE_TO_REFRESH:
                getImageView().setImageResource(R.drawable.ic_pull_refresh_ready);
                break;
            case REFRESHING:
                getImageView().setImageResource(R.drawable.ic_pull_refresh_refreshing);
                FViewUtil.startAnimationDrawable(getImageView().getDrawable());
                break;
        }
    }
}
