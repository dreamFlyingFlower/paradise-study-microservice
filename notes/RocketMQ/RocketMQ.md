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



* [下载](https://github.com/apache/rocketmq-externals/tree/master/rocketmq-console),解压,是一个springboot的源码程序
* 修改application.properties中的rocketmq.config.namesrvAddr为RocketMQ服务的端口
* 打包成Jar:mvn clean package -Dmaven.test.skip=true
* 运行Jar包,访问ip:port



## 配置文件



* conf目录下有多个配置文件:

  * 2m-2s-async:双主双从异步复制模式
  * 2m-2s-sync:双主双从同步双写模式
  * 2m-noslave:双主模式

* 双主模式:需要在broker-a.properties 与 broker-b.properties 末尾追加 NameServer 集群的地址

  ```properties
  # broker-a.properties
  # 集群名称,同一个集群下的 broker 要求统一
  brokerClusterName=DefaultCluster
  # broker名称
  brokerName=broker-a
  # brokerId=0 代表主节点,大于零代表从节点
  brokerId=0
  # 删除日志文件时间点,默认凌晨2点
  deleteWhen=02
  # 日志文件保留时间,默认48小时
  fileReservedTime=48
  # Broker的角色:ASYNC_MASTER->异步复制Master;SYNC_MASTER->同步双写Master
  brokerRole=ASYNC_MASTER
  # 刷盘方式:ASYNC_FLUSH->异步刷盘,性能好宕机会丢数;SYNC_FLUSH->同步刷盘,性能较差不会丢数
  flushDiskType=ASYNC_FLUSH
  # 末尾追加,NameServer节点列表,使用分号分割
  namesrvAddr=192.168.31.200:9876;192.168.31.201:9876
  ```

  ```properties
  # broker-b.properties,只有brokerName不同,其他一样
  # 集群名称,同一个集群下的 broker 要求统一
  brokerClusterName=DefaultCluster
  # broker名称
  brokerName=broker-b
  # brokerId=0 代表主节点,大于零代表从节点
  brokerId=0
  # 删除日志文件时间点,默认凌晨2点
  deleteWhen=02
  # 日志文件保留时间,默认48小时
  fileReservedTime=48
  # Broker的角色:ASYNC_MASTER->异步复制Master;SYNC_MASTER->同步双写Master
  brokerRole=ASYNC_MASTER
  # 刷盘方式:ASYNC_FLUSH->异步刷盘,性能好宕机会丢数;SYNC_FLUSH->同步刷盘,性能较差不会丢数
  flushDiskType=ASYNC_FLUSH
  # 末尾追加,NameServer节点列表,使用分号分割
  namesrvAddr=192.168.31.200:9876;192.168.31.201:9876
  ```

* 双主启动:在a,b的broker上执行:`sh bin/mqbroker -c ./conf/2m-noslave/broker-a.properties`,-c表示使用哪个配置文件

* 查看集群状态:`sh mqadmin clusterList -n nameserverip:port`

* 使用RocketMQ自带的tools.sh工具通过生成演示数据来测试MQ生产者实际的运行情况

  ```shell
  export NAME_ADDR = 192.168.0.150:9876(nameserver地址)
  sh tools.sh org.apache.rocketmq.example.quickstart.Producer
  ```

* 如果broker-a,broker-b交替出现,说明集群已经生效

* 测试消费者

  ```shell
  export NAME_ADDR = 192.168.0.150:9876(nameserver地址)
  sh tools.sh org.apache.rocketmq.example.quickstart.Consumer
  ```

  







# 事务消息



* RocketMQ提供了事务消息,通过事务消息就能达到分布式事务的最终一致

* 事务消息交互流程

  ![](Rocket01.png)

* 半事务消息:暂不能投递的消息,发送方已经成功地将消息发送到了RocketMQ服务端,但是服务端未收到生产者对该消息的二次确认,此时该消息被标记成`暂不能投递`状态,处于该种状态下的消息即半事务消息

* 消息回查:由于网络闪断,生产者应用重启等原因,导致某条事务消息的二次确认丢失,RocketMQ服务端通过扫描发现某条消息长期处于`半事务消息`时,需主动向生产者询问该消息的最终状态(Commit是Rollback),该询问过程即消息回查

* 事务消息发送:

  * 发送方将半事务消息发送至RocketMQ服务端
  * RocketMQ服务端将消息持久化之后,向发送方返回Ack确认消息发送成功,此时消息为半事务消息
  * 发送方开始执行本地事务逻辑
  * 发送方根据本地事务执行结果向服务端提交二次确认(Commit或Rollback),服务端收到Commit 状态则将半事务消息标记为可投递,订阅方最终将收到该消息;服务端收到 Rollback 状态则删除半事务消息,订阅方将不会接受该消息

* 事务消息回查:

  * 在断网或者是应用重启的特殊情况下,上述步骤4提交的二次确认最终未到达服务端,经过固定时间后服务端将对该消息发起消息回查
  * 发送方收到消息回查后,需要检查对应消息的本地事务执行的最终结果
  * 发送方根据检查得到的本地事务的最终状态再次提交二次确认,服务端仍按照步骤4对半事务消息进行操作
