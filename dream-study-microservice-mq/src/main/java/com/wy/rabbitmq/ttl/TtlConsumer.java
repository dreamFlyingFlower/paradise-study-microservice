package com.wy.rabbitmq.ttl;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import lombok.extern.slf4j.Slf4j;

/**
 * 延迟队列业务队列生产者,监听死信队列
 * 
 * 此处测试,没有正常的消费order.create的MQ监听器,只监听死信队列
 *
 * @author 飞花梦影
 * @date 2021-12-23 14:37:36
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@RabbitListener(queues = "DEAD-CLOSE-QUEUE")
@Slf4j
public class TtlConsumer {

	@RabbitHandler
	public void orderConsumer(String msg) {
		log.info(">死信队列消费订单消息:msg{}<<", msg);
	}
}