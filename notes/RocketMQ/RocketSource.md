# RocketSource



# 源码拉取



* 从官方仓库 <https://github.com/apache/rocketmq> `clone`或者`download`源码
* 源码目录结构:
  * broker: broker 模块.broke 启动进程
  * client : 消息客户端,包含消息生产者、消息消费者相关类
  * common : 公共包
  * dev : 开发者信息,非源代码
  * distribution : 部署实例文件夹,非源代码
  * example: RocketMQ 例代码
  * filter : 消息过滤相关基础类
  * filtersrv: 消息过滤服务器实现相关类,Filter启动进程
  * logappender: 日志实现相关类
  * namesrv: NameServer实现相关类,NameServer启动进程
  * openmessageing: 消息开放标准
  * remoting: 远程通信模块,给予Netty
  * srcutil: 服务工具类
  * store: 消息存储实现相关类
  * style: checkstyle相关实现
  * test: 测试相关类
  * tools: 工具类,监控命令相关实现类
* 根据官方文档,导入IDE中之后进行调试



# 调试



## 启动Nameserver



* 进入rocketmq目录,执行`clean install -Dmaven.test.skip=true`
* 创建`conf`配置文件夹,从`distribution`拷贝`broker.conf`和`logback_broker.xml`和`logback_namesrv.xml`
* 展开namesrv模块,右键NamesrvStartup.java进行启动,此时启动不成功
* 打开Run Configuration,找到NamesrvStartup启动项,点击找到Environment,添加:`ROCKETMQ_HOME=当前源码目录`
* 重启NamesrvStartup.java



## 启动Broker



* 修改broker.conf

  ```shell
  brokerClusterName = DefaultCluster
  brokerName = broker-a
  brokerId = 0
  # namesrvAddr地址
  namesrvAddr=127.0.0.1:9876
  deleteWhen = 04
  fileReservedTime = 48
  brokerRole = ASYNC_MASTER
  flushDiskType = ASYNC_FLUSH
  autoCreateTopicEnable=true
  
  # 存储路径
  storePathRootDir=E:\\RocketMQ\\data\\rocketmq\\dataDir
  # commitLog路径
  storePathCommitLog=E:\\RocketMQ\\data\\rocketmq\\dataDir\\commitlog
  # 消息队列存储路径
  storePathConsumeQueue=E:\\RocketMQ\\data\\rocketmq\\dataDir\\consumequeue
  # 消息索引存储路径
  storePathIndex=E:\\RocketMQ\\data\\rocketmq\\dataDir\\index
  # checkpoint文件路径
  storeCheckpoint=E:\\RocketMQ\\data\\rocketmq\\dataDir\\checkpoint
  # abort文件存储路径
  abortFile=E:\\RocketMQ\\data\\rocketmq\\dataDir\\abort
  ```

* 启动BrokerStartup.java,失败

* 在Run Configuration的Arguments的Program Arguments中添加`broker.conf`地址:`-c rocket源码地址\conf\broker.conf`

* 在Run Configuration中配置`broker.conf`和`ROCKETMQ_HOME`,同配置NamesrvStartup

* 再次启动BrokerStartup.java



## 发送消息



* 进入example模块的`org.apache.rocketmq.example.quickstart`
* 指定Namesrv地址

```java
DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
producer.setNamesrvAddr("127.0.0.1:9876");
```

* 运行`main`方法,发送消息



## 消费消息



* 进入example模块的`org.apache.rocketmq.example.quickstart`
* 指定Namesrv地址

```java
DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name_4");
consumer.setNamesrvAddr("127.0.0.1:9876");
```

* 运行`main`方法,消费消息



# NameServer



## 架构设计



![](img/010.jpg)



* 消息中间件的设计思路一般是基于主题订阅发布的机制,Producer发送某一个主题到消息服务器,消息服务器负责将消息持久化存储,Consumer订阅该兴趣的主题,消息服务器根据订阅信息(路由信息)将消息推送到消费者(Push模式)或者消费者主动向消息服务器拉去(Pull模式),从而实现消息生产者与消息消费者解耦
* Broker消息服务器在启动的时向所有NameServer注册,Producer在发送消息时之前先从NameServer获取Broker服务器地址列表,然后根据负载均衡算法从列表中选择一台服务器进行发送
* NameServer与每台Broker保持长连接,并间隔30S检测Broker是否存活,如果检测到Broker宕机,则从路由注册表中删除.但是路由变化不会马上通知消息生产者,这样是为了降低NameServer实现的复杂度,在消息发送端提供容错机制保证消息发送的可用性
* NameServer本身的高可用是通过部署多台NameServer来实现,但彼此之间不通讯,也就是NameServer服务器之间在某一个时刻的数据并不完全相同,但这对消息发送并不会造成任何影响



## 启动流程



![](img/011.png)



* 启动类: `org.apache.rocketmq.namesrv.NamesrvStartup`
* NamesrvController#createNamesrvController: 解析配置文件,填充NameServerConfig、NettyServerConfig属性值,并创建NamesrvController

```java
// 创建NamesrvConfig
final NamesrvConfig namesrvConfig = new NamesrvConfig();
// 创建NettyServerConfig
final NettyServerConfig nettyServerConfig = new NettyServerConfig();
// 设置启动端口号
nettyServerConfig.setListenPort(9876);
// 解析启动-c参数
if (commandLine.hasOption('c')) {
    String file = commandLine.getOptionValue('c');
    if (file != null) {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        properties = new Properties();
        properties.load(in);
        MixAll.properties2Object(properties, namesrvConfig);
        MixAll.properties2Object(properties, nettyServerConfig);

        namesrvConfig.setConfigStorePath(file);

        System.out.printf("load config properties file OK, %s%n", file);
        in.close();
    }
}
// 解析启动-p参数
if (commandLine.hasOption('p')) {
    InternalLogger console = InternalLoggerFactory.getLogger(LoggerName.NAMESRV_CONSOLE_NAME);
    MixAll.printObjectProperties(console, namesrvConfig);
    MixAll.printObjectProperties(console, nettyServerConfig);
    System.exit(0);
}
// 将启动参数填充到namesrvConfig,nettyServerConfig
MixAll.properties2Object(ServerUtil.commandLine2Properties(commandLine), namesrvConfig);

// 创建NameServerController
final NamesrvController controller = new NamesrvController(namesrvConfig, nettyServerConfig);
```

* NamesrvConfig

```java
// rocketmq主目录
private String rocketmqHome = System.getProperty(MixAll.ROCKETMQ_HOME_PROPERTY, System.getenv(MixAll.ROCKETMQ_HOME_ENV));
// NameServer存储KV配置属性的持久化路径
private String kvConfigPath = System.getProperty("user.home") + File.separator + "namesrv" + File.separator + "kvConfig.json";
// nameServer默认配置文件路径
private String configStorePath = System.getProperty("user.home") + File.separator + "namesrv" + File.separator + "namesrv.properties";
private String productEnvName = "center";
private boolean clusterTest = false;
// 是否支持顺序消息
private boolean orderMessageEnable = false;
```

* NettyServerConfig

