## jmap

JVM Memory Map命令用于生成heap dump文件，如果不使用这个命令，还可以使用-XX:+HeapDumpOnOutOfMemoryError参数来让虚拟机出现OOM的时候自动生成dump文件。 jmap不仅能生成dump文件，还可以查询finalize执行队列、Java堆和永久代的详细信息，如当前使用率、当前使用的是哪种收集器等。

### 命令格式
- jmap [ option ] pid
- jmap [ option ] executable core
- jmap [ option ] [server-id@]remote-hostname-or-IP

#### 参数

option：选项参数，不可同时使用多个选项参数
pid：java进程id，命令ps -ef | grep java获取
executable：产生核心dump的java可执行文件
core：需要打印配置信息的核心文件
remote-hostname-or-ip：远程调试的主机名或ip
server-id：可选的唯一id，如果相同的远程主机上运行了多台调试服务器，用此选项参数标识服务器

### options参数

heap : 显示Java堆详细信息
histo : 显示堆中对象的统计信息
permstat :Java堆内存的永久保存区域的类加载器的统计信息
finalizerinfo : 显示在F-Queue队列等待Finalizer线程执行finalizer方法的对象
dump : 生成堆转储快照
F : 当-dump没有响应时，强制生成dump快照


### -dump

dump堆到文件,format指定输出格式，live指明是活着的对象,file指定文件名

[root@localhost jdk1.7.0_79]# jmap -dump:live,format=b,file=dump.hprof 24971
Dumping heap to /usr/local/java/jdk1.7.0_79/dump.hprof ...
Heap dump file created

### -heap
打印heap的概要信息，GC使用的算法，heap的配置及使用情况，可以用此来判断内存目前的使用情况以及垃圾回收情况

### -histo

打印堆的对象统计，包括对象数、内存大小等等。jmap -histo:live 这个命令执行，JVM会先触发gc，然后再统计信息

jmap -histo:live 24971 | grep laimi | head -20