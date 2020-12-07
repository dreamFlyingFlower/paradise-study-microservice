package com.wy.rabbitmq.provider;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 主题模式(topic),交换器完全匹配,路由键可由通配符匹配
 * 
 * @author ParadiseWY
 * @date 2019年4月15日 下午1:19:49
 * @git {@link https://github.com/mygodness100}
 */
@Component
public class Provider2_1 {

	@Autowired
	private AmqpTemplate rabbitAmqpTemplate;

	@Value("${mq.top.exchange}")
	private String exchange;

	public void sendMsg(String msg) {
		// 向消息队列发送消息;交换器名称,路由键,消息
		rabbitAmqpTemplate.convertAndSend(exchange, "provider2_1.info.log", msg);
		rabbitAmqpTemplate.convertAndSend(exchange, "provider2_1.error.log", msg);
		rabbitAmqpTemplate.convertAndSend(exchange, "provider2_1.warn.log", msg);
		rabbitAmqpTemplate.convertAndSend(exchange, "provider2_1.debug.log", msg);
	}
}