```java
// NameServer监听端口,该值默认会被初始化为9876
private int listenPort = 8888;
// Netty业务线程池线程个数
private int serverWorkerThreads = 8;
// Netty public任务线程池线程个数,根据业务类型会创建不同的线程池,比如消息发送、消费等,如果该业务类型未注册线程池,则由public线程池执行
private int serverCallbackExecutorThreads = 0;
// IO线程池个数,主要是NameServer、Broker端解析请求、返回相应的线程个数,这类线程主要是处理网路请求的,解析请求包,然后转发到各个业务线程池完成具体的操作,然后将结果返回给调用方
private int serverSelectorThreads = 3;
// send oneway消息请求并发读,Broker端参数
private int serverOnewaySemaphoreValue = 256;
// 异步消息发送最大并发度
private int serverAsyncSemaphoreValue = 64;
// 网络连接最大的空闲时间,默认120s
private int serverChannelMaxIdleTimeSeconds = 120;
// 网络socket发送缓冲区大小
private int serverSocketSndBufSize = NettySystemConfig.socketSndbufSize;
// 网络接收端缓存区大小
private int serverSocketRcvBufSize = NettySystemConfig.socketRcvbufSize;
// ByteBuffer是否开启缓存
private boolean serverPooledByteBufAllocatorEnable = true;
// 是否启用Epoll IO模型
private boolean useEpollNativeSelector = false;
```

* NamesrvController#initialize:根据启动属性创建NamesrvController实例,并初始化该实例.NameServerController实例为NameServer核心控制器

```java
public boolean initialize() {
    // 加载KV配置
    this.kvConfigManager.load();
    // 创建NettyServer网络处理对象
    this.remotingServer = new NettyRemotingServer(this.nettyServerConfig, this.brokerHousekeepingService);
    // 开启定时任务:每隔10s扫描一次Broker,移除不活跃的Broker
    this.remotingExecutor =
        Executors.newFixedThreadPool(nettyServerConfig.getServerWorkerThreads(), new ThreadFactoryImpl("RemotingExecutorThread_"));
    this.registerProcessor();
    this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {
            NamesrvController.this.routeInfoManager.scanNotActiveBroker();
        }
    }, 5, 10, TimeUnit.SECONDS);
    //开启定时任务:每隔10min打印一次KV配置
    this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

        @Override
        public void run() {
            NamesrvController.this.kvConfigManager.printAllPeriodically();
        }
    }, 1, 10, TimeUnit.MINUTES);
    return true;
}
```

* NamesrvStartup#start: 在JVM进程关闭之前,先将线程池关闭,及时释放资源

```java
// 注册JVM钩子函数代码
Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(log, new Callable<Void>() {
    @Override
    public Void call() throws Exception {
        // 释放资源
        controller.shutdown();
        return null;
    }
}));
```



## 路由管理



* NameServer的主要作用是为消息的生产者和消息消费者提供关于主题Topic的路由信息,那么NameServer需要存储路由的基础信息,还要管理Broker节点,包括路由注册、路由删除等

* RouteInfoManager: 路由元信息

  ```java
  // Topic消息队列路由信息,消息发送时根据路由表进行负载均衡
  private final HashMap<String/* topic */, List<QueueData>> topicQueueTable;
  // Broker基础信息,包括brokerName、所属集群名称、主备Broker地址
  private final HashMap<String/* brokerName */, BrokerData> brokerAddrTable;
  // Broker集群信息,存储集群中所有Broker名称
  private final HashMap<String/* clusterName */, Set<String/* brokerName */>> clusterAddrTable;
  // Broker状态信息,NameServer每次收到心跳包会替换该信息,BrokerLiveInfo中的lastUpdateTimestamp存储上次收到Broker心跳包的时间
  private final HashMap<String/* brokerAddr */, BrokerLiveInfo> brokerLiveTable;
  // Broker上的FilterServer列表,用于类模式消息过滤
  private final HashMap<String/* brokerAddr */, List<String>/* Filter Server */> filterServerTable;
  ```




![](img/012.png)



![](img/013.png)



### 路由注册



![](img/014.png)



* BrokerController#start: 发送心跳包.RocketMQ路由注册是通过Broker与NameServer的心跳功能实现的.Broker启动时向集群中所有的NameServer发送心跳信息,每隔30s向集群中所有NameServer发送心跳包,NameServer收到心跳包时会更新brokerLiveTable缓存中BrokerLiveInfo的lastUpdataTimeStamp信息,然后NameServer每隔10s扫描brokerLiveTable,如果连续120S没有收到心跳包,NameServer将移除Broker的路由信息同时关闭Socket连接

  ```java
  // 注册Broker信息
  this.registerBrokerAll(true, false, true);
  // 每隔30s上报Broker信息到NameServer
  this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
  
      @Override
      public void run() {
          try {
              BrokerController.this.registerBrokerAll(true, false, brokerConfig.isForceRegister());
          } catch (Throwable e) {
              log.error("registerBrokerAll Exception", e);
          }
      }
  }, 1000 * 10, Math.max(10000, Math.min(brokerConfig.getRegisterNameServerPeriod(), 60000)), 
                                                    TimeUnit.MILLISECONDS);
  
  ```

* BrokerOuterAPI#registerBrokerAll

  ```java
  // 获得nameServer地址信息
  List<String> nameServerAddressList = this.remotingClient.getNameServerAddressList();
  // 遍历所有nameserver列表
  if (nameServerAddressList != null && nameServerAddressList.size() > 0) {
      // 封装请求头
      final RegisterBrokerRequestHeader requestHeader = new RegisterBrokerRequestHeader();
      requestHeader.setBrokerAddr(brokerAddr);
      requestHeader.setBrokerId(brokerId);
      requestHeader.setBrokerName(brokerName);
      requestHeader.setClusterName(clusterName);
      requestHeader.setHaServerAddr(haServerAddr);
      requestHeader.setCompressed(compressed);
      // 封装请求体
      RegisterBrokerBody requestBody = new RegisterBrokerBody();
      requestBody.setTopicConfigSerializeWrapper(topicConfigWrapper);
      requestBody.setFilterServerList(filterServerList);
      final byte[] body = requestBody.encode(compressed);
      final int bodyCrc32 = UtilAll.crc32(body);
      requestHeader.setBodyCrc32(bodyCrc32);
      final CountDownLatch countDownLatch = new CountDownLatch(nameServerAddressList.size());
      for (final String namesrvAddr : nameServerAddressList) {
          brokerOuterExecutor.execute(new Runnable() {
              @Override
              public void run() {
                  try {
                      // 分别向NameServer注册
                      RegisterBrokerResult result = registerBroker(namesrvAddr,oneway, timeoutMills,requestHeader,body);
                      if (result != null) {
                          registerBrokerResultList.add(result);
                      }
  
                      log.info("register broker[{}]to name server {} OK", brokerId, namesrvAddr);
                  } catch (Exception e) {
                      log.warn("registerBroker Exception, {}", namesrvAddr, e);
                  } finally {
                      countDownLatch.countDown();
                  }
              }
          });
      }
  
      try {
          countDownLatch.await(timeoutMills, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
      }
  }
  ```

* BrokerOutAPI#registerBroker

  ```java
  if (oneway) {
      try {
          this.remotingClient.invokeOneway(namesrvAddr, request, timeoutMills);
      } catch (RemotingTooMuchRequestException e) {
          // Ignore
      }
      return null;
  }
  RemotingCommand response = this.remotingClient.invokeSync(namesrvAddr, request, timeoutMills);
  ```

  

### 处理心跳包



![](img/015.png)



* `org.apache.rocketmq.namesrv.processor.DefaultRequestProcessor`网路处理类解析请求类型,如果请求类型是为***REGISTER_BROKER***,则将请求转发到`RouteInfoManager#regiesterBroker`
* DefaultRequestProcessor#processRequest

