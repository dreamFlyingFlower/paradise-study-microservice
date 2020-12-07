package com.wy.rabbitmq;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @apiNote 声明一个交换机的bean
 * @author ParadiseWY
 * @date 2020年2月17日 下午4:10:59
 */
@Configuration
public class ExchangeConfig {

	/**
	 * durable:rabbitmq重启之后队列不消失,即持久化
	 * @return 一个交换机
	 */
	@Bean
	public Exchange exchange() {
		return ExchangeBuilder.topicExchange("exchange-test").durable(true).build();
	}
}