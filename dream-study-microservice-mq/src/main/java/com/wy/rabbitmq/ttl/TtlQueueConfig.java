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
 * 延迟队列,利用TTL+死信队列可组成延迟队列,消费者则监听死信队列即可达到延迟效果
 * 
 * 所有需要过期的消息发送到DELAY_QUEUE中,DELAY_QUEUE不能设置消费者监听,不设置队列过期时间,但是生产者必须设置消息过期时间,否则消息不过期就不会进入死信队列
 * 
 * DELAY_QUEUE中的消息过期被送到DEAD_QUEUE中,消费者监听死信队列进行消费
 * 
 * 第一种方式有一个问题:如果生产者发送的消息设置了过期时间,而队列没有设置过期时间,队列中又挤压了大量消息,那后面过期的消息可能不会在过期时就立刻被丢到死信队列
 * 因为消息只有在被消费的时候才会知道是否过期,即便不产生挤压,队列前面的消息过期时间长,后面的消息过期短,后面的消息一样不会丢到死信队列
 * 
 * 还有另外一种方式:消费者直接将消息送到死信队列中,并由生产者设置消息过期时间,消费者监听死信队列,消息过期后由消费者消费
 * 
 * @author 飞花梦影
 * @date 2021-01-04 23:39:44
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class TtlQueueConfig {

	public static final String DEAD_QUEUE = "dead_queue";

	public static final String DELAY_QUEUE = "delay_queue";

	public static final String DEAD_EXCHANGE = "dead_exchange";

	public static final String DELAY_EXCHANGE = "delay_exchange";

	public static final String DEAD_ROUTING_KEY = "dead.dealy";

	public static final String PRODUCER_ROUTING_KEY = "dead.producer";

	/**
	 * 业务交换机
	 * 
	 * @return
	 */
	@Bean
	Exchange ttlExchange() {
		return new TopicExchange(DELAY_EXCHANGE, true, false, null);
	}

	/**
	 * 业务延时队列
	 * 
	 * @return
	 */
	@Bean
	Queue ttlQueue() {
		Map<String, Object> arguments = new HashMap<>();
		// 以下参数都可以在RabbitMQ的管理界面看到
		// x-dead-letter-exchange 声明当前队列绑定的死信交换机
		arguments.put("x-dead-letter-exchange", DEAD_EXCHANGE);
		// x-dead-letter-routing-key 声明当前队列的死信路由key
		arguments.put("x-dead-letter-routing-key", DEAD_ROUTING_KEY);
		// 声明延迟队列的过期时间,仅仅用于测试,实际根据需求,通常30分钟或者15分钟
		// 设置了业务队列的过期时间才会将消息发送到死信队列,会造成不同的延迟时间需要多个队列,可以不设置该参数,由生产者设置
		// arguments.put("x-message-ttl", 120000);
		// 将死信队列参数绑定到正常队列中
		return new Queue(DELAY_QUEUE, true, false, false, arguments);
	}

	/**
	 * 业务延时队列绑定到业务延时交换机 rountingKey:order.create
	 * 
	 * @return
	 */
	@Bean
	Binding ttlBinding() {
		return new Binding(DELAY_QUEUE, Binding.DestinationType.QUEUE, DELAY_EXCHANGE, PRODUCER_ROUTING_KEY, null);
	}

	/**
	 * 死信交换机
	 * 
	 * @return
	 */
	@Bean
	Exchange deadExchange() {
		return new TopicExchange(DEAD_EXCHANGE, true, false, null);
	}

	/**
	 * 死信队列
	 * 
	 * @return
	 */
	@Bean
	Queue deadQueue() {
		return new Queue(DEAD_QUEUE, true, false, false);
	}

	/**
	 * 死信队列绑定到交换机 routingKey:order.close
	 * 
	 * @return
	 */
	@Bean
	Binding closeBinding() {
		return new Binding(DEAD_QUEUE, Binding.DestinationType.QUEUE, DEAD_EXCHANGE, DEAD_ROUTING_KEY, null);
	}
}