```java
// 判断是注册Broker信息
case RequestCode.REGISTER_BROKER:
Version brokerVersion = MQVersion.value2Version(request.getVersion());
if (brokerVersion.ordinal() >= MQVersion.Version.V3_0_11.ordinal()) {
    return this.registerBrokerWithFilterServer(ctx, request);
} else {
    // 注册Broker信息
    return this.registerBroker(ctx, request);
}
```

* DefaultRequestProcessor#registerBroker

```java
RegisterBrokerResult result = this.namesrvController.getRouteInfoManager().registerBroker(
    requestHeader.getClusterName(),
    requestHeader.getBrokerAddr(),
    requestHeader.getBrokerName(),
    requestHeader.getBrokerId(),
    requestHeader.getHaServerAddr(),
    topicConfigWrapper,
    null,
    ctx.channel()
);
```

* RouteInfoManager#registerBroker: 维护路由信息

```java
// 加锁
this.lock.writeLock().lockInterruptibly();
// 维护clusterAddrTable
Set<String> brokerNames = this.clusterAddrTable.get(clusterName);
if (null == brokerNames) {
    brokerNames = new HashSet<String>();
    this.clusterAddrTable.put(clusterName, brokerNames);
}
brokerNames.add(brokerName);
```

```java
// 维护brokerAddrTable
BrokerData brokerData = this.brokerAddrTable.get(brokerName);
// 第一次注册,则创建brokerData
if (null == brokerData) {
    registerFirst = true;
    brokerData = new BrokerData(clusterName, brokerName, new HashMap<Long, String>());
    this.brokerAddrTable.put(brokerName, brokerData);
}
// 非第一次注册,更新Broker
Map<Long, String> brokerAddrsMap = brokerData.getBrokerAddrs();
Iterator<Entry<Long, String>> it = brokerAddrsMap.entrySet().iterator();
while (it.hasNext()) {
    Entry<Long, String> item = it.next();
    if (null != brokerAddr && brokerAddr.equals(item.getValue()) && brokerId != item.getKey()) {
        it.remove();
    }
}
String oldAddr = brokerData.getBrokerAddrs().put(brokerId, brokerAddr);
registerFirst = registerFirst || (null == oldAddr);
```

```java
// 维护topicQueueTable
if (null != topicConfigWrapper && MixAll.MASTER_ID == brokerId) {
    if (this.isBrokerTopicConfigChanged(brokerAddr, topicConfigWrapper.getDataVersion()) || 
        registerFirst) {
        ConcurrentMap<String, TopicConfig> tcTable = topicConfigWrapper.getTopicConfigTable();
        if (tcTable != null) {
            for (Map.Entry<String, TopicConfig> entry : tcTable.entrySet()) {
                this.createAndUpdateQueueData(brokerName, entry.getValue());
            }
        }
    }
}
```

* RouteInfoManager#createAndUpdateQueueData

```java
private void createAndUpdateQueueData(final String brokerName, final TopicConfig topicConfig) {
    // 创建QueueData
    QueueData queueData = new QueueData();
    queueData.setBrokerName(brokerName);
    queueData.setWriteQueueNums(topicConfig.getWriteQueueNums());
    queueData.setReadQueueNums(topicConfig.getReadQueueNums());
    queueData.setPerm(topicConfig.getPerm());
    queueData.setTopicSynFlag(topicConfig.getTopicSysFlag());
    // 获得topicQueueTable中队列集合
    List<QueueData> queueDataList = this.topicQueueTable.get(topicConfig.getTopicName());
    // topicQueueTable为空,则直接添加queueData到队列集合
    if (null == queueDataList) {
        queueDataList = new LinkedList<QueueData>();
        queueDataList.add(queueData);
        this.topicQueueTable.put(topicConfig.getTopicName(), queueDataList);
        log.info("new topic registered, {} {}", topicConfig.getTopicName(), queueData);
    } else {
        // 判断是否是新的队列
        boolean addNewOne = true;
        Iterator<QueueData> it = queueDataList.iterator();
        while (it.hasNext()) {
            QueueData qd = it.next();
            // 如果brokerName相同,代表不是新的队列
            if (qd.getBrokerName().equals(brokerName)) {
                if (qd.equals(queueData)) {
                    addNewOne = false;
                } else {
                    log.info("topic changed, {} OLD: {} NEW: {}", topicConfig.getTopicName(), qd,
                             queueData);
                    it.remove();
                }
            }
        }
        // 如果是新的队列,则添加队列到queueDataList
        if (addNewOne) {
            queueDataList.add(queueData);
        }
    }
}
```

```java
// 维护brokerLiveTable
BrokerLiveInfo prevBrokerLiveInfo = this.brokerLiveTable.put(brokerAddr,new BrokerLiveInfo(
    System.currentTimeMillis(),
    topicConfigWrapper.getDataVersion(),
    channel,
    haServerAddr));
```

```java
// 维护filterServerList
if (filterServerList != null) {
    if (filterServerList.isEmpty()) {
        this.filterServerTable.remove(brokerAddr);
    } else {
        this.filterServerTable.put(brokerAddr, filterServerList);
    }
}

if (MixAll.MASTER_ID != brokerId) {
    String masterAddr = brokerData.getBrokerAddrs().get(MixAll.MASTER_ID);
    if (masterAddr != null) {
        BrokerLiveInfo brokerLiveInfo = this.brokerLiveTable.get(masterAddr);
        if (brokerLiveInfo != null) {
            result.setHaServerAddr(brokerLiveInfo.getHaServerAddr());
            result.setMasterAddr(masterAddr);
        }
    }
}
```



### 路由删除



* Broker每隔30s向NameServer发送一个心跳包,心跳包包含BrokerId,Broker地址,Broker名称,Broker所属集群名称,Broker关联的FilterServer列表.NameServer会每隔10s扫描brokerLiveTable状态表,如果BrokerLive的lastUpdateTimestamp的时间戳距当前时间超过120s,则认为Broker失效,移除该Broker,关闭与Broker连接,同时更新topicQueueTable`、`brokerAddrTable`、`brokerLiveTable`、`filterServerTable

* RocketMQ有两个触发点来删除路由信息:
  * NameServer定期扫描brokerLiveTable检测上次心跳包与当前系统的时间差,如果时间超过120s,则需要移除broker
  * Broker在正常关闭的情况下,会执行unregisterBroker指令
* 这两种方式路由删除的方法都是一样的,就是从相关路由表中删除与该broker相关的信息



![](img/016.png)



* NamesrvController#initialize

```java
// 每隔10s扫描一次为活跃Broker
this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

    @Override
    public void run() {
        NamesrvController.this.routeInfoManager.scanNotActiveBroker();
    }
}, 5, 10, TimeUnit.SECONDS);
```

* RouteInfoManager#scanNotActiveBroker

```java
public void scanNotActiveBroker() {
    // 获得brokerLiveTable
    Iterator<Entry<String, BrokerLiveInfo>> it = this.brokerLiveTable.entrySet().iterator();
    // 遍历brokerLiveTable
    while (it.hasNext()) {
        Entry<String, BrokerLiveInfo> next = it.next();
        long last = next.getValue().getLastUpdateTimestamp();
        // 如果收到心跳包的时间距当时时间是否超过120s
        if ((last + BROKER_CHANNEL_EXPIRED_TIME) < System.currentTimeMillis()) {
            // 关闭连接
            RemotingUtil.closeChannel(next.getValue().getChannel());
            // 移除broker
            it.remove();
            // 维护路由表
            this.onChannelDestroy(next.getKey(), next.getValue().getChannel());
        }
    }
}
```

