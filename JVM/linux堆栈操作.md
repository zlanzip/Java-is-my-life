

先看JVM总的垃圾回收情况 jstat
找cpu使用最高的线程
看


### jstat

jstat -options

    2060是进程号 pid
    -compiler 类加载统计 jstat -class 2060
    -gc 编译统计 jstat -compiler 2060
    -gccapacity 垃圾回收统计 jstat -gc 2060
    -gccause  堆内存统计 jstat -gccapacity 2060
    -gcmetacapacity 元数据空间统计  jstat -gcmetacapacity 7172
    -gcnew 新生代垃圾回收统计 jstat -gcnew 7172
    -gcnewcapacity 新生代内存统计 jstat -gcnewcapacity 7172 
    -gcold 老年代垃圾回收统计 jstat -gcold 7172
    -gcoldcapacity 老年代内存统计 jstat -gcoldcapacity 7172
    -gcutil 总结垃圾回收统计 jstat -gcutil 7172
    -printcompilation JVM编译方法统计 jstat -printcompilation 7172

