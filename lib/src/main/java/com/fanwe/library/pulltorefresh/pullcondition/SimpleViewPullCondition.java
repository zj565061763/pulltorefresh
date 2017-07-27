package com.fanwe.library.pulltorefresh.pullcondition;

import android.support.v4.view.ViewCompat;
import android.view.View;

import com.fanwe.library.pulltorefresh.ISDPullToRefreshView;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2017/7/27.
 */
public class SimpleViewPullCondition implements ISDPullToRefreshView.IPullCondition
{
    private WeakReference<View> mView;

    public SimpleViewPullCondition(View view)
    {
        mView = new WeakReference<>(view);
    }

    public View getView()
    {
        if (mView != null)
        {
            return mView.get();
        } else
        {
            return null;
        }
    }

    @Override
    public boolean canPullFromHeader()
    {
        if (getView() == null)
        {
            return true;
        } else
        {
            return !ViewCompat.canScrollVertically(getView(), -1);
        }
    }

    @Override
    public boolean canPullFromFooter()
    {
        if (getView() == null)
        {
            return true;
        } else
        {
            return !ViewCompat.canScrollVertically(getView(), 1);
        }
    }
}
