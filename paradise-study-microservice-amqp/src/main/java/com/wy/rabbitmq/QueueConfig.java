package com.wy.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description 初始化rabbitmq所需队列,不管是何种模式的mq,都需要先声明队列.若有多个队列,需要实例化多次
 * @author ParadiseWY
 * @date 2019年4月15日 下午1:17:36
 * @git {@link https://github.com/mygodness100}
 */
@Configuration
public class QueueConfig {

	@Bean
	public Queue initQueue() {
		return new Queue("defaultQueue");
	}
}