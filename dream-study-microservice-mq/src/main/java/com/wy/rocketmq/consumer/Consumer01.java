package com.wy.rocketmq.consumer;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * RocketMQ消费者,监听消息
 * 
 * @author 飞花梦影
 * @date 2021-04-09 12:00:56
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
@RocketMQMessageListener(topic = "test-rocket-topic", consumerGroup = "test-rocket-group",
		// 消费模式:无序和有序
		consumeMode = ConsumeMode.CONCURRENTLY,
		// 消息模式:广播和集群,默认集群
		messageModel = MessageModel.CLUSTERING,
		// 消息过滤
		selectorExpression = "A||B")
public class Consumer01 implements RocketMQListener<String> {

	/**
	 * 监听队列中的消息,可以是同步,异步,单向
	 * 
	 * @param message 消息
	 */
	@Override
	public void onMessage(String message) {
		System.out.println(message);
	}
}