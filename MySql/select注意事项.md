## mysql 的delete from 子查询限制

1.使用mysql进行delete from操作时，若子查询的 FROM 字句和更新/删除对象使用同一张表，会出现错误。 
```sql
    mysql> DELETE FROM tab1 WHERE col1 = ( SELECT MAX( col1 ) FROM tab1 ); 
    ERROR 1093 (HY000): You can’t specify target table ‘tab1′ for update in FROM clause 

    针对“同一张表”这个限制，撇开效率不谈，多数情况下都可以通过多加一层select 别名表来变通解决，像这样 

    DELETE FROM tab1 
    WHERE col1 = ( 
    SELECT MAX( col1 ) 
    FROM ( 
    SELECT * FROM tab1 
    ) AS t 
    ); 
```
2.mysql delete from where in 时后面 的查询语句里不能加where条件 
```sql
    Sql代码 
        delete from `t_goods` where fi_id in (select * from ( select fi_id from `t_goods` where fs_num is null and fs_name is null and fs_type is null and fs_using is null and fs_lifetime is null) b)  

    Sql代码 
        delete from `t_goods` where fi_id in (select fi_id from `t_goods` where fs_num is null and fs_name is null and fs_type is null and fs_using is null and fs_lifetime is null)   

    Sql代码 
        delete from `t_goods` where fi_id in ( select fi_id from `t_goods` )   

    上面三种情况，只有中间的不能执行。 

    综合起来就是mysql delete from where in 时后面 的查询语句里不能加where条件 

    解决方法：将要select where 的数据用临时表存起来

```

3.delete from table... 这其中table不能使用别名 
```sql
    Sql代码 
        delete from student a where a.id in (1,2);(执行失败) 
        select a.* from student a where a.id in (1,2);(执行成功)`
```

## for update语句锁表的细节

当for update的字段为索引或者主键的时候，只会锁住索引或者主键对应的行。而当for update的字段为普通字段的时候，Innodb会锁住整张表。