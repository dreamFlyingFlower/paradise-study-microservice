package com.wy.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;

/**
 * 死信队列
 * 
 * @author 飞花梦影
 * @date 2021-01-04 23:27:10
 * @git {@link https://github.com/mygodness100}
 */
public class DeadQueueConfig {

	/**
	 * 声明业务交换机
	 *
	 * @return
	 */
	@Bean
	public TopicExchange topicExchange() {
		return new TopicExchange("spring.test.exchange", true, false);
	}

	/**
	 * 声明业务队列并把死信交换机绑定到业务队列
	 * 
	 * @return
	 */
	@Bean
	public Queue queue() {
		Map<String, Object> arguments = new HashMap<>();
		// x-dead-letter-exchange 这里声明当前队列绑定的死信交换机
		arguments.put("x-dead-letter-exchange", "dead-exchange");
		// x-dead-letter-routing-key 这里声明当前队列的死信路由key
		arguments.put("x-dead-letter-routing-key", "msg.dead");
		return new Queue("spring.test.queue", true, false, false, arguments);
	}

	/**
	 * 业务队列绑定到业务交换机
	 *
	 * @return
	 */
	@Bean
	public Binding binding() {
		return new Binding("spring.test.queue", Binding.DestinationType.QUEUE, "spring.test.exchange", "a.b", null);
	}

	/**
	 * 声明死信交换机
	 * 
	 * @return
	 */
	@Bean
	public TopicExchange deadExchange() {
		return new TopicExchange("dead-exchange", true, false);
	}

	/**
	 * 声明死信队列
	 * 
	 * @return
	 */
	@Bean
	public Queue deadQueue() {
		return new Queue("dead-queue", true, false, false);
	}

	/**
	 * 把死信队列绑定到死信交换机
	 * 
	 * @return
	 */
	@Bean
	public Binding deadBinding() {
		return new Binding("dead-queue", Binding.DestinationType.QUEUE, "dead-exchange", "msg.dead", null);
	}
}