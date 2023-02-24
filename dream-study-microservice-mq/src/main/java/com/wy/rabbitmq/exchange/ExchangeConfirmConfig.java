package com.wy.rabbitmq.exchange;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

/**
 * 交换器ACK配置类,需要开启配置spring.rabbitmq.publisher-confirm-type=correlated,但是对性能影响很大,不建议开启
 * 
 * @author 飞花梦影
 * @date 2023-02-24 16:14:41
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class ExchangeConfirmConfig {

	public static final String CONFIRM_EXCHANGE_NAME = "confirm.exchange";

	public static final String CONFIRM_QUEUE_NAME = "confirm.queue";

	/**
	 * 声明业务 Exchange
	 * 
	 * @return
	 */
	@Bean("confirmExchange")
	public DirectExchange confirmExchange() {
		return new DirectExchange(CONFIRM_EXCHANGE_NAME);
	}

	/**
	 * 声明确认队列
	 * 
	 * @return
	 */
	@Bean("confirmQueue")
	public Queue confirmQueue() {
		return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
	}

	/**
	 * 声明确认队列绑定关系
	 * 
	 * @param queue
	 * @param exchange
	 * @return
	 */
	@Bean
	public Binding queueBinding(@Qualifier("confirmQueue") Queue queue,
			@Qualifier("confirmExchange") DirectExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("key1");
	}
}