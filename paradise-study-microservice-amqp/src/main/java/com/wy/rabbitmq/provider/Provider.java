package com.wy.rabbitmq.provider;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 向消息队列发送消息,简单模式
 * 
 * @author ParadiseWY
 * @date 2019年4月15日 下午1:19:49
 * @git {@link https://github.com/mygodness100}
 */
@Component
public class Provider {

	/** spring对rabbitmq,activemq等消息队列的封装类 */
	@Autowired
	private AmqpTemplate amqpTemplate;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private AmqpAdmin amqpAdmin;

	public void sendMsg(String msg) {
		// 向默认的交换器发送消息:路由键,消息
		amqpTemplate.convertAndSend("exchange-default", msg);
		// 和amqpTemplate一样的方法,点对点模式.注意交换器必须先在rabbitmq中新建好,否则失败
		rabbitTemplate.convertAndSend("exchange-direct", "routingKey-direct", msg);
		// 广播模式发送消息,不需要路由键.路由键为"",但不能不写,否则调用默认的交换器,交换器需要先新建好
		rabbitTemplate.convertAndSend("exchange-fanout", "", msg);
	}

	public void sendMsg() {
		// 在rabbitmq中没有交换器的时候由程序创建
		amqpAdmin.declareExchange(new DirectExchange("exchange-declare"));
		// amqpAdmin.declareExchange(new FanoutExchange("exchange-declare"));
		// amqpAdmin.declareExchange(new TopicExchange("exchange-declare"));
		// amqpAdmin.declareExchange(new HeadersExchange("exchange-declare"));
		// 创建队列,名称随机
		// amqpAdmin.declareQueue();
		// 创建一个指定名称的队列
		amqpAdmin.declareQueue(new Queue("queue-declare"));
		// 创建一个绑定规则:绑定队列的名称;绑定类型,是queue,还是exchange;exchange名称;绑定的路由键,可自定义;其他参数
		amqpAdmin.declareBinding(
				new Binding("queue-declare", DestinationType.QUEUE, "exchange-declare", "routingKey-declare", null));
	}
}