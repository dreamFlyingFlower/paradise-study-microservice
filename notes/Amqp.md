# Qmqp

# RabbitMQ



## 概念

* Message:消息,不具名,由消息头,消息体组成,不透明,而消息头则由一系列可选属性组成,包括RoutingKey(路由键),priority(相对于其他消息的优先权),DeliveryMode(是否需要持久性)等
* Publisher:消息生产者,向交换器发布消息的客户端程序
* Exchange:交换器,用来接收生产者发送的消息并将这些消息路由给服务器中的队列.
  * direct:点对点模式,默认,发布订阅的routingkey要完全一样
  * fanout:广播,不处理路由键,只要是发送到交换器的消息都会转发到绑定的队列上
  * topic:主题,匹配模式,根据路由键指定的字符串和队列进行匹配.路由键中的通配符为#,\*.#表示0个或以上的字符,\*匹配一个单词
  * headers:匹配AMQP消息的header而不是路由键,和direct交换器一样,但是性能差,几乎不用
* Queue:消息队列,用来保存消息直到发送给消费者,一个消息可投入一个或多个队列,在队列里等待消费者连接队列取走
* Bingding:绑定,用于消息队列和交换器之间的关联,一个绑定就是基于路由键将交换器和消息队列连接起来的路由规则.Exchange和queue的绑定可以是多对多的
* Connection:连接,比如TCP
* Channel:信道,类似NIO(非阻塞线程)中的多路复用双向流数据流通道,建立在tcp连接上
* Consumer:消费者,从消息队列中取走消息
* VirtualHost:虚拟主机,表示一批交换器,消息队列和相关对象.rabbitmq的默认vhost是/
* routing-key:路由键,rabbitmq决定消息该投递到那个队列的规则
* Broker:消息队列服务器实体



## 运行机制

* Amqp中消息的路由过程和JMS存在一些差别,Amqp中增加了Exchange和Binding
* 生产者把消息发布到Exchange上,消息最终到达队列并被消费者消费,而Binding决定交互器的消息应该发送到那个队列
* 



## 消息模式

### 简单队列

1. 一个生产者,一个队列,一个消费者
2. 生产者将消息发送到队列中,消费者直接从队列中取消息,每条消息只能被消费一次



### Work

1. 工作模式,一个生产者,多个消费者,每条消息只能被消费一次,生产者直接将消息发送给消费者
2. 多个消费者会轮询获取消息,若是不进行轮询,而是某个队列处理完之后立刻处理下一个,则需要手动返回ACK状态,默认ACK状态是自动确认



### Publish/Subscribe

1. 发布/订阅,对应的消息模式为ExchangeTypes.FANOUT
2. 一个生产者,多个消费者,每个队列的消费者实例可以消费一次消息,多个队列会消费多次消息
3. 生产者和消费者的exchange需要完全匹配,不需要路由键
4. 每个消费者都对应一个队列,而一个队列可以对应多个消费者实例
5. 交换器中的消息默认是不保留的,也是发生故障就会消失,最好是开启持久化



### Direct

1. 路由模式,对应的消息模式为ExchangeTypes.DIRECT
2. 在交换机的基础上又增加了一个路由键(routingkey)
3. 生产者和消费者的exchange和routeingkey需要完全匹配
4. 生产者是将消息发送到交换器(exchange),而每个队列(queue)需要将自己绑定到交换器上



### Topic

1. 主题模式,将路由键和特定的规则进行匹配
2. 队列需要绑定到特定的规则上
   1. #:匹配一个或多个词
   2. *:匹配一个词
3. 主要是在Direct模式上增加了更多的自由度,可以模糊匹配路由键



## 持久化

	当消费者挂掉,但是生产者仍在发送消息的时候,若是队列只是一个临时队列,那么消息就会丢失
	临时队列只在消费者存在的时候才会存在,消费者挂掉,临时队列就删除
	实现消息的持久化,需要将相关注解中的autoDelete设置为false,表明该queue或exchange不是一个临时队列和交换器



## ACK

1. 消费者在处理消息的时候,若是服务器发生异常,那该消息可能就没有完成消息消费,数据就会丢失
2. 为了确保消息不丢失,消息被消费者消费之后,需反馈给RabbitMQ是否处理成功,RabbitMQ收到反馈才会从队列中删除消息
3. 消息的ACK机制默认是打开的,但同时会有一个问题,即当消费者挂掉之后,RabbitMQ仍在一直发送消息,这样很可能就会造成内存泄漏,解决的办法是开启RabbitMQ的重试机制,设置重试的次数
4. ACK机制默认是自动提交的,若是需要手动提交,需要修改配置文件进行全局修改.若只修改某个queue或exchange,则需要打开相应的参数durable,设置为true



## Docker中使用

1. docker安装可网上搜索,因为国内安装erlang和rabbitmq太慢,直接在docker中安装更快

2. 需要下载rabbitmq:management,否则没有web管理页面,若不带management为latest版本,没有web页面

