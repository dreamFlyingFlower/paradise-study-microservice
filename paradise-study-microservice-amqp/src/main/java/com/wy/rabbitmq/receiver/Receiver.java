package com.wy.rabbitmq.receiver;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息接收者,直接写在方法上调用
 * 
 * @author ParadiseWY
 * @date 2019-04-15 13:23:47
 * @git {@link https://github.com/mygodness100}
 */
@Component
public class Receiver {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	/**
	 * rabbit从点对点的交换器exchange-direct中接收消息,消息被消费后会从队列中去除
	 */
	public void receiveMsg() {
		Object object = rabbitTemplate.receiveAndConvert("routingKey-direct");
		System.out.println(object);
	}

	/**
	 * 使用默认的消息队列接收消息,默认队列必须提前新建好,并绑定在路由键routingKey-direct上,queues队列的名称
	 */
	@RabbitListener(queues = "default-queue")
	public void receiveMsg(String msg) {
		System.out.println(msg);
	}

	/**
	 * 使用绑定在路由键上的其他队列接收消息,队列必须先提前新建好,并绑定在路由键上<br>
	 * 假设绑定在routingKey-direct队列名为queue-direct.该队列也可以同时绑定在exchange-fanout上,同样可以接收消息
	 * 
	 * @param msg 生产者发送的消息
	 */
	@RabbitListener(queues = "queue-direct")
	public void receiveMsg(Message msg) {
		System.out.println(msg.getBody());
		System.out.println(msg.getMessageProperties());
	}
}