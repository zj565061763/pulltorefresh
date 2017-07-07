# pulltorefresh
基于android.support.v4.widget.ViewDragHelper实现的下拉刷新和上拉加载的库

## Gradle
`compile 'com.fanwe.android:pulltorefresh:1.0.6'`

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
view_pull.setOnViewPositionChangedCallback(new ISDPullToRefreshView.OnViewPositionChangedCallback()
{
    @Override
    public void onViewPositionChanged(SDPullToRefreshView view)
    {
        //view被拖动回调
        //自定义的加载view继承库中的加载view基类后也可以收到此事件，可以根据状态和滚动距离自定义各种炫酷ui
    }
});
```
