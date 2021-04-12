package com.wy.rocketmq;

/**
 * RocketMQ消息队列:支持事务;支持延迟消息;支持集群,负载均衡;支持指定次数和时间间隔的失败消息重发.只支持Java
 *
 * RocketMQ4大构成:生产者(Producer),管理中心(NameServer),消息块(Broker),消费者(Consumer)
 *
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
 * Tag:相当于消息的二级分类,用于消费消息时进行过滤,可为null
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
 * @author 飞花梦影
 * @date 2021-04-09 11:26:24
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class RocketConfig {

}