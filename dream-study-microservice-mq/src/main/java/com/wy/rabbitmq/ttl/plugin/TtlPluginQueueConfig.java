package com.wy.rabbitmq.ttl.plugin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 利用RabbitMQ延迟插件rabbitmq_delayed_message_exchange实现通用延迟队列
 * 
 * @author 飞花梦影
 * @date 2021-01-04 23:39:44
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class TtlPluginQueueConfig {

	public static final String DELAYED_QUEUE_NAME = "delayed.queue";

	public static final String DELAYED_EXCHANGE_NAME = "delayed.exchange";

	public static final String DELAYED_ROUTING_KEY = "delayed.routingkey";

	/**
	 * 声明队列
	 * 
	 * @return
	 */
	@Bean
	public Queue delayedQueue() {
		return new Queue(DELAYED_QUEUE_NAME);
	}

	/**
	 * 自定义一个延迟交换机,x-delayed-type,x-delayed-message为固定写法,由RabbitMQ插件生成
	 * 
	 * @return
	 */
	@Bean
	public CustomExchange delayedExchange() {
		Map<String, Object> args = new HashMap<>();
		// 自定义交换机的类型
		args.put("x-delayed-type", "direct");
		return new CustomExchange(DELAYED_EXCHANGE_NAME, "x-delayed-message", true, false, args);
	}

	@Bean
	public Binding bindingDelayedQueue(@Qualifier("delayedQueue") Queue queue,
			@Qualifier("delayedExchange") CustomExchange delayedExchange) {
		return BindingBuilder.bind(queue).to(delayedExchange).with(DELAYED_ROUTING_KEY).noargs();
	}
}