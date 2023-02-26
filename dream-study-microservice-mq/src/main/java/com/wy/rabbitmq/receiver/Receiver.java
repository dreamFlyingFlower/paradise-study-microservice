package com.wy.rabbitmq.receiver;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 消息接收者,直接写在方法上调用
 * 
 * 被 RabbliListener 修饰的方法可以有以下参数
 * 
 * <pre>
 * Annotated methods are allowed to have flexible signatures similar to what {@link MessageMapping} provides, that is
 * {@link com.rabbitmq.client.Channel} to get access to the Channel:channel对象
 * {@link org.springframework.amqp.core.Message} or one if subclass to get access to the raw AMQP message:message对象,可以直接操作原生的AMQP消息
 * {@link org.springframework.messaging.Message} to use the messaging abstraction counterpart:抽象message
 * {@link org.springframework.messaging.handler.annotation.Payload @Payload}
 * 		-annotated method arguments including the support of validation:注解方法参数,该参数的值就是消息体.可以直接作用在参数上
 * {@link org.springframework.messaging.handler.annotation.Header @Header}
 * 		-annotated method arguments to extract a specific header value,including standard AMQP headers defined by
 * 		{@link org.springframework.amqp.support.AmqpHeaders AmqpHeaders}:注解方法参数,访问指定的消息头字段的值
 * {@link org.springframework.messaging.handler.annotation.Headers @Headers}
 * 		-annotated argument that must also be assignable to {@link java.util.Map} for getting access to all headers:
 * 		该注解的方法参数获取该消息的消息头的所有字段,参数类型对应Map
 * {@link org.springframework.messaging.MessageHeaders MessageHeaders} arguments for getting access to all headers:参数类型,访问所有消息头字段
 * {@link org.springframework.messaging.support.MessageHeaderAccessor MessageHeaderAccessor} or
 * {@link org.springframework.amqp.support.AmqpMessageHeaderAccessor AmqpMessageHeaderAccessor} for convenient access to all method arguments:
 * 		访问所有消息头字段
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2019-04-15 13:23:47
 * @git {@link https://github.com/dreamFlyingFlower}
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
	public void receiveMsg(@Payload String msg) {
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