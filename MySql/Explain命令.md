## expllain 预估的rows和实际查询的行数区别

explain是怎样预估行数的

1）mysql-5.5之前

>　　首先找到查询第一个记录所在的page（记为PLeft），统计PLeft里的记录数（记为Records_PLeft），之后找到最后一个记录所在的page（记为PRight），统计PRight的记录数（Records_PRight），之后将Records_PLeft与Records_PRight取平均，最后乘以总共的page数目（记为Page_Num）。公式如下：
Rows = ((Records_PLeft + Records_PRight)/2)*Page_Num

2）mysql-5.5之后

>　　上述预估偏差大的关键在于有偏，而有偏的关键在于采样的page数太少了，事实上只采样了边界2个，新算法的思路很简单，增加采样数目，比如采样10个page，这样可以在一定程度上降低偏差。

>　　具体来说，mysql除了边界2个外，还沿着左侧page往右连续查找8个page，如果总的page数目小于等于10个，那么预估的Rows和真实的Rows一致。
Rows = ((Records_PLeft +  Records_P1 + Records_P2 + ... + Records_P8 + Records_PRight)/10)*Page_Num

3)思考

>　　为什么是从左往右连续选8个page，而不是在首尾之间随机选择8个page，既然要缓解采样有偏的问题，那么随机选应该更好。猜想可能有两个原因：1）随机选择每次explain得到的Rows不一样，不方便应用；2）随机选会造成I/O开销，尤其是数据量大的时候，毕竟explain是希望能快速得到预估结果。

## EXPLAIN type(从上到下,性能从差到好)

    all 全表查询
    index 索引全扫描
    range 索引范围扫描
    ref 使用非唯一或唯一索引的前缀扫描,返回相同值的记录
    eq_ref 使用唯一索引,只返回一条记录
    const,system 单表中最多只有一行匹配,根据唯一索引或主键进行查询
    null 不访问表或索引就可以直接得到结果

## MYSQL 五大引擎

    ISAM :读取快,不占用内存和存储资源。 不支持事物,不能容错。
    MyISAM :读取块,扩展多。
    HEAP :驻留在内存里的临时表格,比ISAM和MyISAM都快。数据是不稳定的,关机没保存,数据都会丢失。
    InnoDB :支持事物和外键,速度不如前面的引擎块。
    Berkley(BDB) :支持事物和外键,速度不如前面的引擎块。
    一般需要事物的设为InnoDB,其他设为MyISAM

