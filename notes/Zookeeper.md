# Zookeeper

# 概述

* 一个高可用分布式管理和协调框架,基于ZAB算法(原子消息广播协议)的实现,可以很好的保证分布式中数据的一致性
* 全局数据一致,每个节点保存一份相同的数据副本,客户端无论连接到那给节点,数据都是相同的
* zk包括文件系统+监听系统;其目录结构类似linux文件树,每一个节点(znode)都可以存数据,其默认的根节点是/,但是每个节点存储的数据比较小,通常不超过1M
* ZAB原子性原则:当要在zk中写入数据时,若是请求先到的follower,则follower将会把请求发送到leader,leader会对每一个事件都生成一个全局的,顺序的zkid,形成一个队列,再将该请求发送到各个follower中,follower对请求的操作将返回一个结果,该结果必须返回给leader,之后leader将对结果进行统计.当结果数超过所有子节点的一半时,进行commit操作,同时通知其他子节点进行commit操作
* zk的监听通知机制,当任何节点数据变动时,都会通知在zk注册的客户端
* zk在提供读写服务的时候,只会是leader节点提供写服务,其他follower全部读.当leader挂掉的时候,会从follower节点中选取一个节点成为leader节点
* 只要半数以上的节点存活,集群就能正常运行,所以通常zk集群节点为奇数
* zk在选举节点时,数据不可用,此时可能会造成线程阻塞.
* zk的数据都会从follower同步到leader上,数据量越大,同步的时间越长,应该尽量控制数据量在一定范围内



# 应用场景

* 集群管理:对集群中的机器进行管理,并监控.选出leader,实现集群的容错管理
* 配置管理:数据量最好小点,这样同步会快些.若数据是动态改变的,可以用zk
* 发布订阅:可以进行分布式数据的发布与订阅
* 服务器动态上下线:只能监听手动上下线的服务器,若是异常退出的,无法监听
* 数据库动态切换
* 分布式日志收集
* zk配合curator或zkclient实现高可用



# 特性



* 顺序一致性:从一个客户端发起的事务请求,会严格的按照发起的顺序被应用到zk中
* 原子性:所有事务处理结果在集群中都是一致的,要么集群里的机器都应用了某一事务,要么都没有应用
* 单一视图:集群中所有的zk节点上的数据都是一致的
* 可靠性:当应用了一个事务,并对客户端进行了响应,那么该事务引起的服务端状态会被一致保留下来
* 实时性:通常意义的实时性是指一旦事务被应用成功,那么客户端立刻就能获取变更后的数据.但是zk只能保证在一段时间内,客户端最终一定能拿到服务器的最新数据



# 核心



## 工作流程

* zk集群启动后,Client连接到其中的一个节点,这个节点可以leader,也可以follower
* 连通后,节点分配一个id给Client,发送Ack信息给Client
* 如果客户端没有收到Ack,连接到另一个节点
* Client周期性发送心跳信息给节点保证连接不会丢失
* 如果Client读取数据,发送请求给节点,节点读取自己的数据,返回节点数据给Client
* 如果Client存储数据,将路径和数据发送给Server,Server转发给leader,leader再补发请求给所有的follower,只有半数以上节点成功响应,数据才算写入成功



## ACL(auth)

> ACL(Access Control List):为保证zk的正常运行,zk提供的一套权限控制机制.有3种模式:权限模式,授权对象,权限

* 权限模式(Scheme),包括4中权限模式:
  * ip:通过对ip的细粒度控制
  * digest:最常用的权限控制模式,类似于登录时输入用户名和密码进行验证,格式为username:Base64(SHA-1(username:password))
  * world:最开放的权限模式,仅仅是一个标识
  * super:超级用户模式,可对zk进行任意操作
* 权限对象:如用户和角色一样,不同的权限赋值给不同的用户
* 权限:通过权限检测后可以被允许执行的操作,在zk中分为5种
  * create:创建子节点的权限
  * read:获取节点数据和子节点列表的权限
  * write:更新节点数据的权限
  * delete:删除子节点的权限
  * admin:设置节点acl的权限
