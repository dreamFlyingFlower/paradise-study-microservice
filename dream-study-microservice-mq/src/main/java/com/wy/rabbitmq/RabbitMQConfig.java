package com.wy.rabbitmq;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Configuration;

/**
 * 修改rabbitmq默认的序列化类
 * 
 * @author 飞花梦影
 * @date 2019-07-05 00:34:40
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class RabbitMQConfig {

	/**
	 * 可以修改rabbitmq序列化对象的序列化器
	 * 
	 * @return 自定义序列化器
	 */
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}