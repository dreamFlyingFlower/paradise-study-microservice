package com.wy.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Configuration;

/**
 * 修改rabbitmq默认的序列化类
 * 
 * @author ParadiseWY
 * @date 2019年7月5日 下午12:34:40
 */
@Configuration
public class RabbitMQConfig {

	/**
	 * 可以修改rabbitmq序列化对象的序列化器,fastjon无该方法,只有jackson有
	 * 
	 * @return
	 */
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}