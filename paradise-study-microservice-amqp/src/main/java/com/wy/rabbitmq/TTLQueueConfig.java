package com.wy.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author 飞花梦影
 * @date 2021-01-04 23:39:44
 * @git {@link https://github.com/mygodness100}
 */
@Configuration
public class TTLQueueConfig {

	/**
	 * 交换机
	 * 
	 * @return
	 */
	@Bean
	public Exchange exchange() {
		return new TopicExchange("DEAR-EXCHANGE", true, false, null);
	}

	/**
	 * 延时队列
	 * 
	 * @return
	 */
	@Bean("DEAR-TTL-QUEUE")
	public Queue ttlQueue() {
		Map<String, Object> arguments = new HashMap<>();
		arguments.put("x-dead-letter-exchange", "DEAR-EXCHANGE");
		arguments.put("x-dead-letter-routing-key", "order.close");
		arguments.put("x-message-ttl", 120000); // 仅仅用于测试,实际根据需求,通常30分钟或者15分钟
		return new Queue("DEAR-TTL-QUEUE", true, false, false, arguments);
	}

	/**
	 * 延时队列绑定到交换机 rountingKey:order.create
	 * 
	 * @return
	 */
	@Bean("DEAR-TTL-BINDING")
	public Binding ttlBinding() {
		return new Binding("DEAR-TTL-QUEUE", Binding.DestinationType.QUEUE, "DEAR-EXCHANGE", "order.create", null);
	}

	/**
	 * 死信队列
	 * 
	 * @return
	 */
	@Bean("DEAR-CLOSE-QUEUE")
	public Queue queue() {
		return new Queue("DEAR-CLOSE-QUEUE", true, false, false, null);
	}

	/**
	 * 死信队列绑定到交换机 routingKey:order.close
	 * 
	 * @return
	 */
	@Bean("DEAR-CLOSE-BINDING")
	public Binding closeBinding() {
		return new Binding("DEAR-CLOSE-QUEUE", Binding.DestinationType.QUEUE, "DEAR-EXCHANGE", "order.close", null);
	}
}