

## springCloud 如何实现集群
eureka注册中心作为服务注册到其他的eureka注册中心

客户端注册进去注册中心集群：

    eureka.client.serviceUrl.defaultZone 把 eureka 集群中的所有 url 都填写了进来，也可以只写一台，因为各个 eureka server 可以同步注册表。

## eureka自我保护模式
当 Eureka Server 节点在短时间内丢失了过多实例的连接时（比如网络故障或频繁的启动关闭客户端），那么这个节点就会进入自我保护模式，一旦进入到该模式，Eureka server 就会保护服务注册表中的信息，不再删除服务注册表中的数据（即不会注销任何微服务），当网络故障恢复后，该 Ereaka Server 节点就会自动退出自我保护模式

默认情况下，如果 Ereaka Server 在一段时间内没有接受到某个微服务示例的心跳，便会注销该实例（默认90秒），而一旦进入自我保护模式，那么即使你关闭了指定实例，仍然会发现该 Ereaka Server 的注册实例中会存在被关闭的实例信息，如果你对该实例做了负载均衡，那么仅关闭了其中一个实例，则通过网关调用接口api时很可能会发生异常

## ribbon默认负载均衡策略
默认的负载均衡算法是 Round Robin 算法，顺序向下轮询

## Spring Cloud Ribbon 重试机制
```yml
# hystrix的超时时间必须大于ribbon的超时时间
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=10000
# 开启重试
zuul.retryable=true
spring.cloud.loadbalancer.retry.enabled=true
# 请求连接的超时时间
ribbon.connectTimeout=2000
# 请求处理的超时时间
ribbon.readTimeout=5000
# 对当前实例的重试次数
ribbon.maxAutoRetries=1
# 切换实例的重试次数
ribbon.maxAutoRetriesNextServer=3
# 对所有操作请求都进行重试
ribbon.okToRetryOnAllOperations=true

```
## Ribbon 是怎么和 Eureka 整合的？

![](image/2019-01-18-11-24-51.png)
1. 首先，用户调用 Feign 创建的动态代理。
2. 然后，Feign 调用 Ribbon 发起调用流程。

    - 首先，Ribbon 会从 Eureka Client 里获取到对应的服务列表。
    - 然后，Ribbon 使用负载均衡算法获得使用的服务。
    - 最后，Ribbon 调用对应的服务。最后，Ribbon 调用 Feign ，而 Feign 调用 HTTP 库最终调用使用的服务。

这可能是比较绕的，艿艿自己也困惑了一下，后来去请教了下 didi 。因为 Feign 和 Ribbon 都存在使用 HTTP 库调用指定的服务，那么两者在集成之后，必然是只能保留一个。比较正常的理解，也是保留 Feign 的调用，而 Ribbon 更纯粹的只负责负载均衡的功能。

## 如下是 Eureka + Ribbon + Feign + Hystrix + Zuul 整合后的图
![](image/2019-01-18-11-43-23.png)

##