3. docker中运行rabbitmq:

   > docker run -d --hostname my-rabbit --name rabbit -e RABBITMQ_DEFAULT_USER=guest -e RABBITMQ_DEFAULT_PASS=guest  -p 15672:15672 -p 5672:5672 -p 25672:25672 -p 61613:61613 -p 1883:1883 -v /app/rabbitmq/etc:/etc/rabbitmq --privileged=true -v  /app/rabbitmq/lib:/var/lib/rabbitmq --privileged=true  -v /app/rabbitmq/log:/var/log/rabbitmq  --privileged=true rabbitmq:management

   1. -d:后台运行
   2. --hostname:指定容器主机名,可自定义
   3. --name:运行的rabbitmq的名称,可自定义,可以同时运行多个相同版本的rabbitmq,每个的名称不一样
   4. -e RABBITMQ_DEFAULT_USER=admin:定义web页面登录的用户名为admin,若不指定,默认为guest
   5. -e RABBITMQ_DEFAULT_PASS=admin:定义web页面登录的密码为admin,若不指定,默认为guest
   6. -p:端口映射,前面的是linux端口,后面的是docker端口
   7. -v:将docker中运行的rabbitmq的文件映射到linux中,前面的参数是linux中地址,后面的是docker中的地址
   8. 注意在映射本地文件时,需要给权限,否则创建文件时会失败

4. docker exec -it rabbit bash:进入正在运行的名为rabbit的容器中,也可以用containerid

5. docker logs rabbit:查看容器的运行情况

6. 在web上运行ip:15672,能成功打开页面表示安装运行成功,5672为通讯端口,spring连接rabbit时需要的端口

7. 若无法打开web页面,则利用docker exec -it rabbit bash进入rabbit的控制台,输入命令:rabbitmq-plugins enable rabbitmq_management即可



## Springboot配置MQ

```yaml
spring: 
  rabbitmq: 
    host: 192.168.1.146
    port: 5672
    # 登录rabbit的用户名和密码,默认guest
    username: guest
    password: guest
    # 连接超时时间,0不超时
    connection-timeout: 0
    listener:
      simple:
        # 全局Ack是否手动确认,manual手动,none不确认,auto自动,默认自动
        # 若在开启手动确认消息,则必须在消费者中手动确认消息,否则生产者会不停的发消息
        # acknowledge-mode: manual
        retry:
          # 是否开启重试机制,默认不开启
          enabled: true
          # 最大重试次数,默认3次
          max-attempts: 5
      direct:
        # acknowledge-mode: manual
        retry:
          enabled: true
          max-attempts: 5
```

在代码中手动确认消息

```java
@Configuration
@RabbitListener(bindings = @QueueBinding(value = @Queue(value = "${config.rabbitmq.queue-email}"),
		exchange = @Exchange(value = "${config.rabbitmq.exchange-email}", type = ExchangeTypes.FANOUT)))
@Slf4j
public class EmailReceiver {
    // import com.rabbitmq.client.Channel;
    // import org.springframework.amqp.core.Message;
    // params为生产者传递的参数,可自定义
	@RabbitHandler
	public void sendMail(Channel channel, Message msg, Map<String, String> params) {
		try {
			// Ack手动确认消息已经被消费,false表示只确认当前的消息,true表示队列中所有的消息都被确认
			channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
			// Ack返回false,拒绝消费消息
            // 第一个boolean表示是否确认当前消息,true表示该队列中所有消息,false表示当前消息
			// 第二个boolean表示丢弃消息或重新回到队列中,true表示重新回到队列,false表示丢弃
			// channel.basicNack(msg.getMessageProperties().getDeliveryTag(), false, true);
			// Ack拒绝消息
			// channel.basicReject(msg.getMessageProperties().getDeliveryTag(), true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
```



# 二.ActiveMQ

	1.Destination:相比于RabbitMQ多了一个Destination,由JMS Provider(消息中间件)负责维护,用于管理Message.而Producer需要指定Destination才能发送消息,Consumer也需要指定Destination才能接收消息.
	2.Producer:消息的生产者,发送message到目的地,应用接口为MessageProducer
	3.Consumer(Receiver):消息消费者,负责从目的地中(处理,监听,订阅)message,应用接口MessageConsumer
	4.Message:消息内容.常见有StreamMessage,BytesMessage,TextMessage,ObjectMessage,MapMessage.若是要实现自己的消息接口,实体类就需要实现Serializable接口
	5.ConnectionFactory:链接工厂,非jdbc的工厂.
	6.Connection:链接,创建访问ActiveMQ连接,由工厂创建
	7.Session:会话,一次持久有效有状态的访问,由链接创建
	8.Queue:队列,是Destination的子接口,处在队列中的消息,只能由一个Consumer消费,消费完之后删除
	9.Topic:主题,Destination的子接口,和RabbitMQ中的Topic差不多,可重复处理信息
	
	ActiveMQ安装
	apache官网下载,解压.主要的配置文件有active的jar包,data是active数据默认存储文件夹,webapps是active网页端监控,配置文件都在conf文件夹中.
	bin:该文件夹中有个active脚本,可用来start,stop,status当前active
	conf:该文件中有active的各种配置文件,其中jetty.xml是启动webapps需要配置的,类似tomcat的settings.xml.还有一个jetty-realm.properties文件是登录网页端配置的用户名和密码等.groups和user.properties文件是jetty的权限配置文件.active.xml是active主要配置文件,集群,端口等在该文件配置.



# 三.springcloud stream

	主要是对rabbit和kafuka的简化使用,不必配置更多的配置来使用中间件