* 还有zk预设的其他权限模式
  * Ids.CREATOR_ALL_ACL:系统将利用addAuthinfo注册的信息来作为新创建的节点的授权信息
  * Ids.OPEN_ACL_UNSAFE:任何人可以做任何操作
  * Ids.READ_ACL_UNSAFE:任何人可以做读取操作



## 选举

* 选举的因素有4个
  * serverid:就是myid的大小,这是不变的,值越大权重越大
  * zxid:服务器中存放的最大数据id,值越大说明数据越新,在选举中权重越大
  * epoch:逻辑时钟,即投票次数.每一轮开始投票时都是相同的,每投一次票就增加1,然后与接收到的其他server返回的投票信息中的数值相比,值越大权重越大
  * server状态:
    * LOOKING:选举状态
    * FOLLOWING:随从状态,同步leader状态,参与投票
    * OBSERVING:观察状态,同步leader状态,不参与投票
    * LEADING:领导者状态
* 选举发送到其他server的消息包括:
  * serverid,即myid
  * zxid,数据id
  * epoch
  * 选举状态
* server1启动,第一轮投票都会投给自己,由于其他服务还没启动,得不到反馈,一直处于LOOKING状态
* server2启动,给自己投票,与server1进行比较,由于server2的编号大于server1,server2胜出.若此时启动的服务数大于集群服务的一半,server2就是leader
* server3启动,给自己投票,同时与server1,server2交换信息,尽管server3打,但leader已经选出,server3仍然是follower
* 若leader挂了,此时不单单要比较myid的编号大小,还需要比较结点的zxid大小,2者比较的结果都胜出的才是新的leader
* 投票数大于半数即为leader



# ZNode

## 1 特性

* node节点的唯一标识就是他的路径,每个节点可以存储数据,可以有子节点,除了ephemeral不可有子节点
* znode是有版本的,每个节点可以有多个数据版本,也就是会有多份数据
* zk的客户端和服务器之间使用长连接方式,通过心跳保持连接,这个连接状态被称为session
* znode不能递归创建节点,即不能跳过父节点,直接创建子节点.不可重复创建相同节点
* znode可以被监控,只要其中的数据变化,都可以通知到监听这个节点的客户端



## 2 节点类型

* persistent:持久化目录节点,客户端与zookeeper断开后,节点仍存在
* persistent_sequential:持久化顺序编号目录节点,在持久化基础上给该节点名称顺序编号
* ephemeral:临时节点,与zookeeper断开后,删除该节点.不可有子节点
* ephemeral_sequential:临时顺序编号目录节点,在临时节点上给该节点名称顺序编号



## 3 事件类型

* EventType.NodeCreated:节点创建事件
* EventType.NodeDataChanged:节点数据变化事件
* EventType.NodeChildrenChanged:节点的子节点变化事件
* EventTYPE.NodeDeleted:节点删除事件



# 安装

* 下载压缩包,解压,修改解压后的文件名为zookeeper

* 新增zk的环境变量

  ```shell
  vi /etc/profile
  export ZOOKEEPER_HOME=/usr/local/zookeepr(zk文件夹地址)
  export PATH=$ZOOKEEPER_HOME/bin:$PATH
  # 保存退出编辑
  source /etc/profile
  ```

* 在zookeeper中新建data目录,进入zookeeper/conf,复制zoo_sample.cfg,并改名为zoo.cfg.修改zoo.cfg中的dataDir为刚才新建的data目录

* 若是搭建集群,则进入data,创建一个myid的文件,在文件里写入一个1,不同的zk要写不同的值,不能重复.集群中myid越大优先级越高,投票时得到的票数越多

