
## ZookeeperConfiguration 配置

```yml
elastic:
  job:
    zk:
      serverLists: 101.236.43.161:2181
      namespace: laimi_replenishment_job
```
ZookeeperConfiguration
```java
@Getter
@Setter
@RequiredArgsConstructor
public final class ZookeeperConfiguration {
    
    /**
     * 连接Zookeeper服务器的列表.
     * 包括IP地址和端口号.
     * 多个地址用逗号分隔.
     * 如: host1:2181,host2:2181
     */
    private final String serverLists;
    
    /**
     * 命名空间.
     */
    private final String namespace;
    
    /**
     * 等待重试的间隔时间的初始值.
     * 单位毫秒.
     */
    private int baseSleepTimeMilliseconds = 1000;
    
    /**
     * 等待重试的间隔时间的最大值.
     * 单位毫秒.
     */
    private int maxSleepTimeMilliseconds = 3000;
    
    /**
     * 最大重试次数.
     */
    private int maxRetries = 3;
    
    /**
     * 会话超时时间.
     * 单位毫秒.
     */
    private int sessionTimeoutMilliseconds;
    
    /**
     * 连接超时时间.
     * 单位毫秒.
     */
    private int connectionTimeoutMilliseconds;
    
    /**
     * 连接Zookeeper的权限令牌.
     * 缺省为不需要权限验证.
     */
    private String digest;
}
```


## 初始化ZookeeperRegistryCenter
在elasticJob中，所有的关于zk的操作都会交给ZookeeperRegistryCenter该对象去协调，在这里去初始化zookeeper，操作节点，使用缓存等