

## elastic-job启动时候配置没有更新

参考：http://www.imooc.com/article/252337


启动作业过程中，首先会更新作业配置：

![](image/2018-11-08-16-37-24.png)

持久化配置条件：

![](image/2018-11-08-16-39-32.png)

却没有持久化：

![](image/2018-11-08-16-36-19.png)

解决方法：设置overwrite=true 即可

![](image/2018-11-08-16-54-11.png)   

![](image/2018-11-08-16-54-55.png)