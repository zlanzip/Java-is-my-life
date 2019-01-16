# 对接文档
-------------------------
## 一.配置

### 1. 引入依赖
```xml
<dependency>
   <groupId>com.laimi</groupId>
   <artifactId>laimi-excel-platform-sdk</artifactId>
   <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. 配置服务ip和端口(简称`服务名`)
有两种类型的服务名要配置, excel平台服务名和sdk项目的服务名, excel平台的服务名主要用于启动时发送请求,sdk所在项目的服务名
主要是用于给启动时, 注册到excel平台, 给excel平台在执行任务时调用 . 接入的时候都有默认配置

#### a. 服务平台服务地址的配置
```sql
#platformHostName的优先级比platformServiceName高, 如果platformHostName没有配置则程序会读取platformServiceName
#platformServiceName主要用于spring cloud的负载均衡, 也就是eureka的实例名, 如果没有接入spring cloud , 则配置platformHostName
#platformHostName必须是具体的ip或ip:port
#platformServiceName的默认值是laimi-excel-platform
laimi.excelHandle.platformServiceName = laimi-excel-platform
laimi.excelHandle.platformHostName = localhost:8821
```

#### b. sdk项目的服务名
```sql
#sdkHostName如果没有配置, 则程序优先读取spring.application.name作为sdk的服务名
laimi.excelHandle.sdkHostName = localhost:8772
spring.application.name = athena
```
## 二. 开发步骤

### 1.导入导出配置
在这个地址[http://excel-platform.51dinghuo.cc/](http://excel-platform.51dinghuo.cc/)进行导入导出配置
####a. 导入的基本配置
>编码:    这个必填, 全局唯一, 程序开发的直接需要用到
>项目:     用于区分那个项目的业务
>业务名称:     导入导出的名称
>excel模板文件:     主要给用户下载填写的
>xml解析文件:     主要用于解析excel的配置
>校验结果模板excel文件:      如果基础校验不通过或业务校验不通过, 则根据这个模版生成校验结果excel供下载 
>是否做业务校验:     如果是否, 表示只有基础校验通过才会发送给sdk做业务校验, 如果是真, 表示无论基础校验通过不通过都发送给业务方
>是否返回数据:     如果勾上, 则如果流程结束后会将数据返回

#### b. 导出的基本配置

>编码:    这个必填, 全局唯一, 程序开发的直接需要用到
>项目:     用于区分那个项目的业务
>业务名称:     导入导出的名称
>导出excel模板文件:     主要是生成excel导出的模板, 如果非动态表头, 则一定要excel模板
>是否动态表头:     如果勾上, 则如果根据程序的实时给的表头生成excel, 

### 2.sdk导入导出开发
#### a.导入
导入要实现ImportService, 具体看接口文档

#### a.导出
导出要实现ExportService, 具体看接口文档


## 三.配置文件内容
### 1.xml解析文件
>key是装进对象的属性名
>type的类型主要有: string, int, double, float, long, date, boolean, 如果类型是date, 则mapping可以配置datePattern = 'yyyy-MM-dd HH:mm:ss' 属性
>rule校验规则, 可以配置多个, 目前只写了equals(相等), required(必填), match(正则) 三种类型的基础校验器, 可根据情况开发更多的校验器, 校验器
>的使用可以查看sdk下的com.laimi.platform.common.validator包下的类
```xml
<?xml version="1.0" encoding="UTF-8"?>
<rowMapping sheetName="template">
    <mapping col="0" key="goodsId" type="long">
        <rules>
        <rule name="required" msg="商品id必填"/>
        </rules>
    </mapping>

    <mapping col="1" key="code" type="string">
        <rules>
        <rule name="required" msg="商品编码必填"/>
        <rule name="between" msg="商品编码长度应该5-100个字符之间">
        <param name="min" value="5"/>
        <param name="max" value="100"/>
        </rule>
        </rules>
    </mapping>

    <mapping col="2" key="title" type="string">
        <rules>
        <rule name="required" msg="商品条码必填"/>
        <rule name="between" msg="商品条码长度应该10-100个字符之间">
        <param name="min" value="10"/>
        <param name="max" value="100"/>
        </rule>
        </rules>
    </mapping>
    <mapping col="3" key="creationDate" type="date" datePattern = 'yyyy-MM-dd HH:mm:ss'>
    </mapping>

</rowMapping>

```
### 2.excel模板文件
商品编码| 商品名称 | 商品条码 | 校验不通过
- | :-: | -: 
\$code | \$title | ${"key":"goodsTitle","style":{"color":"RED","bgColor":"YELLOW","condition":"goodsCode == 2"}} | \$errorMsg_

> \$code是简写, \${}这个是完整写法, 其中{}是json格式
> 程序会将当前的所有字段放进ognl的context中, condition可以直接用字段名引用
> condition: 写返回boolean类型的ognl表达式, 如condition为true时, style的样式会应用, 目前style只支持字段颜色和背景颜色
> $errorMsg_ : 校验不通过的信息, 这个一个固定的字段, 内置




























































