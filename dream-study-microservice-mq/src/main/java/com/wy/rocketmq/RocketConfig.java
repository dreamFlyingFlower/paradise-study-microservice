package com.wy.rocketmq;

import com.wy.rocketmq.consumer.Consumer02;

/**
 * RocketMQ消息队列:支持事务;支持延迟消息;支持集群,负载均衡;支持指定次数和时间间隔的失败消息重发.只支持Java
 *
 * RocketMQ4大构成:生产者(Producer),管理中心(NameServer),消息块(Broker),消费者(Consumer)
 *
 *	NameServer是一个很简单的Topic路由注册中心,支持Broker的动态注册和发现,保存Topic和Broker之间的关系
 * Broker会定时发送自身状态到NameServer,Producer请求NameServer获取Broker地址,
 * Producer将消息发送到Broker中的消息队列,Consumer订阅Broker中的消息队列,通过主动拉取,或由Broker推送获取消息
 * 
 * RocketMQ的3种消息模式:同步,异步,单向(Producer只将消息发送到MQ中,不管结果)
 * 
 * RocketMQ的基础属性:
 * 
 * <pre>
 * Topic:主题,相当于消息的一级分类,具有相同Topic的消息将发送到该Topic下的消息队列中
 * 消息体:消息的内容,可以是字符串,对象等,最大长度是4M
 * 消息Flag:消息的一个标记,RocketMQ不处理,业务中自行处理
 * Tag:相当于消息的二级分类,用于消费消息时进行过滤,可为null,见 {@link Consumer02}
 * Keys:Message索引建,在运维中可以根据这些key快速检索到消息,可为null
 * WaitStoreMsgOK:消息发送时是否等消息存储完成后再返回
 * Message:主要包括消息所属Topic,消息Flag,扩展属性,消息体
 * </pre>
 * 
 * RocketMQ消费重试:
 * 
 * <pre>
 * 当消息成功到达Broker时,若消息没有被消费者接收,比如Broker和消费者网络异常等,此时会不断重试发送消息
 * 
 * 当消息被消费者成功接收,但是消费时异常,消费者无法向Broker返回成功,此时RocketMQ会不断重试.
 * 消息会按延迟消息的延迟等级(1s/5s/10s/30s/1m/2m/3m/4m...10m/20m/30m/1h/2h,总共18等级),
 * 从第3级(10s)开始重试让消费者消费,每失败一次,延迟等级就加1,直到2h.
 * 当最终消息没有被消费时,该消息将被投递到死信队列中,到达死信队列的消息将不再被消费.
 * 
 * 实际中则会指定重试次数,若最终消费失败,将消息写入数据库,由其他程序处理或人工处理
 * </pre>
 * 
 * RocketMQ集群与广播模式:
 * 
 * <pre>
 * 集群:同一个组里的消费者可以监听接收多个队列的消息,但是队列中的单条消息只能由同组中的单个消费者消费
 * 广播:所有监听了队列的消费者都可以消费
 * </pre>
 * 
 * RocketMQ的分布式事务半消息机制:
 * 
 * <pre>
 * 1.A服务是事务开始之前将消息发送至Topic中,但是此时该消息不能被消费者B捕捉
 * 2.A服务完成事务之后将事务状态(成功或失败)发送给Topic
 * 3.Topic收到消息是成功,此时该消息对消费者B可见,B执行本地事务;若失败,则不删除Topic中的消息
 * 4.若A和Topic之间发生异常,半消息长时间存在于Topic中,MQ服务需要通过定时任务扫描Topic中的半消息
 * 5.当定时任务扫描到半消息时,需要主动向A询问该消息的最终状态,此时A服务需要提供可查询事务状态的接口
 * 6.A的本地事务和事务最终状态的修改需要在同一个事务中完成
 * 
 * 具体流程如下:
 * 
 * 1.A向MQ Broker发送消息
 * 2.Broker将消息持久化成功之后向A发送ACK,此时消息为半消息
 * 3.A执行本地事务,并将执行最终结果(commit或rollback)发送给Broker
 * 4.Broker收到结果commit之后将半消息设置为可投递,B可以消费该消息;收到rollback则删除Broker中的半消息
 * 5.若A与Broker之间发送异常,经过固定时间后,Broker根据半消息向A查询事务结果
 * 6.A收到回查消息后将最终结果再次发送给Broker,此时A需要提供事务回查接口
 * 7.Broker收到回查接口信息后再次执行第4步
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2021-04-09 11:26:24
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class RocketConfig {

}