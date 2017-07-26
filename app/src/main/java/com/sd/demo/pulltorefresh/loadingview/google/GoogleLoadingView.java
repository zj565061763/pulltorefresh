package com.sd.demo.pulltorefresh.loadingview.google;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;

import com.fanwe.library.pulltorefresh.ISDPullToRefreshView;
import com.fanwe.library.pulltorefresh.SDPullToRefreshView;
import com.fanwe.library.pulltorefresh.loadingview.SDPullToRefreshLoadingView;

/**
 * Created by Administrator on 2017/7/26.
 */

public class GoogleLoadingView extends SDPullToRefreshLoadingView
{
    public GoogleLoadingView(@NonNull Context context)
    {
        super(context);
        init();
    }

    public GoogleLoadingView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public GoogleLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private static final int[] DEFAULT_SCHEME_COLORS = {Color.parseColor("#3F51B5"),
            Color.parseColor("#FF4081")};

    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    private static final float MAX_PROGRESS_ANGLE = .8f;

    private MaterialProgressDrawable mProgress;
    private CircleImageView mCircleView;

    private void init()
    {
        createProgressDrawable();
        createCircleImageView();
    }

    private void createProgressDrawable()
    {
        mProgress = new MaterialProgressDrawable(getContext(), this);
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        mProgress.setAlpha(255);
        mProgress.setColorSchemeColors(DEFAULT_SCHEME_COLORS);
    }

    private void createCircleImageView()
    {
        mCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT);
        mCircleView.setImageDrawable(mProgress);

        LayoutParams params = generateDefaultLayoutParams();
        params.width = LayoutParams.WRAP_CONTENT;
        params.height = LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        params.topMargin = (int) (getResources().getDisplayMetrics().density * 10);
        params.bottomMargin = params.topMargin;
        addView(mCircleView, params);
    }

    public void setColorSchemeColors(int... colors)
    {
        mProgress.setColorSchemeColors(DEFAULT_SCHEME_COLORS);
    }

    @Override
    public void onViewPositionChanged(SDPullToRefreshView view)
    {
        super.onViewPositionChanged(view);

        float scrollDistance = Math.abs(view.getScrollDistance());
        float percent = scrollDistance / (float) getRefreshHeight();
        float dragPercent = Math.min(1f, percent);

        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;

        float strokeStart = adjustedPercent * .8f;
        mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
        mProgress.setArrowScale(Math.min(1f, adjustedPercent));

        float rotation = (-0.25f + .4f * adjustedPercent) * .5f;
        mProgress.setProgressRotation(rotation);
    }

    @Override
    public void onStateChanged(ISDPullToRefreshView.State newState, ISDPullToRefreshView.State oldState, SDPullToRefreshView view)
    {
        switch (newState)
        {
            case RESET:
                mProgress.stop();
                break;
            case PULL_TO_REFRESH:
                mProgress.showArrow(true);
                break;
            case RELEASE_TO_REFRESH:

                break;
            case REFRESHING:
                mProgress.start();
                break;
            case REFRESH_SUCCESS:

                break;
            case REFRESH_FAILURE:

                break;
            case REFRESH_FINISH:
                mProgress.stop();
                break;
        }
    }
}
