package com.wy.rabbitmq.receiver;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description 消息接收者,直接写在方法上调用
 * @author ParadiseWY
 * @date 2019年4月15日 下午1:23:47
 * @git {@link https://github.com/mygodness100}
 */
@Component
public class Receiver {
	
	@Autowired
	private RabbitTemplate rabbitTemplate;

	/**
	 * 消息监听机制监听队列,queues队列的名称
	 */
	@RabbitListener(queues = "defaultQueue")
	public void receiveMsg(String msg) {
		System.out.println(msg);
	}
	
	@RabbitListener(queues = "defaultQueue")
	public void receiveMsg(Message msg) {
		System.out.println(msg.getBody());
		System.out.println(msg.getMessageProperties());
	}
	
	/**
	 * rabbit接收消息,消息被消费后会从队列中去除
	 */
	public void receiveMsg() {
		Object object = rabbitTemplate.receiveAndConvert("routingKey");
		System.out.println(object);
	}
}