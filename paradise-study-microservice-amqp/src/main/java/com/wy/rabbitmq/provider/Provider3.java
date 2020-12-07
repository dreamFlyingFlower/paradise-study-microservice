package com.wy.rabbitmq.provider;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description 订阅/发布模式(广播模式),不需要路由键
 * @author ParadiseWY
 * @date 2019年4月15日 下午1:19:49
 * @git {@link https://github.com/mygodness100}
 */
@Component
public class Provider3 {

	// spring对rabbitmq,activeqm等消息队列的封装类
	@Autowired
	private AmqpTemplate rabbitAmqpTemplate;
	// 交换器名称
	@Value("${mq.fanout.exchange}")
	private String exchange;

	public void sendMsg(String msg) {
		// 向消息队列发送消息;交换器名称,路由键,消息
		rabbitAmqpTemplate.convertAndSend(exchange, "", msg);
	}
}