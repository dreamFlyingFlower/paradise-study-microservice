package com.wy.rabbitmq.dead;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wy.properties.RabbitProperties;

/**
 * 死信队列
 * 
 * @author 飞花梦影
 * @date 2021-01-04 23:27:10
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class DeadQueueConfig {

	@Autowired
	private RabbitProperties rabbit;

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
	 * 声明业务队列并把业务队列绑定到死信交换机
	 * 
	 * @return
	 */
	@Bean
	public Queue queue() {
		Map<String, Object> arguments = new HashMap<>();
		// 以下参数都可以在RabbitMQ的管理界面看到
		// x-dead-letter-exchange:固定写法.声明当前队列绑定的死信交换机
		arguments.put("x-dead-letter-exchange", rabbit.getDead().getExchange());
		// x-dead-letter-routing-key:固定写法.声明当前队列的死信路由key
		arguments.put("x-dead-letter-routing-key", rabbit.getDead().getRoutingKey());
		// x-message-ttl:固定写法.声明当前队列的过期时间,仅仅用于测试,实际根据需求,通常30分钟或者15分钟
		// 可以不设置,由生产者设置过期时间,更灵活
		arguments.put("x-message-ttl", 120000);
		// x-max-length:固定写法.声明当前队列的最大长度,超过长度的消息也将转到死信队列中
		arguments.put("x-max-length", 1000);
		// 将死信队列参数以及其他参数绑定到正常队列中
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
		return new TopicExchange(rabbit.getDead().getExchange(), true, false);
	}

	/**
	 * 声明死信队列
	 * 
	 * @return
	 */
	@Bean
	public Queue deadQueue() {
		return new Queue(rabbit.getDead().getQueue(), true, false, false);
	}

	/**
	 * 把死信队列绑定到死信交换机
	 * 
	 * @return
	 */
	@Bean
	public Binding deadBinding() {
		return new Binding(rabbit.getDead().getQueue(), Binding.DestinationType.QUEUE, rabbit.getDead().getExchange(),
				rabbit.getDead().getRoutingKey(), null);
	}
}