* RouteInfoManager#onChannelDestroy

```java
// 申请写锁,根据brokerAddress从brokerLiveTable和filterServerTable移除
this.lock.writeLock().lockInterruptibly();
this.brokerLiveTable.remove(brokerAddrFound);
this.filterServerTable.remove(brokerAddrFound);
```

```java
// 维护brokerAddrTable
String brokerNameFound = null;
boolean removeBrokerName = false;
Iterator<Entry<String, BrokerData>> itBrokerAddrTable =this.brokerAddrTable.entrySet().iterator();
while (itBrokerAddrTable.hasNext() && (null == brokerNameFound)) {
    BrokerData brokerData = itBrokerAddrTable.next().getValue();
    Iterator<Entry<Long, String>> it = brokerData.getBrokerAddrs().entrySet().iterator();
    while (it.hasNext()) {
        Entry<Long, String> entry = it.next();
        Long brokerId = entry.getKey();
        String brokerAddr = entry.getValue();
        // 根据broker地址移除brokerAddr
        if (brokerAddr.equals(brokerAddrFound)) {
            brokerNameFound = brokerData.getBrokerName();
            it.remove();
            log.info("remove brokerAddr[{}, {}] from brokerAddrTable, because channel destroyed",
                     brokerId, brokerAddr);
            break;
        }
    }
    // 如果当前主题只包含待移除的broker,则移除该topic
    if (brokerData.getBrokerAddrs().isEmpty()) {
        removeBrokerName = true;
        itBrokerAddrTable.remove();
        log.info("remove brokerName[{}] from brokerAddrTable, because channel destroyed",
                 brokerData.getBrokerName());
    }
}
```

```java
// 维护clusterAddrTable
if (brokerNameFound != null && removeBrokerName) {
    Iterator<Entry<String, Set<String>>> it = this.clusterAddrTable.entrySet().iterator();
    while (it.hasNext()) {
        Entry<String, Set<String>> entry = it.next();
        String clusterName = entry.getKey();
        // 获得集群中brokerName集合
        Set<String> brokerNames = entry.getValue();
        // 从brokerNames中移除brokerNameFound
        boolean removed = brokerNames.remove(brokerNameFound);
        if (removed) {
            log.info("remove brokerName[{}], clusterName[{}] from clusterAddrTable, because channel destroyed",
                     brokerNameFound, clusterName);

            if (brokerNames.isEmpty()) {
                log.info("remove the clusterName[{}] from clusterAddrTable, because channel destroyed and no broker in this cluster",
                         clusterName);
                // 如果集群中不包含任何broker,则移除该集群
                it.remove();
            }

            break;
        }
    }
}
```

```java
// 维护topicQueueTable队列
if (removeBrokerName) {
    // 遍历topicQueueTable
    Iterator<Entry<String, List<QueueData>>> itTopicQueueTable =
        this.topicQueueTable.entrySet().iterator();
    while (itTopicQueueTable.hasNext()) {
        Entry<String, List<QueueData>> entry = itTopicQueueTable.next();
        // 主题名称
        String topic = entry.getKey();
        // 队列集合
        List<QueueData> queueDataList = entry.getValue();
        // 遍历该主题队列
        Iterator<QueueData> itQueueData = queueDataList.iterator();
        while (itQueueData.hasNext()) {
            // 从队列中移除为活跃broker信息
            QueueData queueData = itQueueData.next();
            if (queueData.getBrokerName().equals(brokerNameFound)) {
                itQueueData.remove();
                log.info("remove topic[{} {}], from topicQueueTable, because channel destroyed",
                         topic, queueData);
            }
        }
        // 如果该topic的队列为空,则移除该topic
        if (queueDataList.isEmpty()) {
            itTopicQueueTable.remove();
            log.info("remove topic[{}] all queue, from topicQueueTable, because channel destroyed",
                     topic);
        }
    }
}
```

```java
// 释放写锁
finally {
    this.lock.writeLock().unlock();
}
```



### 路由发现



* RocketMQ路由发现是非实时的,当Topic路由出现变化后,NameServer不会主动推送给客户端,而是由客户端定时拉取
* DefaultRequestProcessor#getRouteInfoByTopic

```java
public RemotingCommand getRouteInfoByTopic(ChannelHandlerContext ctx,
                                           RemotingCommand request) throws RemotingCommandException {
    final RemotingCommand response = RemotingCommand.createResponseCommand(null);
    final GetRouteInfoRequestHeader requestHeader =
        (GetRouteInfoRequestHeader) request.decodeCommandCustomHeader(GetRouteInfoRequestHeader.class);
    // 调用RouteInfoManager的方法,从路由表topicQueueTable、brokerAddrTable、filterServerTable中分别填充TopicRouteData的List<QueueData>、List<BrokerData>、filterServer
    TopicRouteData topicRouteData = this.namesrvController.getRouteInfoManager().pickupTopicRouteData(requestHeader.getTopic());
    // 如果找到主题对应你的路由信息并且该主题为顺序消息,则从NameServer KVConfig中获取关于顺序消息相关的配置填充路由信息
    if (topicRouteData != null) {
        if (this.namesrvController.getNamesrvConfig().isOrderMessageEnable()) {
            String orderTopicConf =
                this.namesrvController.getKvConfigManager().getKVConfig(NamesrvUtil.NAMESPACE_ORDER_TOPIC_CONFIG,
                                                                        requestHeader.getTopic());
            topicRouteData.setOrderTopicConf(orderTopicConf);
        }

        byte[] content = topicRouteData.encode();
        response.setBody(content);
        response.setCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }

    response.setCode(ResponseCode.TOPIC_NOT_EXIST);
    response.setRemark("No topic route info in name server for the topic: " + requestHeader.getTopic()
                       + FAQUrl.suggestTodo(FAQUrl.APPLY_TOPIC_URL));
    return response;
}
```



# Producer



* 消息生产者的代码都在client模块中,相对于RocketMQ来讲,消息生产者就是客户端,也是消息的提供者



## MQAdmin



```java
// 创建主题
void createTopic(final String key, final String newTopic, final int queueNum) throws MQClientException;
// 根据时间戳从队列中查找消息偏移量
long searchOffset(final MessageQueue mq, final long timestamp);
// 查找消息队列中最大的偏移量
long maxOffset(final MessageQueue mq) throws MQClientException;
// 查找消息队列中最小的偏移量
long minOffset(final MessageQueue mq) ;
// 根据偏移量查找消息
MessageExt viewMessage(final String offsetMsgId) throws RemotingException, MQBrokerException,
InterruptedException, MQClientException;
// 根据条件查找消息
QueryResult queryMessage(final String topic, final String key, final int maxNum, final long begin,
                         final long end) throws MQClientException, InterruptedException;
// 根据消息ID和主题查找消息
MessageExt viewMessage(String topic,String msgId) throws RemotingException, MQBrokerException, InterruptedException, MQClientException;
```



## MQProducer



