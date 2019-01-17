
windows下开启binlog
https://www.cnblogs.com/wangwust/p/6433453.html

https://www.cnblogs.com/martinzhang/p/3454358.html

https://blog.csdn.net/u010002184/article/details/78996283

## binlog简介
    MySQL的二进制日志可以说是MySQL最重要的日志了，它记录了所有的DDL和DML(除了数据查询语句)语句，以事件形式记录，还包含语句所执行的消耗的时间，MySQL的二进制日志是事务安全型的。

    一般来说开启二进制日志大概会有1%的性能损耗(参见MySQL官方中文手册 5.1.24版)。二进制有两个最重要的使用场景: 
    其一：MySQL Replication在Master端开启binlog，Mster把它的二进制日志传递给slaves来达到master-slave数据一致的目的。 
    其二：自然就是数据恢复了，通过使用mysqlbinlog工具来使恢复数据。
    
    二进制日志包括两类文件：二进制日志索引文件（文件名后缀为.index）用于记录所有的二进制文件，二进制日志文件（文件名后缀为.00000*）记录数据库所有的DDL和DML(除了数据查询语句)语句事件。 

## 一、开启binlog日志：

### vi编辑打开mysql配置文件

    # vi /usr/local/mysql/etc/my.cnf
    在[mysqld] 区块
    设置/添加 log-bin=mysql-bin  确认是打开状态(值 mysql-bin 是日志的基本名或前缀名)；

### 重启mysqld服务使配置生效

    # pkill mysqld
    # /usr/local/mysql/bin/mysqld_safe --user=mysql &

## 二、也可登录mysql服务器，通过mysql的变量配置表，查看二进制日志是否已开启 单词：variable[ˈvɛriəbəl] 变量

    登录服务器
    # /usr/local/mysql/bin/mysql -uroot -p123456

    mysql> show variables like 'log_%'; 
    +----------------------------------------+---------------------------------------+
    | Variable_name                          | Value                                 |
    +----------------------------------------+---------------------------------------+
    | log_bin                                | ON                                    | ------> ON表示已经开启binlog日志
    | log_bin_basename                       | /usr/local/mysql/data/mysql-bin       |
    | log_bin_index                          | /usr/local/mysql/data/mysql-bin.index |
    | log_bin_trust_function_creators        | OFF                                   |
    | log_bin_use_v1_row_events              | OFF                                   |
    | log_error                              | /usr/local/mysql/data/martin.err      |
    | log_output                             | FILE                                  |
    | log_queries_not_using_indexes          | OFF                                   |
    | log_slave_updates                      | OFF                                   |
    | log_slow_admin_statements              | OFF                                   |
    | log_slow_slave_statements              | OFF                                   |
    | log_throttle_queries_not_using_indexes | 0                                     |
    | log_warnings                           | 1                                     |
    +----------------------------------------+---------------------------------------+

## 三、常用binlog日志操作命令

1.查看所有binlog日志列表 

    mysql> show master logs;

2.查看master状态，即最后(最新)一个binlog日志的编号名称，及其最后一个操作事件pos结束点(Position)值

    mysql> show master status;

3.刷新log日志，自此刻开始产生一个新编号的binlog日志文件

    mysql> flush logs;

    注：每当mysqld服务重启时，会自动执行此命令，刷新binlog日志；在mysqldump备份数据时加 -F 选项也会刷新binlog日志；

4.重置(清空)所有binlog日志

    mysql> reset master;

5.查看第一份binlog 数据

    SHOW BINLOG EVENTS LIMIT 100;

6.查看指定 binlog 数据

    SHOW BINLOG EVENTS IN 'mysql-bin.000120' LIMIT 100;
    
                 选项解析：
               IN 'log_name'   指定要查询的binlog文件名(不指定就是第一个binlog文件)
               FROM pos        指定从哪个pos起始点开始查起(不指定就是从整个文件首个pos点开始算)
               LIMIT [offset,] 偏移量(不指定就是0)
               row_count       查询总条数(不指定就是所有行)

             截取部分查询结果：
             *************************** 20. row ***************************
                Log_name: mysql-bin.000021  ----------------------------------------------> 查询的binlog日志文件名
                     Pos: 11197 ----------------------------------------------------------> pos起始点:
              Event_type: Query ----------------------------------------------------------> 事件类型：Query
               Server_id: 1 --------------------------------------------------------------> 标识是由哪台服务器执行的
             End_log_pos: 11308 ----------------------------------------------------------> pos结束点:11308(即：下行的pos起始点)
                    Info: use `zyyshop`; INSERT INTO `team2` VALUES (0,345,'asdf8er5') ---> 执行的sql语句
             *************************** 21. row ***************************
                Log_name: mysql-bin.000021
                     Pos: 11308 ----------------------------------------------------------> pos起始点:11308(即：上行的pos结束点)
              Event_type: Query
               Server_id: 1
             End_log_pos: 11417
                    Info: use `zyyshop`; /*!40000 ALTER TABLE `team2` ENABLE KEYS */
             *************************** 22. row ***************************
                Log_name: mysql-bin.000021
                     Pos: 11417
              Event_type: Query
               Server_id: 1
             End_log_pos: 11510
                    Info: use `zyyshop`; DROP TABLE IF EXISTS `type`


## binlogsql闪回
mysqlbinlog --start-POSITION=4 --stop-POSITION=459 mysql-bin.000001 > d:\\test1.sql