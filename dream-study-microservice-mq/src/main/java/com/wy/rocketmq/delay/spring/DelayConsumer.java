package com.wy.rocketmq.delay.spring;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2023-07-27 11:12:19
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Component
@RocketMQMessageListener(topic = "topic_delay", consumerGroup = "group_delay")
@Slf4j
public class DelayConsumer implements RocketMQListener<String> {

	@Override
	public void onMessage(String message) {
		log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
	}
}