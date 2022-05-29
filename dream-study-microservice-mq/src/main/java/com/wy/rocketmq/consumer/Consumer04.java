package com.wy.rocketmq.consumer;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * RocketMQ原生事务消费者
 *
 * @author 飞花梦影
 * @date 2022-05-29 11:55:52
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class Consumer04 {

	public static void main(String[] args) throws Exception {
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group");
		consumer.setNamesrvAddr("192.168.1.50:9876");

		// 订阅topic,接收此Topic下的所有消息
		consumer.subscribe("topic", "*");
		consumer.registerMessageListener(new MessageListenerConcurrently() {

			@Override
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
				for (MessageExt msg : msgs) {
					System.out.println(new String(msg.getBody(), StandardCharsets.UTF_8));
				}
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});
		consumer.start();
	}
}