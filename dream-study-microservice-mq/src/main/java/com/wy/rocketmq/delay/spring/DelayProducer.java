package com.wy.rocketmq.delay.spring;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * RocketMQ延迟队列,延迟时间不支持自定义,只有固定时长:1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m
 * 20m 30m 1h 2h
 *
 * @author 飞花梦影
 * @date 2022-06-26 18:09:27
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Component
public class DelayProducer {

	@Autowired
	private RocketMQTemplate rocketMQTemplate;

	public void send() throws Exception {
		Message<String> message = MessageBuilder.withPayload("延迟消息").build();
		// 同步发送消息
		rocketMQTemplate.syncSend("topic_delay", message, 30 * 1000,
				// 此处设置的就是延时级别
				3);
	}
}