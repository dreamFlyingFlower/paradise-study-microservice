# RocketMQ



# 安装



## 服务安装

* 要求JDK1.8,Maven3.2,[下载地址](http://archive.apache.org/dist/rocketmq),按需下载版本,解压到/app/rocketmq中
* 修改bin/runserver.sh,根据服务器情况调整内存大小

```shell
set "JAVA_OPT=%JAVA_OPT% -server -Xms512m -Xmx512m -Xmn512m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m" 
```

* 修改bin/runbroker.sh,调整磁盘利用率大小,默认磁盘空间超过85%不再接收消息

```shell
set "JAVA_OPT=%JAVA_OPT% -server -Drocketmq.broker.diskSpaceWarningLevelRatio=0.98 -Xms512m -Xmx512m -Xmn512m"
```

* 启动NameServer:sh mqnamesrv
* 启动Broker:sh mqbroker -n 127.0.0.1:9876,-n指定nameserver的地址



## Web界面安装

* [下载](https://github.com/apache/rocketmq-externals/tree/master/rocketmq-console),解压,是一个springboot的原码
* 修改application.properties中的rocketmq.config.namesrvAddr为RocketMQ服务的端口
* 打包成Jar:mvn clean package -Dmaven.test.skip=true
* 运行Jar包,访问ip:port