* 搭建集群,若有3台机器集群,需要修改每个zk的zoo.cfg,在文件最末尾加上相同的以下配置.

  server后的数字要跟zk中的myid里的数字一致,ip可以换成主机名

  第一个端口是leader和follower之间通讯的端口,后面一个端口是节点之间用来选举的

  若是多台机器集群,则除了ip不一样,后面的2个端口都可以是一样的

  若是伪集群(单台机器),则后面的2个端口都要不一样

  ```shell
  server.1=192.168.1.1:2888:3888
  server.2=192.168.1.2:2888:3888
  server.3=192.168.1.3:2888:3888
  ```

* 启动和客户端连接zk

  ```shell
  # 进入zookeeper/bin
  zkServer.sh start/stop/restart/status # zk启动,停止,重启,状态
  zkCli.sh # 进入zk客户端
  ```

  

# 配置

* tickTime:作为zk服务器之间或客户端与服务器之间保持心跳的时间间隔,单位毫秒
* dataDir:zk保存数据的目录,默认情况下,zk将写数据的日志文件也保存在这个目录里
* dataLogDir:zk保存日志文件的目录
* clientPort:客户端连接zk服务器的端口,与集群中配置的2个端口要不一样
* initLimit:集群启动时leader和follower之间心跳检查的最大次数,若超过则集群启动失败
* syncLimit:leader和follower之间发送消息,检查心跳的最大次数
* server.myid=ip:port1:port2:集群模式下才有这个参数,且是多个.myid是数据.ip是各个集群节点的ip,port1是节点交换信息的端口,port2是用来选举leader的端口
* globalOutstandingLimit:zk服务器最大请求线程数
* preAllocSize:zk事务日志文件大小,默认64M
* snapCount:相邻2次数据快照之间的事务操作的次数
* maxClientCnxns:单台客户端和zk之间的最大连接数
* minSessionTimeout:回话最小超时时间
* maxSessionTimeout:回话最大超时时间
* fsync.warningthresholdms:若zk事务同步日志超过该值,将写入报警日志
* autopurge.snapRetainCount:历史事务日志和快照自动清理功能可以保留的快照数量,最小为3
* autopurge.purgeInterval:自动清理日志和快照的频率,默认不开启
* syncEnabled:是否开启保存事务日志和快照功能,默认开启



# ZKShell

* help:显示所有的操作命令

* ls [] path [watch]:查看节点下的子节点,根节点为/,默认都会有一个zookeeper节点

  * -s:查看详细信息,显示stat信息
    * cZxid:事务的id
    * ctime:节点被创建的毫秒数
    * mZxid:最后更新的zxid
    * mtime:最后更新的时间
    * pZxid:节点最后更新的子节点zxid
    * cversion:子节点版本,子节点修改次数
    * dataVersion:数据版本
    * aclVersion:acl版本
    * ephemeralOwner:若是临时节点,这是znode拥有者的sessionid.若不是,则为0
    * dataLength:数据长度
    * numChildren:子节点数量
  * -w:查看被监听的信息
  * -R:百度,我也不知道
  * watch:是否监听该节点以及子节点变化,只能监听一次

* create [] path data:创建一个znode,若data不存在,则不同版本上结果不一样,3.6版本为null

  * -e:创建临时节点
  * -s:创建一个带顺序编号的节点
  * path:不能递归创建znode,会报错.即父目录不存在时,子目录也不能创建

  ```shell
  create /test test1 # 成功
  create /test/test test2 # 失败
  ```

* get path [watch]:获得节点的信息.

  watch表示是否监听该节点,且只能监听当前节点,不能监听该节点的子节点和父节点

  若监听,当该节点的值发生变化时,触发监听机制,当前节点获得节点变化的消息

  每次监听只能监听一次,若还要继续监听,则需要再次watch

* set path data:修改节点的值,节点若不存在,抛出异常

* delete path:删除节点,当节点下有子节点时,报错

* deleteall:递归删除节点,包括子节点,不同版本的命令不一样

* stat path:查看节点的状态,不包括数据

* history:查看已经执行过的命令

* redo num:重新执行已经执行过的命令,num是从history从获得的编号