```java
// 启动
void start() throws MQClientException;
// 关闭
void shutdown();
// 查找该主题下所有消息
List<MessageQueue> fetchPublishMessageQueues(final String topic) throws MQClientException;
// 同步发送消息
SendResult send(final Message msg) throws MQClientException, RemotingException, MQBrokerException,InterruptedException;
// 同步超时发送消息
SendResult send(final Message msg, final long timeout) throws MQClientException,RemotingException, MQBrokerException, InterruptedException;
// 异步发送消息
void send(final Message msg, final SendCallback sendCallback) throws MQClientException,RemotingException, InterruptedException;
// 异步超时发送消息
void send(final Message msg, final SendCallback sendCallback, final long timeout) throws MQClientException, RemotingException, InterruptedException;
// 发送单向消息
void sendOneway(final Message msg) throws MQClientException, RemotingException,InterruptedException;
// 选择指定队列同步发送消息
SendResult send(final Message msg, final MessageQueue mq) throws MQClientException,RemotingException, MQBrokerException, InterruptedException;
// 选择指定队列异步发送消息
void send(final Message msg, final MessageQueue mq, final SendCallback sendCallback) throws MQClientException, RemotingException, InterruptedException;
// 选择指定队列单项发送消息
void sendOneway(final Message msg, final MessageQueue mq) throws MQClientException,RemotingException, InterruptedException;
// 批量发送消息
SendResult send(final Collection<Message> msgs) throws MQClientException, RemotingException, MQBrokerException, InterruptedException;
```



## DefaultMQProducer



```java
// 生产者所属组
private String producerGroup;
// 默认Topic
private String createTopicKey = TopicValidator.AUTO_CREATE_TOPIC_KEY_TOPIC;
// 默认主题在每一个Broker队列数量
private volatile int defaultTopicQueueNums = 4;
// 发送消息默认超时时间,默认3s
private int sendMsgTimeout = 3000;
// 消息体超过该值则启用压缩,默认4k
private int compressMsgBodyOverHowmuch = 1024 * 4;
// 同步方式发送消息重试次数,默认为2,总共执行3次
private int retryTimesWhenSendFailed = 2;
// 异步方法发送消息重试次数,默认为2
private int retryTimesWhenSendAsyncFailed = 2;
// 消息重试时选择另外一个Broker时,是否不等待存储结果就返回,默认为false
private boolean retryAnotherBrokerWhenNotStoreOK = false;
// 允许发送的最大消息长度,默认为4M
private int maxMessageSize = 1024 * 1024 * 4; 
```



## 启动流程



![](img/017.png)



* DefaultMQProducerImpl#start

```java
// 检查生产者组是否满足要求
this.checkConfig();
// 更改当前instanceName为进程ID
if (!this.defaultMQProducer.getProducerGroup().equals(MixAll.CLIENT_INNER_PRODUCER_GROUP)) {
    this.defaultMQProducer.changeInstanceNameToPID();
}
// 获得MQ客户端实例
this.mQClientFactory = MQClientManager.getInstance().getAndCreateMQClientInstance(this.defaultMQProducer, rpcHook);
```

* 整个JVM中只存在一个MQClientManager实例,维护一个MQClientInstance缓存表

```java
ConcurrentMap<String/* clientId */, MQClientInstance> factoryTable = new ConcurrentHashMap<String,MQClientInstance>();
```

* 同一个clientId只会创建一个MQClientInstance,MQClientInstance封装了RocketMQ网络处理API,是消息生产者和消息消费者与NameServer、Broker打交道的网络通道
* MQClientManager#getAndCreateMQClientInstance

```java
public MQClientInstance getAndCreateMQClientInstance(final ClientConfig clientConfig, 
                                                     RPCHook rpcHook) {
    // 构建客户端ID
    String clientId = clientConfig.buildMQClientId();
    // 根据客户端ID或者客户端实例
    MQClientInstance instance = this.factoryTable.get(clientId);
    // 实例如果为空就创建新的实例,并添加到实例表中
    if (null == instance) {
        instance =
            new MQClientInstance(clientConfig.cloneClientConfig(),
                                 this.factoryIndexGenerator.getAndIncrement(), clientId, rpcHook);
        MQClientInstance prev = this.factoryTable.putIfAbsent(clientId, instance);
        if (prev != null) {
            instance = prev;
            log.warn("Returned Previous MQClientInstance for clientId:[{}]", clientId);
        } else {
            log.info("Created new MQClientInstance for clientId:[{}]", clientId);
        }
    }

    return instance;
}
```

* DefaultMQProducerImpl#start

```java
// 注册当前生产者到到MQClientInstance管理中,方便后续调用网路请求
boolean registerOK = mQClientFactory.registerProducer(this.defaultMQProducer.getProducerGroup(), this);
if (!registerOK) {
    this.serviceState = ServiceState.CREATE_JUST;
    throw new MQClientException("The producer group[" + this.defaultMQProducer.getProducerGroup()
                                + "] has been created before, specify another name please." + FAQUrl.suggestTodo(FAQUrl.GROUP_NAME_DUPLICATE_URL),
                                null);
}
// 启动生产者
if (startFactory) {
    mQClientFactory.start();
}
```



## 消息发送



![](img/018.png)



* DefaultMQProducerImpl#send(Message msg): 发送消息
* DefaultMQProducerImpl#send(Message msg,long timeout): 发送消息,默认超时时间为3s
* DefaultMQProducerImpl#sendDefaultImpl

```java
// 校验消息
Validators.checkMessage(msg, this.defaultMQProducer);
```



### 验证消息



* Validators#checkMessage

```java
public static void checkMessage(Message msg, DefaultMQProducer defaultMQProducer)
    throws MQClientException {
    // 判断是否为空
    if (null == msg) {
        throw new MQClientException(ResponseCode.MESSAGE_ILLEGAL, "the message is null");
    }
    //  校验主题
    Validators.checkTopic(msg.getTopic());

    // 校验消息体
    if (null == msg.getBody()) {
        throw new MQClientException(ResponseCode.MESSAGE_ILLEGAL, "the message body is null");
    }

    if (0 == msg.getBody().length) {
        throw new MQClientException(ResponseCode.MESSAGE_ILLEGAL, "the message body length is zero");
    }

    if (msg.getBody().length > defaultMQProducer.getMaxMessageSize()) {
        throw new MQClientException(ResponseCode.MESSAGE_ILLEGAL,
                                    "the message body size over max value, MAX: " + defaultMQProducer.getMaxMessageSize());
    }
}
```



### 查找路由



* DefaultMQProducerImpl#tryToFindTopicPublishInfo

```java
private TopicPublishInfo tryToFindTopicPublishInfo(final String topic) {
    // 从缓存中获得主题的路由信息
    TopicPublishInfo topicPublishInfo = this.topicPublishInfoTable.get(topic);
    // 路由信息为空,则从NameServer获取路由
    if (null == topicPublishInfo || !topicPublishInfo.ok()) {
        this.topicPublishInfoTable.putIfAbsent(topic, new TopicPublishInfo());
        this.mQClientFactory.updateTopicRouteInfoFromNameServer(topic);
        topicPublishInfo = this.topicPublishInfoTable.get(topic);
    }

    if (topicPublishInfo.isHaveTopicRouterInfo() || topicPublishInfo.ok()) {
        return topicPublishInfo;
    } else {
        // 如果未找到当前主题的路由信息,则用默认主题继续查找
        this.mQClientFactory.updateTopicRouteInfoFromNameServer(topic, true, this.defaultMQProducer);
        topicPublishInfo = this.topicPublishInfoTable.get(topic);
        return topicPublishInfo;
    }
}
```

* TopicPublishInfo

