package com.wy.rabbitmq;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 声明一个全局的默认交换器
 * 
 * @author ParadiseWY
 * @date 2020-02-17 16:10:59
 * @git {@link https://github.com/mygodness100}
 */
@Configuration
public class ExchangeConfig {

	/**
	 * durable:rabbitmq重启之后队列不消失,即是否持久化,默认持久化
	 * 
	 * @return 一个交换器
	 */
	@Bean
	public Exchange exchange() {
		return ExchangeBuilder.topicExchange("exchange-default").durable(true).build();
	}
}