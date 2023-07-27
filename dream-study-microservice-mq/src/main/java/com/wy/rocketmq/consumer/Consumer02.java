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
		// 订阅消息,接收的是所有消息
		consumer.subscribe("topic", "*");
		// 利用||表示或,同时监听topic下的多个TAG
		consumer.subscribe("topic", "tag1 || tag2");
		// 设置消费模式,只有集群模式和广播模式,默认是负载均衡模式(集群模式).广播模式每个消费者消费的消息都是相同的
		consumer.setMessageModel(MessageModel.CLUSTERING);
		// 过滤消息,类似SQL的WHERE子句,必须配合生产者消息的 UserProperty 使用
		// consumer.subscribe("topic", MessageSelector.bySql("id > 0 AND age > 20"));
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