```java
public class TopicPublishInfo {
    //是否是顺序消息
    private boolean orderTopic = false;
    private boolean haveTopicRouterInfo = false; 
    //该主题消息队列
    private List<MessageQueue> messageQueueList = new ArrayList<MessageQueue>();
    //每选择一次消息队列,该值+1
    private volatile ThreadLocalIndex sendWhichQueue = new ThreadLocalIndex();
    //关联Topic路由元信息
    private TopicRouteData topicRouteData;
}
```

* MQClientInstance#updateTopicRouteInfoFromNameServer

```java
TopicRouteData topicRouteData;
// 使用默认主题从NameServer获取路由信息
if (isDefault && defaultMQProducer != null) {
    topicRouteData = this.mQClientAPIImpl.getDefaultTopicRouteInfoFromNameServer(defaultMQProducer.getCreateTopicKey(),
                                                                                 1000 * 3);
    if (topicRouteData != null) {
        for (QueueData data : topicRouteData.getQueueDatas()) {
            int queueNums = Math.min(defaultMQProducer.getDefaultTopicQueueNums(), data.getReadQueueNums());
            data.setReadQueueNums(queueNums);
            data.setWriteQueueNums(queueNums);
        }
    }
} else {
    // 使用指定主题从NameServer获取路由信息
    topicRouteData = this.mQClientAPIImpl.getTopicRouteInfoFromNameServer(topic, 1000 * 3);
}
```

* MQClientInstance#updateTopicRouteInfoFromNameServer

```java
// 判断路由是否需要更改
TopicRouteData old = this.topicRouteTable.get(topic);
boolean changed = topicRouteDataIsChange(old, topicRouteData);
if (!changed) {
    changed = this.isNeedUpdateTopicRouteInfo(topic);
} else {
    log.info("the topic[{}] route info changed, old[{}] ,new[{}]", topic, old, topicRouteData);
}
```

* MQClientInstance#updateTopicRouteInfoFromNameServer

```java
if (changed) {
    // 将topicRouteData转换为发布队列
    TopicPublishInfo publishInfo = topicRouteData2TopicPublishInfo(topic, topicRouteData);
    publishInfo.setHaveTopicRouterInfo(true);
    // 遍历生产
    Iterator<Entry<String, MQProducerInner>> it = this.producerTable.entrySet().iterator();
    while (it.hasNext()) {
        Entry<String, MQProducerInner> entry = it.next();
        MQProducerInner impl = entry.getValue();
        if (impl != null) {
            // 生产者不为空时,更新publishInfo信息
            impl.updateTopicPublishInfo(topic, publishInfo);
        }
    }
}
```

* MQClientInstance#topicRouteData2TopicPublishInfo

```java
public static TopicPublishInfo topicRouteData2TopicPublishInfo(final String topic, final TopicRouteData route) {
    // 创建TopicPublishInfo对象
    TopicPublishInfo info = new TopicPublishInfo();
    // 关联topicRoute
    info.setTopicRouteData(route);
    // 顺序消息,更新TopicPublishInfo
    if (route.getOrderTopicConf() != null && route.getOrderTopicConf().length() > 0) {
        String[] brokers = route.getOrderTopicConf().split(";");
        for (String broker : brokers) {
            String[] item = broker.split(":");
            int nums = Integer.parseInt(item[1]);
            for (int i = 0; i < nums; i++) {
                MessageQueue mq = new MessageQueue(topic, item[0], i);
                info.getMessageQueueList().add(mq);
            }
        }

        info.setOrderTopic(true);
    } else {
        // 非顺序消息更新TopicPublishInfo
        List<QueueData> qds = route.getQueueDatas();
        Collections.sort(qds);
        // 遍历topic队列信息
        for (QueueData qd : qds) {
            // 是否是写队列
            if (PermName.isWriteable(qd.getPerm())) {
                BrokerData brokerData = null;
                // 遍历写队列Broker
                for (BrokerData bd : route.getBrokerDatas()) {
                    // 根据名称获得读队列对应的Broker
                    if (bd.getBrokerName().equals(qd.getBrokerName())) {
                        brokerData = bd;
                        break;
                    }
                }

                if (null == brokerData) {
                    continue;
                }

                if (!brokerData.getBrokerAddrs().containsKey(MixAll.MASTER_ID)) {
                    continue;
                }
                // 封装TopicPublishInfo写队列
                for (int i = 0; i < qd.getWriteQueueNums(); i++) {
                    MessageQueue mq = new MessageQueue(topic, qd.getBrokerName(), i);
                    info.getMessageQueueList().add(mq);
                }
            }
        }

        info.setOrderTopic(false);
    }
    // 返回TopicPublishInfo对象
    return info;
}
```



### 选择队列



* 默认不启用Broker故障延迟机制

* TopicPublishInfo#selectOneMessageQueue(lastBrokerName)

```java
public MessageQueue selectOneMessageQueue(final String lastBrokerName) {
    // 第一次选择队列
    if (lastBrokerName == null) {
        return selectOneMessageQueue();
    } else {
        // sendWhichQueue
        int index = this.sendWhichQueue.getAndIncrement();
        // 遍历消息队列集合
        for (int i = 0; i < this.messageQueueList.size(); i++) {
            // sendWhichQueue自增后取模
            int pos = Math.abs(index++) % this.messageQueueList.size();
            if (pos < 0)
                pos = 0;
            // 规避上次Broker队列
            MessageQueue mq = this.messageQueueList.get(pos);
            if (!mq.getBrokerName().equals(lastBrokerName)) {
                return mq;
            }
        }
        // 如果以上情况都不满足,返回sendWhichQueue取模后的队列
        return selectOneMessageQueue();
    }
}
```

* TopicPublishInfo#selectOneMessageQueue()

```java
// 第一次选择队列
public MessageQueue selectOneMessageQueue() {
    // sendWhichQueue自增
    int index = this.sendWhichQueue.getAndIncrement();
    // 对队列大小取模
    int pos = Math.abs(index) % this.messageQueueList.size();
    if (pos < 0)
        pos = 0;
    // 返回对应的队列
    return this.messageQueueList.get(pos);
}
```

* 启用Broker故障延迟机制

```java
public MessageQueue selectOneMessageQueue(final TopicPublishInfo tpInfo, final String lastBrokerName) {
    // Broker故障延迟机制
    if (this.sendLatencyFaultEnable) {
        try {
            // 对sendWhichQueue自增
            int index = tpInfo.getSendWhichQueue().getAndIncrement();
            // 对消息队列轮询获取一个队列
            for (int i = 0; i < tpInfo.getMessageQueueList().size(); i++) {
                int pos = Math.abs(index++) % tpInfo.getMessageQueueList().size();
                if (pos < 0)
                    pos = 0;
                MessageQueue mq = tpInfo.getMessageQueueList().get(pos);
                // 验证该队列是否可用
                if (latencyFaultTolerance.isAvailable(mq.getBrokerName())) {
                    // 可用
                    if (null == lastBrokerName || mq.getBrokerName().equals(lastBrokerName))
                        return mq;
                }
            }
            // 从规避的Broker中选择一个可用的Broker
            final String notBestBroker = latencyFaultTolerance.pickOneAtLeast();
            // 获得Broker的写队列集合
            int writeQueueNums = tpInfo.getQueueIdByBroker(notBestBroker);
            if (writeQueueNums > 0) {
                // 获得一个队列,指定broker和队列ID并返回
                final MessageQueue mq = tpInfo.selectOneMessageQueue();
                if (notBestBroker != null) {
                    mq.setBrokerName(notBestBroker);
                    mq.setQueueId(tpInfo.getSendWhichQueue().getAndIncrement() % writeQueueNums);
                }
                return mq;
            } else {
                latencyFaultTolerance.remove(notBestBroker);
            }
        } catch (Exception e) {
            log.error("Error occurred when selecting message queue", e);
        }

        return tpInfo.selectOneMessageQueue();
    }

    return tpInfo.selectOneMessageQueue(lastBrokerName);
}
```

