## 1.0.13
* 新增stopRefreshingWithResult(boolean success);方法，可以展示刷新成功和失败的结果
* 新增setDurationShowRefreshResult(int durationShowRefreshResult);方法，可以设置显示刷新成功或者刷新失败的时长
* 新增REFRESH_SUCCESS（刷新成功），REFRESH_FAILURE（刷新失败），REFRESH_FINISH（刷新完成）状态

## 1.0.12
* 修复滚动中或者刷新中改变view的高度后造成的界面显示异常bug

## 1.0.11
* 滚动动画的时长根据最大滚动距离，当前滚动距离，最小动画时长，最大动画时长参数动态计算

## 1.0.10
* 修改下拉刷新到刷新中的过程动画时长为600毫秒

## 1.0.9
* 内部实现改为Scrolle+ViewGroup实现
* 新增设置覆盖模式或者拖拽模式功能
