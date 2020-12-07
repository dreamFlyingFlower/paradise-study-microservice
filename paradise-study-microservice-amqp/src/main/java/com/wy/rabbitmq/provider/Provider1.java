package com.wy.rabbitmq.provider;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description 路由模式,交换器以及路由键必须全匹配
 * @author ParadiseWY
 * @date 2019年4月15日 下午1:19:49
 * @git {@link https://github.com/mygodness100}
 */
@Component
public class Provider1 {

	// spring对rabbitmq,activeqm等消息队列的封装类
	@Autowired
	private AmqpTemplate rabbitAmqpTemplate;
	// 交换器名称
	@Value("${mq.direct.exchange}")
	private String exchange;
	// 路由键名称
	@Value("${mq.direct.routing.key}")
	private String routingKey;

	public void sendMsg(String msg) {
		// 向消息队列发送消息;交换器名称,队列名称,消息
		rabbitAmqpTemplate.convertAndSend(exchange, routingKey, msg);
	}
}