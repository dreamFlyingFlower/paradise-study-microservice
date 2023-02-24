package com.wy.rabbitmq.exchange;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import lombok.extern.slf4j.Slf4j;

/**
 * 消费者
 *
 * @author 飞花梦影
 * @date 2023-02-24 16:20:07
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
public class ExchangeConsumer {

	public static final String CONFIRM_QUEUE_NAME = "confirm.queue";

	@RabbitListener(queues = CONFIRM_QUEUE_NAME)
	public void receiveMsg(Message message) {
		String msg = new String(message.getBody());
		log.info("接受到队列 confirm.queue 消息:{}", msg);
	}
}