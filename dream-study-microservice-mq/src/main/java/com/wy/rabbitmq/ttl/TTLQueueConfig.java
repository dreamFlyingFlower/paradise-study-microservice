package com.wy.rabbitmq.ttl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 延迟队列
 * 
 * @author 飞花梦影
 * @date 2021-01-04 23:39:44
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class TTLQueueConfig {

	/**
	 * 业务交换机
	 * 
	 * @return
	 */
	@Bean
	public Exchange exchange() {
		return new TopicExchange("BIZ-EXCHANGE", true, false, null);
	}

	/**
	 * 业务延时队列
	 * 
	 * @return
	 */
	@Bean
	public Queue ttlQueue() {
		Map<String, Object> arguments = new HashMap<>();
		// x-dead-letter-exchange 声明当前队列绑定的死信交换机
		arguments.put("x-dead-letter-exchange", "DEAD-EXCHANGE");
		// x-dead-letter-routing-key 声明当前队列的死信路由key
		arguments.put("x-dead-letter-routing-key", "order.close");
		// 声明延迟队列的过期时间,仅仅用于测试,实际根据需求,通常30分钟或者15分钟
		arguments.put("x-message-ttl", 120000);
		return new Queue("BIZ-TTL-QUEUE", true, false, false, arguments);
	}

	/**
	 * 延时队列绑定到交换机 rountingKey:order.create
	 * 
	 * @return
	 */
	@Bean
	public Binding ttlBinding() {
		return new Binding("BIZ-TTL-QUEUE", Binding.DestinationType.QUEUE, "BIZ-EXCHANGE", "order.create", null);
	}

	/**
	 * 死信交换机
	 * 
	 * @return
	 */
	@Bean
	public Exchange deadExchange() {
		return new TopicExchange("DEAD-EXCHANGE", true, false, null);
	}

	/**
	 * 死信队列绑定到交换机 routingKey:order.close
	 * 
	 * @return
	 */
	@Bean
	public Binding closeBinding() {
		return new Binding("DEAD-CLOSE-QUEUE", Binding.DestinationType.QUEUE, "DEAD-EXCHANGE", "order.close", null);
	}
}