# pulltorefresh
基于android.support.v4.widget.ViewDragHelper实现的下拉刷新和上拉加载的库

## Gradle
`compile 'com.fanwe.android:pulltorefresh:1.0.6'`

## In Xml File
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

## In Java File
![](http://thumbsnap.com/s/kDqOgNRr.png?0630)
