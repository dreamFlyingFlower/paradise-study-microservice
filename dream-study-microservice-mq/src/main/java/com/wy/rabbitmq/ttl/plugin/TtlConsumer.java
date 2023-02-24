package com.wy.rabbitmq.ttl.plugin;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import lombok.extern.slf4j.Slf4j;

/**
 * 延迟业务队列,监听死信队列,不监听业务队列
 * 
 * @author 飞花梦影
 * @date 2021-12-23 14:37:36
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@RabbitListener(queues = "delayed.queue")
@Slf4j
public class TtlConsumer {

	@RabbitHandler
	public void orderConsumer(String msg) {
		log.info(">死信队列消费订单消息:msg{}<<", msg);
	}
}