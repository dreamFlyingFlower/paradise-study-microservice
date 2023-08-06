package com.wy.rocketmq.consumer;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * RocketMQ原生接收消息
 *
 * @author 飞花梦影
 * @date 2022-05-29 00:01:53
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class Consumer02 {

	public static void main(String[] args) throws Exception {
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group");
		// 设置nameserver地址
		consumer.setNamesrvAddr("192.168.1.150:9876");
		// 客户端本机IP地址.某些机器会发生无法识别客户端IP地址情况,需要应用在代码中强制指定
		consumer.setClientIP("127.0.0.1");
		// 客户端实例名称.客户端创建的多个 Producer、Consumer实际是共用一个内部实例,这个实例包含网络连接、线程资源等
		consumer.setInstanceName("instanceName");
		// 通信层异步回调线程数
		consumer.setClientCallbackExecutorThreads(4);
		// 轮询Name Server间隔时间,单位毫秒
		consumer.setPollNameServerInterval(30000);
		// 向Broker发送心跳间隔时间,单位毫秒
		consumer.setHeartbeatBrokerInterval(30000);
		// 持久化Consumer消费进度间隔时间,单位毫秒
		consumer.setPersistConsumerOffsetInterval(5000);

		// 消费者组
		consumer.setConsumerGroup("consumer-group");
		// 订阅消息,接收的是所有消息
		consumer.subscribe("topic", "*");
		// 利用||表示或,同时监听topic下的多个TAG
		consumer.subscribe("topic", "tag1 || tag2");
		// 设置消费模式,只有集群模式和广播模式,默认是负载均衡模式(集群模式).广播模式每个消费者消费的消息都是相同的
		consumer.setMessageModel(MessageModel.CLUSTERING);
		// Consumer启动后,默认从上次消费的位置开始消费,这包含两种情况:
		// 上次消费的位置未过期,则消费从上次中止的位置进行;上次消费位置已经过期,则从当前队列第一条消息开始消费
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
		// 只有当consumeFromWhere值为CONSUME_FROM_TIMESTAMP时才起作用.默认半小时前
		// consumer.setConsumeTimeout(0);
		// Rebalance算法实现策略
		consumer.setAllocateMessageQueueStrategy(new AllocateMessageQueueAveragely());
		// 消费线程池最小线程数
		// consumer.setConsumeThreadMin(0);
		// 消费线程池最大线程数
		// consumer.setConsumeThreadMax(0);
		// 单队列并行消费允许的最大跨度
		// consumer.setConsumeConcurrentlyMaxSpan(0);
		// 拉消息本地队列缓存消息最大数
		// consumer.setPullThresholdForQueue(0);
		// 拉消息间隔,由于是长轮询,所以为0,但是如果应用为了流控,也可以设置大于0的值,单位毫秒
		consumer.setPullInterval(0);
		// 批量拉消息,一次最多拉多少条
		consumer.setPullBatchSize(0);
		// 批量消费,一次消费多少条消息
		consumer.setConsumeMessageBatchMaxSize(0);
		// 过滤消息,类似SQL的WHERE子句,必须配合生产者消息的 UserProperty 使用
		// consumer.subscribe("topic", MessageSelector.bySql("id > 0 AND age > 20"));

		// DefaultLitePullConsumer defaultMQPullConsumer = new
		// DefaultLitePullConsumer();
		// 长轮询,Consumer拉消息请求在Broker挂起超过指定时间,客户端认为超时,单位毫秒
		// defaultMQPullConsumer.setConsumerTimeoutMillisWhenSuspend(0);
		// 非长轮询,拉消息超时时间,单位毫秒
		// defaultMQPullConsumer.setConsumerPullTimeoutMillis(0);
		// 消费进度存储
		// defaultMQPullConsumer.setOffsetStore(null);
		// Rebalance算法实现策略
		// defaultMQPullConsumer.setAllocateMessageQueueStrategy(null);

		// 接收消息
		consumer.registerMessageListener(new MessageListenerConcurrently() {

			@Override
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
				for (MessageExt msg : msgs) {
					System.out.println(new String(msg.getBody(), StandardCharsets.UTF_8));
				}
				// 获得消息的重试次数,如果重试3次仍然失败,直接返回成功
				if (msgs.get(0).getReconsumeTimes() > 3) {
					// return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
				// 消息消费失败,进行重试
				// return ConsumeConcurrentlyStatus.RECONSUME_LATER;
				// 返回null,抛异常也会触发消息重试
				// 消息消费成功
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});
		// 顺序接收消息
		consumer.registerMessageListener(new MessageListenerOrderly() {

			@Override
			public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
				for (MessageExt msg : msgs) {
					System.out.println(new String(msg.getBody(), StandardCharsets.UTF_8));
				}
				return ConsumeOrderlyStatus.SUCCESS;
			}
		});
		consumer.start();
	}
}