* 延迟机制接口规范

```java
public interface LatencyFaultTolerance<T> {
    // 更新失败条目
    void updateFaultItem(final T name, final long currentLatency, final long notAvailableDuration);
	// 判断Broker是否可用
    boolean isAvailable(final T name);
	// 移除Fault条目
    void remove(final T name);
	// 尝试从规避的Broker中选择一个可用的Broker
    T pickOneAtLeast();
}
```

* FaultItem: 失败条目

```java
class FaultItem implements Comparable<FaultItem> {
    // 条目唯一键,这里为brokerName
    private final String name;
    // 本次消息发送延迟
    private volatile long currentLatency;
    // 故障规避开始时间
    private volatile long startTimestamp;
}
```

* 消息失败策略

```java
public class MQFaultStrategy {
    // 根据currentLatency本地消息发送延迟,从latencyMax尾部向前找到第一个比currentLatency小的索引,如果没有找到,返回0
    private long[] latencyMax = {50L, 100L, 550L, 1000L, 2000L, 3000L, 15000L};
    // 根据这个索引从notAvailableDuration取出对应的时间,在该时长内,Broker设置为不可用
    private long[] notAvailableDuration = {0L, 0L, 30000L, 60000L, 120000L, 180000L, 600000L};
}
```

* DefaultMQProducerImpl#sendDefaultImpl

```java
sendResult = this.sendKernelImpl(msg ,mq, communicationMode, sendCallback, topicPublishInfo, timeout - costTime);
endTimestamp = System.currentTimeMillis();
this.updateFaultItem(mq.getBrokerName(), endTimestamp - beginTimestampPrev, false);
```

* 如果上述发送过程出现异常,则调用`DefaultMQProducerImpl#updateFaultItem`

```java
public void updateFaultItem(final String brokerName, final long currentLatency, boolean isolation) {
    // 参数:broker名称,本次消息发送延迟时间,是否隔离
    this.mqFaultStrategy.updateFaultItem(brokerName, currentLatency, isolation);
}
```

* MQFaultStrategy#updateFaultItem

```java
public void updateFaultItem(final String brokerName, final long currentLatency, boolean isolation) {
    if (this.sendLatencyFaultEnable) {
        // 计算broker规避的时长
        long duration = computeNotAvailableDuration(isolation ? 30000 : currentLatency);
        // 更新该FaultItem规避时长
        this.latencyFaultTolerance.updateFaultItem(brokerName, currentLatency, duration);
    }
}
```

* MQFaultStrategy#computeNotAvailableDuration

```java
private long computeNotAvailableDuration(final long currentLatency) {
    // 遍历latencyMax
    for (int i = latencyMax.length - 1; i >= 0; i--) {
        // 找到第一个比currentLatency的latencyMax值
        if (currentLatency >= latencyMax[i])
            return this.notAvailableDuration[i];
    }
    // 没有找到则返回0
    return 0;
}
```

* LatencyFaultToleranceImpl#updateFaultItem

```java
public void updateFaultItem(final String name, final long currentLatency, final long notAvailableDuration) {
    // 获得原FaultItem
    FaultItem old = this.faultItemTable.get(name);
    // 为空新建faultItem对象,设置规避时长和开始时间
    if (null == old) {
        final FaultItem faultItem = new FaultItem(name);
        faultItem.setCurrentLatency(currentLatency);
        faultItem.setStartTimestamp(System.currentTimeMillis() + notAvailableDuration);

        old = this.faultItemTable.putIfAbsent(name, faultItem);
        if (old != null) {
            old.setCurrentLatency(currentLatency);
            old.setStartTimestamp(System.currentTimeMillis() + notAvailableDuration);
        }
    } else {
        // 更新规避时长和开始时间
        old.setCurrentLatency(currentLatency);
        old.setStartTimestamp(System.currentTimeMillis() + notAvailableDuration);
    }
}
```



### 发送消息



* DefaultMQProducerImpl#sendKernelImpl: 消息发送API核心入口

```java
// 参数:待发送消息,消息发送队列,消息发送内模式,异步消息回调函数,主题路由信息,超时时间
private SendResult sendKernelImpl( final Message msg, final MessageQueue mq, final CommunicationMode communicationMode, final SendCallback sendCallback, final TopicPublishInfo topicPublishInfo,final long timeout)
```

* DefaultMQProducerImpl#sendKernelImpl

```java
// 获得broker网络地址信息
String brokerAddr = this.mQClientFactory.findBrokerAddressInPublish(mq.getBrokerName());
if (null == brokerAddr) {
    // 没有找到从NameServer更新broker网络地址信息
    tryToFindTopicPublishInfo(mq.getTopic());
    brokerAddr = this.mQClientFactory.findBrokerAddressInPublish(mq.getBrokerName());
}
```

```java
// 为消息分类唯一ID
if (!(msg instanceof MessageBatch)) {
    MessageClientIDSetter.setUniqID(msg);
}

boolean topicWithNamespace = false;
if (null != this.mQClientFactory.getClientConfig().getNamespace()) {
    msg.setInstanceId(this.mQClientFactory.getClientConfig().getNamespace());
    topicWithNamespace = true;
}
// 消息大小超过4K,启用消息压缩
int sysFlag = 0;
boolean msgBodyCompressed = false;
if (this.tryToCompressMessage(msg)) {
    sysFlag |= MessageSysFlag.COMPRESSED_FLAG;
    msgBodyCompressed = true;
}
// 如果是事务消息,设置消息标记MessageSysFlag.TRANSACTION_PREPARED_TYPE
final String tranMsg = msg.getProperty(MessageConst.PROPERTY_TRANSACTION_PREPARED);
if (tranMsg != null && Boolean.parseBoolean(tranMsg)) {
    sysFlag |= MessageSysFlag.TRANSACTION_PREPARED_TYPE;
}
```

```java
// 如果注册了消息发送钩子函数,在执行消息发送前的增强逻辑
if (this.hasSendMessageHook()) {
    context = new SendMessageContext();
    context.setProducer(this);
    context.setProducerGroup(this.defaultMQProducer.getProducerGroup());
    context.setCommunicationMode(communicationMode);
    context.setBornHost(this.defaultMQProducer.getClientIP());
    context.setBrokerAddr(brokerAddr);
    context.setMessage(msg);
    context.setMq(mq);
    context.setNamespace(this.defaultMQProducer.getNamespace());
    String isTrans = msg.getProperty(MessageConst.PROPERTY_TRANSACTION_PREPARED);
    if (isTrans != null && isTrans.equals("true")) {
        context.setMsgType(MessageType.Trans_Msg_Half);
    }

    if (msg.getProperty("__STARTDELIVERTIME") != null || msg.getProperty(MessageConst.PROPERTY_DELAY_TIME_LEVEL) != null) {
        context.setMsgType(MessageType.Delay_Msg);
    }
    this.executeSendMessageHookBefore(context);
}
```

* SendMessageHook

```java
public interface SendMessageHook {
    String hookName();

    void sendMessageBefore(final SendMessageContext context);

    void sendMessageAfter(final SendMessageContext context);
}
```

* DefaultMQProducerImpl#sendKernelImpl

