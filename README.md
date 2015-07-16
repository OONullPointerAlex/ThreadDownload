## ThreadDownload

Android-Service系列之断点续传下载
功能介绍：本示例介绍在Android平台下通过HTTP协议实现断点续传下载。

 >学习资源来源于[慕课网](2)，地址链接为：[Android-Service系列之断点续传下载](3)，特别感谢[XRay_Chen](4)的网络课程。
 
[2]:http://www.imooc.com/
[3]:http://www.imooc.com/learn/363
[4]:http://www.imooc.com/space/teacher/id/1395824

###学习内容

- 基本UI定义
- 数据库的操作
- Service的启动
-  Activity给Service传递参数
- 使用广播回传数据到Activity
- 线程和Handler
- 网络操作

### 网络下载的关键点

- 获取网络文件长度
- 在本地创建一个文件，设置其长度
- 从数据库中获取上次下载的进度
- 从上次下载的位置下载数据，同时保存进度到数据库
- 将下载进度回传到Activity
- 下载完成后删除下载信息
