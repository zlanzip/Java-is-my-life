
## 单zuul项目启动、网关转发

application.properties配置

```java
server.port=8888

#\u8FD9\u91CC\u7684\u914D\u7F6E\u8868\u793A\uFF0C\u8BBF\u95EE/it/** \u76F4\u63A5\u91CD\u5B9A\u5411\u5230http://www.ityouknow.com/**
zuul.routes.it.path=/it/**
zuul.routes.it.url=http://www.ityouknow.com

zuul.routes.hello.path=/hello/**
zuul.routes.hello.url=http://localhost:9000/
```
定义了2个转发:
    
凡是localhost:8888/it/** 的请求，全部指向 `http://www.ityouknow.com`

 ![](image/2018-10-14-12-09-00.png)

凡是localhost:8888/hello/** 的请求，全部指向到 9000端口的项目
如：`http://localhost:8888/hello/hello?name=zskx`  指向 `http://localhost:9000/hello?name=zskx`

![](image/2018-10-14-12-17-26.png)

![](image/2018-10-14-12-15-39.png)



