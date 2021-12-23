package com.wy.rabbitmq.dead;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import lombok.extern.slf4j.Slf4j;

/**
 * 死信队列消费者
 *
 * @author 飞花梦影
 * @date 2021-12-23 14:39:34
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@RabbitListener(queues = "${config.rabbit.dead.queue}")
@Slf4j
public class DeadConsumer {

	@RabbitHandler
	public void orderConsumer(String msg) {
		log.info(">死信队列消费订单消息:msg{}<<", msg);
	}
}