

### jstack 查看线程具体在做什么，可看出哪些线程在长时间占用CPU，尽快定位问题和解决问题

1.top查找出哪个进程消耗的cpu高。执行top命令，默认是进程视图，其中PID是进程号
co_ad2    18   0 1817m 776m 9712 S  3.3  4.9  12:03.24 java                                                                                          
co_ad     21   0 3028m 2.5g 9432 S  1.0 16.3   6629:44 ja


这里我们分析21125这个java进程
2.top中shift+h 或“H”查找出哪个线程消耗的cpu高 
先输入top，然后再按shift+h 或“H”，此时打开的是线程视图，pid为线程号
co_ad2    15   0 1807m 630m 9492 S  1.3  4.0   0:05.12 java                                                                                           
co_ad2_s  15   0 1360m 560m 9176 S  0.3  3.6   0:46.72 java                                                                                           

这里我们分析21233这个线程，并且注意的是，这个线程是属于21125这个进程的。 

3.使用jstack命令输出这一时刻的线程栈，保存到文件，命名为jstack.log。注意：输出线程栈和保存top命令快照尽量同时进行。由于jstack.log文件记录的线程ID是16进制，需要将top命令展示的线程号转换为16进制。

4.jstack查找这个线程的信息 
jstack [进程]|grep -A 10 [线程的16进制] 
即： jstack 21125|grep -A 10 52f1  

-A 10表示查找到所在行的后10行。21233用计算器转换为16进制52f1，注意字母是小写。 
结果： 
 
"http-8081-11" daemon prio=10 tid=0x00002aab049a1800 nid=0x52bb in Object.wait() [0x0000000042c75000]  
   java.lang.Thread.State: WAITING (on object monitor)  
     at java.lang.Object.wait(Native Method)  
     at java.lang.Object.wait(Object.java:485)  
     at org.apache.tomcat.util.net.JIoEndpoint$Worker.await(JIoEndpoint.java:416)  

在结果中查找52f1，可看到当前线程在做什么。

### jsatck dump分析

https://www.cnblogs.com/kongzhongqijing/articles/3630264.html

对于jstack做的ThreadDump的栈，可以反映如下信息（源自）：

    如果某个相同的call stack经常出现， 我们有80%的以上的理由确定这个代码存在性能问题（读网络的部分除外）；
    如果相同的call stack出现在同一个线程上（tid）上， 我们很很大理由相信， 这段代码可能存在较多的循环或者死循环；
    如果某call stack经常出现， 并且里面带有lock，请检查一下这个lock的产生的原因， 可能是全局lock造成了性能问题；
    在一个不大压力的群集里（w<2）， 我们是很少拿到带有业务代码的stack的， 并且一般在一个完整stack中， 最多只有1-2业务代码的stack，
    如果经常出现， 一定要检查代码， 是否出现性能问题。
    如果你怀疑有dead lock问题， 那么请把所有的lock id找出来，看看是不是出现重复的lock id。


### 频繁GC问题或内存溢出问题

1. 使用jps查看线程ID
2. 使用jstat -gc 3331 250 20 查看gc情况，一般比较关注PERM区的情况，查看GC的增长情况。
3. 使用jstat -gccause：额外输出上次GC原因
4. 使用jmap -dump:format=b,file=heapDump 3331生成堆转储文件
5. 使用jhat或者可视化工具（Eclipse Memory Analyzer 、IBM HeapAnalyzer）分析堆情况。
6. 结合代码解决内存溢出或泄露问题。

 

### 死锁问题

1. 使用jps查看线程ID
2. 使用jstack 3331：查看线程情况

 