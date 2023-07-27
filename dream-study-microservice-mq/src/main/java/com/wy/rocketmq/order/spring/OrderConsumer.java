package com.wy.rocketmq.order.spring;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
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
@RocketMQMessageListener(topic = "topic_order", consumerGroup = "group_order", consumeMode = ConsumeMode.ORDERLY // 设置为顺序消费
)
@Slf4j
public class OrderConsumer implements RocketMQListener<Integer> {

	@Override
	public void onMessage(Integer message) {
		log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
	}

}