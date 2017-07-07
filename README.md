# pulltorefresh
基于android.support.v4.widget.ViewDragHelper实现的下拉刷新和上拉加载的库

## Gradle
`compile 'com.fanwe.android:pulltorefresh:1.0.6'`

## 默认效果
![](http://thumbsnap.com/i/ZYFfADbW.gif?0707)<br>
可以在项目的strings中定义字符串覆盖库中的默认文字提示<br>
```xml
    <string name="lib_pull_state_pull_to_refresh_header">下拉刷新</string>
    <string name="lib_pull_state_pull_to_refresh_footer">上拉加载</string>

    <string name="lib_pull_state_release_to_refresh_header">松开刷新</string>
    <string name="lib_pull_state_release_to_refresh_footer">松开加载</string>

    <string name="lib_pull_state_refreshing_header">刷新中...</string>
    <string name="lib_pull_state_refreshing_footer">加载中...</string>
```

## Xml布局
在xml中只能给SDPullToRefreshView添加一个child<br>
child可以是RecyclerView,ListView,ScrollView等...
```xml
    <com.fanwe.library.pulltorefresh.SDPullToRefreshView
        android:id="@+id/view_pull"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <!--RecyclerView,ListView,ScrollView...-->

    </com.fanwe.library.pulltorefresh.SDPullToRefreshView>
```

## 常用方法
```java
view_pull.setDebug(true); //设置调试模式，会打印log
view_pull.setMode(ISDPullToRefreshView.Mode.BOTH); //刷新模式，BOTH，PULL_FROM_HEADER，PULL_FROM_FOOTER，DISABLE
view_pull.startRefreshingFromHeader(); //触发下拉刷新，此方法只受DISABLE模式限制，不受其他模式限制
view_pull.startRefreshingFromFooter(); //触发上拉加载，此方法只受DISABLE模式限制，不受其他模式限制
view_pull.stopRefreshing(); //停止刷新或者加载
view_pull.setComsumeScrollPercent(0.5f); //设置拖动距离消耗比例[0-1]，让拖动具有阻尼感，默认0.5
view_pull.getScrollDistance(); //获得滚动的距离
view_pull.getDirection(); //获得滚动的方向，HEADER_TO_FOOTER，FOOTER_TO_HEADER
view_pull.setHeaderView(new CustomPullToRefreshLoadingView(this)); //自定义HeaderView
view_pull.setFooterView(new CustomPullToRefreshLoadingView(this)); //自定义FooterView
view_pull.setOnRefreshCallback(new ISDPullToRefreshView.OnRefreshCallback() //设置触发刷新回调
{
    @Override
    public void onRefreshingFromHeader(final SDPullToRefreshView view)
    {
        //头部刷新回调
    }

    @Override
    public void onRefreshingFromFooter(final SDPullToRefreshView view)
    {
        //底部加载回调
    }
});
view_pull.setOnStateChangedCallback(new ISDPullToRefreshView.OnStateChangedCallback() //设置状态变化回调
{
    @Override
    public void onStateChanged(ISDPullToRefreshView.State state, SDPullToRefreshView view)
    {
        //RESET，PULL_TO_REFRESH，RELEASE_TO_REFRESH，REFRESHING
        //自定义的加载view继承库中的加载view基类后也可以收到此事件，可以根据状态展示不同的ui
    }
});
view_pull.setOnViewPositionChangedCallback(new ISDPullToRefreshView.OnViewPositionChangedCallback() //设置view位置变化回调
{
    @Override
    public void onViewPositionChanged(SDPullToRefreshView view)
    {
        //自定义的加载view继承库中的加载view基类后也可以收到此事件，可以根据状态和滚动距离自定义各种炫酷ui
    }
});
```
## 支持的方法
```java
public interface ISDPullToRefreshView
{
    /**
     * 默认的拖动消耗比例
     */
    float DEFAULT_COMSUME_SCROLL_PERCENT = 0.5f;

    /**
     * 设置刷新模式
     *
     * @param mode
     */
    void setMode(Mode mode);

    /**
     * 设置刷新回调
     *
     * @param onRefreshCallback
     */
    void setOnRefreshCallback(OnRefreshCallback onRefreshCallback);

    /**
     * 设置状态变化回调
     *
     * @param onStateChangedCallback
     */
    void setOnStateChangedCallback(OnStateChangedCallback onStateChangedCallback);

    /**
     * 设置view位置变化回调
     *
     * @param onViewPositionChangedCallback
     */
    void setOnViewPositionChangedCallback(OnViewPositionChangedCallback onViewPositionChangedCallback);

    /**
     * 设置拖动的时候要消耗的拖动距离比例，默认是0.5
     *
     * @param comsumeScrollPercent [0-1]
     */
    void setComsumeScrollPercent(float comsumeScrollPercent);

    /**
     * 设置HeaderView处处于刷新状态
     */
    void startRefreshingFromHeader();

    /**
     * 设置Foot而View处处于刷新状态
     */
    void startRefreshingFromFooter();

    /**
     * 停止刷新
     */
    void stopRefreshing();

    /**
     * 是否处于刷新中
     *
     * @return
     */
    boolean isRefreshing();

    /**
     * 返回当前的状态
     *
     * @return
     */
    State getState();

    /**
     * 返回HeaderView
     *
     * @return
     */
    SDPullToRefreshLoadingView getHeaderView();

    /**
     * 设置HeaderView
     *
     * @param headerView
     */
    void setHeaderView(SDPullToRefreshLoadingView headerView);

    /**
     * 返回FooterView
     *
     * @return
     */
    SDPullToRefreshLoadingView getFooterView();

    /**
     * 设置FooterView
     *
     * @param footerView
     */
    void setFooterView(SDPullToRefreshLoadingView footerView);

    /**
     * 返回要支持刷新的view
     *
     * @return
     */
    View getRefreshView();

    /**
     * 返回当前拖动方向
     *
     * @return
     */
    Direction getDirection();

    /**
     * 返回滚动的距离
     *
     * @return
     */
    int getScrollDistance();

    enum State
    {
        RESET,
        PULL_TO_REFRESH,
        RELEASE_TO_REFRESH,
        REFRESHING,
    }

    enum Direction
    {
        NONE,
        HEADER_TO_FOOTER,
        FOOTER_TO_HEADER,
    }

    enum Mode
    {
        /**
         * 支持上下拉
         */
        BOTH,
        /**
         * 只支持下拉
         */
        PULL_FROM_HEADER,
        /**
         * 只支持上拉
         */
        PULL_FROM_FOOTER,
        /**
         * 不支持上下拉
         */
        DISABLE,
    }

    enum LoadingViewType
    {
        HEADER,
        FOOTER,
    }

    interface OnStateChangedCallback
    {
        /**
         * 状态变化回调
         *
         * @param state
         * @param view
         */
        void onStateChanged(State state, SDPullToRefreshView view);
    }

    interface OnRefreshCallback
    {
        /**
         * 下拉触发刷新回调
         *
         * @param view
         */
        void onRefreshingFromHeader(SDPullToRefreshView view);

        /**
         * 上拉触发刷新回调
         *
         * @param view
         */
        void onRefreshingFromFooter(SDPullToRefreshView view);
    }

    interface OnViewPositionChangedCallback
    {
        /**
         * view位置变化回调
         *
         * @param view
         */
        void onViewPositionChanged(SDPullToRefreshView view);
    }

    /**
     * 加载view基类接口
     */
    interface IPullToRefreshLoadingView extends OnStateChangedCallback, OnViewPositionChangedCallback
    {
        /**
         * 返回触发刷新条件的高度
         *
         * @return
         */
        int getRefreshHeight();

        /**
         * 返回加载view类型
         *
         * @return
         */
        LoadingViewType getLoadingViewType();

        SDPullToRefreshView getPullToRefreshView();
    }
}
```
