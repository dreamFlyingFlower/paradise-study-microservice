package com.wy.rabbitmq.ttl;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 延迟队列业务队列生产者
 *
 * @author 飞花梦影
 * @date 2021-12-23 14:37:36
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Component
public class TtlProducer {

	@Autowired
	private AmqpTemplate rabbitAmqpTemplate;

	public void sendMsg(String msg) {
		// 向消息队列发送消息;交换器名称,路由键,消息
		rabbitAmqpTemplate.convertAndSend("BIZ-EXCHANGE", "order.create", msg, message -> {
			// 设置消息过期时间,单位毫秒
			message.getMessageProperties().setExpiration("1000");
			return message;
		});
	}
}