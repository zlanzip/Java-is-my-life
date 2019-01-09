### 选择合适的垃圾收集算法
串行收集器： -XX:+UseSerialGC

并行收集器： 
对年轻代进行并行垃圾回收，因此可以减少垃圾回收时间。一般在多线程多处理器机器上使用。使用 -XX:+UseParallelGC 打开。并行收集器在 J2SE5.0第六6更新上引入，在java SE6.0中进行了增强 --- 可以对年老代进行并行收集。如果年老代不使用并行收集的话，默认是使用单线程进行垃圾回收，因此会制约扩展能力。使用 -XX:+UseParallelOldGC打开。

    -XX:+UseParallelOldGC
    对吞吐量有要求
    使用 -XX:ParallelGCThreads = <N> 设置并行垃圾回收的线程数。此值可以设置与机器处理器数量相等。

并发收集器： 
并发收集器主要减少年老代的暂停时间，它在应用不停止的情况下使用独立的垃圾回收线程，跟踪可达对象。在每个年老代垃圾回收周期中，在收集初期并发收集器会对整个应用进行简短的暂停。在收集中还会再暂停一次。第二次暂停会比第一次稍长，在此过程中多个线程同时进行垃圾回收工作。
并发收集器使用处理器换来短暂的停顿时间。在一个N个处理器的系统上，并发收集部分使用 k/N 个可用处理器进行回收，一般情况下 1 <= k <= N / 4。
在只有一个处理器的主机上使用并发收集器，设置为 incremental mode 模式也可获得较短的停顿时间。
    -XX:+UseConcMarkSweepGC
    对响应时间有要求 

小结

    串行处理器：
        -- 适用情况：数据量比较小（100M左右），单处理器下并且对相应时间无要求的应用。
        -- 缺点：只能用于小型应用。

    并行处理器：
        -- 适用情况：“对吞吐量有高要求”，多CPU，对应用过响应时间无要求的中、大型应用。举例：后台处理、科学计算。
        -- 缺点：垃圾收集过程中应用响应时间可能加长。

    并发处理器：
        -- 适用情况：“对响应时间有高要求”，多CPU，对应用响应时间有较高要求的中、大型应用。举例：Web服务器/应用服务器、电信交换、集成开发环境。


### 回收器选择

JVM给了三种选择：串行收集器、并行收集器、并发收集器，但是串行收集器只适用于小数据量的情况，所以这里的选择主要针对并行收集器和并发收集器。默认情况下，JDK5.0以前都是使用串行收集器，如果想使用其他收集器需要在启动的时候加入相应参数。JDK5.0以后，JVM会根据当前系统配置进行判断。

#### 吞吐量优先的并行收集器

如上文所述，并行收集器主要以到达一定的吞吐量为目标，适用于科学计算和后台处理等。

典型配置：

java  -Xmx3800m  -Xms3800m  -Xmn2g  -Xss128k  -XX:+UseParallelGC  -XX:ParallelGCThreads=20

    -XX:+UseParallelGC：选择垃圾收集器为并行收集器。此配置仅对年轻代有效。即上述配置下，年轻代使用并发收集，而年老代仍旧使用串行收集。
    -XX:+ParallelGCThreads=20：配置并行收集器的线程数，即：同时多少个线程一起进行垃圾回收。此值最好配置与处理器数目相等。



java  -Xmx3550m  -Xms3550m  -Xmn2g  -Xss128k  -XX:+UseParallelGC  -XX:ParallelGCThreads=20 -XX:+UseParallelOldGC

    -XX:+UseParallelOldGC：配置年老代垃圾收集方式为并行收集。JDK6.0支持对年老代并行收集。



java  -Xmx3550m  -Xms3550m  -Xmn2g  -Xss128k  -XX:+UseParallelGC  -XX:MaxGCPauseMillis=100

    -XX:MaxGCPauseMillis=100：设置每次年轻代垃圾回收的最长时间，如果无法满足此时间，JVM会自动调整年轻代大小，以满足此值。



java  -Xmx3550m  -Xms3550m  -Xmn2g  -Xss128k  -XX:+UseParallelGC  -XX:MaxGCPauseMillis=100  -XX:+UseAdaptiveSizePolicy

    -XX:+UseAdaptiveSizePolicy：设置此选项后，并行收集器会自动选择年轻代区大小和相应的Survivor区比例，以达到目标系统规定的最低响应时间或者收集频率等，此值建议使用并行收集器时，一直打开。 

 

#### 响应时间优先的并发收集器

如上文所述，并发收集器主要是保证系统的响应时间，减少垃圾收集时的停顿时间。适用于应用服务器、电信领域等。

典型配置：

java  -Xmx3550m  -Xms3550  -Xmn2g  -Xss128k  -XX:ParallelGCThreads=20  -XX:+UseConcMarkSweepGC  -XX:+UseParNewGC

        -XX:+UseConcMarkSweepGC：设置年老代为并发收集。测试中配置这个以后，-XX:NewRatio=4的配置失效了，原因不明。所以，此时年轻代大小最好用-Xmn设置。
        -XX:+UseParNewGC：设置年轻代为并行收集。可与CMS收集同时使用。JDK5.0以上，JVM会根据系统配置自行设置，所以无需再设置此值。

    
java  -Xmx3550m  -Xms3550  -Xmn2g  -Xss128k  -XX:+UseConcMarkSweepGC  -XX:CMSFullGCsBeforeCompaction=5  -XX：+UseCMSCompactAtFullCollection 

        -XX:CMSFullGCsBeforeCompaction：由于并发收集器不对内存空间进行压缩、整理，所以运行一段时间后会产生“碎片”，使得运行效率降低。此值设置运行多少次GC以后对内存空间进行压缩、整理。
        -XX:+UseCMSCompactAtFullCollection：打开对年老代的压缩。可能会影响性能，但是可以消除碎片。


### 常见配置汇总

堆设置

    -Xms：初始堆大小
    -Xmx：最大堆大小
    -XX:NewSize=n：设置年轻代大小
    -XX:NewRatio=n：设置年轻代和年老代的比值。如：为3，表示年轻代与年老代比值为1：3，表示Eden：Survivor=3:2，一个Survivor区占整个年轻代的1/5。
    -XX:MaxPermSize=n：设置持久代大小

收集器设置

    -XX:+UseSerialGC：设置串行收集器
    -XX:+UseParallelGC：设置并行收集器
    -XX:+UseParalledlOldGC：设置并行年老代收集器
    -XX:+UseConcMarkSweepGC：设置并发收集器

垃圾回收统计信息

    -XX:+PrintGC
    -XX:+PrintGCDetails
    -XX:+PrintGCTimeStamps
    -Xloggc:filename

并行收集器设置

    -XX:ParallelGCThreads=n：设置并行收集器收集时使用的CPU数。并行收集线程数。
    -XX:MaxGCPauseMillis=n：设置并行收集最大暂停时间
    -XX:GCTimeRatio=n：设置垃圾回收时间占程序运行时间的百分比。公式为1/(1+N)

并发收集器设置

    -XX:+CMSIncrementalMode：设置为增量模式。适用于单CPU情况。
    -XX:+ParallelGCThreads=n：设置并发收集器年轻代收集方式为并行收集时，使用的CPU数。并行收集线程数。