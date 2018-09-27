
## git全局配置信息
    git config --list
    https://git-scmcom/book/zh/v1/%E8%B5%B7%E6%AD%A5-%E5%88%9D%E6%AC%A1%E8%BF%90%E8%A1%8C-Git-%E5%89%8D%E7%9A%84%E9%85%8D%E7%BD%AE?tdsourcetag=s_pctim_aiomsg


## git仓库远程设置
    git remote set-url origin ssh://git@gitlab.51dinghuo.cc:33984/java/laimi-warehouse.git

## 开分支

    开：git checkout -b feature/2.10.2 develop
    推：git push --set-upstream origin feature/2.10.2


## git版本迭代
    git status
    git checkout master
    git pull

    git flow init
    git flow hotfix start goodsname
    git push --set-upstream origin hotfix/goodsname   --绑定分支
    git fetch 抓取remote索引，更新分支目录

## 重新关联分支
    git branch --set-upstream-to=origin/hotfix/aop

## 开hotfix
    git checkout -b hotfix/savefinsh origin/master

## 错误：mvn package 提示 “chmod” 命令注意事项

    cmd: 执行 where chmod,如果没有就是环境变量没有加进去
        D:\zskx\git\Git\cmd
        D:\zskx\git\Git\usr\bin -- chmod命令所在
        安装的时候可选    

## 错误：Another git process seems to be running in this repository, e.g.
    翻译过来就是git被另外一个程序占用，重启机器也不能够解决。

    原因在于Git在使用过程中遭遇了奔溃，部分被上锁资源没有被释放导致的。

    解决方案：进入项目文件夹下的 .git文件中（显示隐藏文件夹或rm .git/index.lock）删除index.lock文件即可。

## 开release分支

    以后我们在上线前合并代码都通过 git flow 开个 release 分支, 命名用当前日期
    $ git flow start release 20180912.0.xx 
    然后 finish 在 finish 掉的时候这个 release 分支会合并会 master 和 develop
    同时分支名会作为 tag 打好便签
    $ git flow release finish 20180912.0.xx 
    
## git glow 流程

    开始开发需求：
        初始化: git flow init

        开始新Feature: git flow feature start MYFEATURE

        Publish一个Feature(也就是push到远程): git flow feature publish MYFEATURE

        获取Publish的Feature: git flow feature pull origin MYFEATURE

    上线前准备：
        完成一个Feature: git flow feature finish MYFEATURE

        开始一个Release: git flow release start RELEASE [BASE]

    上线：
        Publish一个Release: git flow release publish RELEASE
        发布Release: git flow release finish RELEASE
        别忘了git push --tags

    维护：
        开始一个Hotfix: git flow hotfix start VERSION [BASENAME]

        发布一个Hotfix: git flow hotfix finish VERSION