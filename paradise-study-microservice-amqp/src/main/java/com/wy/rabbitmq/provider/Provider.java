package com.wy.rabbitmq.provider;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description 向消息队列发送消息,简单模式
 * @author ParadiseWY
 * @date 2019年4月15日 下午1:19:49
 * @git {@link https://github.com/mygodness100}
 */
@Component
public class Provider {

	// spring对rabbitmq,activeqm等消息队列的封装类
	@Autowired
	private AmqpTemplate amqpTemplate;
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	public void sendMsg(String msg) {
		// 向消息队列发送消息;队列名称(不可重复),消息
		amqpTemplate.convertAndSend("defaultQueue", msg);
		// 和amqpTemplate一样的方法,路由模式
		rabbitTemplate.convertAndSend("exchange","routingKey",msg);
	}
}