```java
// 构建消息发送请求包
SendMessageRequestHeader requestHeader = new SendMessageRequestHeader();
// 生产者组
requestHeader.setProducerGroup(this.defaultMQProducer.getProducerGroup());
// 主题
requestHeader.setTopic(msg.getTopic());
// 默认创建主题Key
requestHeader.setDefaultTopic(this.defaultMQProducer.getCreateTopicKey());
// 该主题在单个Broker默认队列树
requestHeader.setDefaultTopicQueueNums(this.defaultMQProducer.getDefaultTopicQueueNums());
// 队列ID
requestHeader.setQueueId(mq.getQueueId());
// 消息系统标记
requestHeader.setSysFlag(sysFlag);
// 消息发送时间
requestHeader.setBornTimestamp(System.currentTimeMillis());
// 消息标记
requestHeader.setFlag(msg.getFlag());
// 消息扩展信息
requestHeader.setProperties(MessageDecoder.messageProperties2String(msg.getProperties()));
// 消息重试次数
requestHeader.setReconsumeTimes(0);
requestHeader.setUnitMode(this.isUnitMode());
// 是否是批量消息等
requestHeader.setBatch(msg instanceof MessageBatch);
if (requestHeader.getTopic().startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX)) {
    String reconsumeTimes = MessageAccessor.getReconsumeTime(msg);
    if (reconsumeTimes != null) {
        requestHeader.setReconsumeTimes(Integer.valueOf(reconsumeTimes));
        MessageAccessor.clearProperty(msg, MessageConst.PROPERTY_RECONSUME_TIME);
    }

    String maxReconsumeTimes = MessageAccessor.getMaxReconsumeTimes(msg);
    if (maxReconsumeTimes != null) {
        requestHeader.setMaxReconsumeTimes(Integer.valueOf(maxReconsumeTimes));
        MessageAccessor.clearProperty(msg, MessageConst.PROPERTY_MAX_RECONSUME_TIMES);
    }
}
```

```java
// 异步发送
case ASYNC:
Message tmpMessage = msg;
boolean messageCloned = false;
if (msgBodyCompressed) {
    //If msg body was compressed, msgbody should be reset using prevBody.
    //Clone new message using commpressed message body and recover origin massage.
    //Fix bug:https://github.com/apache/rocketmq-externals/issues/66
    tmpMessage = MessageAccessor.cloneMessage(msg);
    messageCloned = true;
    msg.setBody(prevBody);
}

if (topicWithNamespace) {
    if (!messageCloned) {
        tmpMessage = MessageAccessor.cloneMessage(msg);
        messageCloned = true;
    }
    msg.setTopic(NamespaceUtil.withoutNamespace(msg.getTopic(), 
                                                this.defaultMQProducer.getNamespace()));
}

long costTimeAsync = System.currentTimeMillis() - beginStartTime;
if (timeout < costTimeAsync) {
    throw new RemotingTooMuchRequestException("sendKernelImpl call timeout");
}
sendResult = this.mQClientFactory.getMQClientAPIImpl().sendMessage(
    brokerAddr,
    mq.getBrokerName(),
    tmpMessage,
    requestHeader,
    timeout - costTimeAsync,
    communicationMode,
    sendCallback,
    topicPublishInfo,
    this.mQClientFactory,
    this.defaultMQProducer.getRetryTimesWhenSendAsyncFailed(),
    context,
    this);
break;
// 同步发送
case ONEWAY:
case SYNC:
long costTimeSync = System.currentTimeMillis() - beginStartTime;
if (timeout < costTimeSync) {
    throw new RemotingTooMuchRequestException("sendKernelImpl call timeout");
}
sendResult = this.mQClientFactory.getMQClientAPIImpl().sendMessage(
    brokerAddr,
    mq.getBrokerName(),
    msg,
    requestHeader,
    timeout - costTimeSync,
    communicationMode,
    context,
    this);
break;
default:
assert false;
break;
}
```

```java
// 如果注册了钩子函数,则发送完毕后执行钩子函数
if (this.hasSendMessageHook()) {
    context.setSendResult(sendResult);
    this.executeSendMessageHookAfter(context);
}
```



## 批量消息发送



![](img/019.png)



* 批量消息发送是将同一个主题的多条消息一起打包发送到消息服务端,减少网络调用次数,提高网络传输效率,但是并不是在同一批次中发送的消息数量越多越好,其判断依据是单条消息的长度,如果单条消息内容比较长,则打包多条消息发送会影响其他线程发送消息的响应时间,并且单批次消息总长度不能超过`DefaultMQProducer#maxMessageSize`
* 批量消息发送要解决的问题是如何将这些消息编码以便服务端能够正确解码出每条消息的消息内容
* DefaultMQProducer#send

```java
public SendResult send(Collection<Message> msgs) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
    // 压缩消息集合成一条消息,然后发送出去
    return this.defaultMQProducerImpl.send(batch(msgs));
}
```

* DefaultMQProducer#batch

```java
private MessageBatch batch(Collection<Message> msgs) throws MQClientException {
    MessageBatch msgBatch;
    try {
        // 将集合消息封装到MessageBatch
        msgBatch = MessageBatch.generateFromList(msgs);
        // 遍历消息集合,检查消息合法性,设置消息ID,设置Topic
        for (Message message : msgBatch) {
            Validators.checkMessage(message, this);
            MessageClientIDSetter.setUniqID(message);
            message.setTopic(withNamespace(message.getTopic()));
        }
        // 压缩消息,设置消息body
        msgBatch.setBody(msgBatch.encode());
    } catch (Exception e) {
        throw new MQClientException("Failed to initiate the MessageBatch", e);
    }
    // 设置msgBatch的topic
    msgBatch.setTopic(withNamespace(msgBatch.getTopic()));
    return msgBatch;
}
```



# 消息存储



## 核心类



* DefaultMessageStore

```java
// 消息配置属性
private final MessageStoreConfig messageStoreConfig;
// CommitLog文件存储的实现类
private final CommitLog commitLog;
// 消息队列存储缓存表,按照消息主题分组
private final ConcurrentMap<String/* topic */, ConcurrentMap<Integer/* queueId */, ConsumeQueue>> consumeQueueTable;
// 消息队列文件刷盘线程
private final FlushConsumeQueueService flushConsumeQueueService;
//清除CommitLog文件服务
private final CleanCommitLogService cleanCommitLogService;
//清除ConsumerQueue队列文件服务
private final CleanConsumeQueueService cleanConsumeQueueService;
//索引实现类
private final IndexService indexService;
//MappedFile分配服务
private final AllocateMappedFileService allocateMappedFileService;
//CommitLog消息分发,根据CommitLog文件构建ConsumerQueue、IndexFile文件
private final ReputMessageService reputMessageService;
//存储HA机制
private final HAService haService;
//消息服务调度线程
private final ScheduleMessageService scheduleMessageService;
//消息存储服务
private final StoreStatsService storeStatsService;
//消息堆外内存缓存
private final TransientStorePool transientStorePool;
//Broker状态管理器
private final BrokerStatsManager brokerStatsManager;
//消息拉取长轮询模式消息达到监听器
private final MessageArrivingListener messageArrivingListener;
//Broker配置类
private final BrokerConfig brokerConfig;
//文件刷盘监测点
private StoreCheckpoint storeCheckpoint;
//CommitLog文件转发请求
private final LinkedList<CommitLogDispatcher> dispatcherList;
```



## 消息存储流程



![](img/020.png)



