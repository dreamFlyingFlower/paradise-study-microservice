package com.wy.rabbitmq.ttl.plugin;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 延迟队列插件业务队列生产者
 *
 * @author 飞花梦影
 * @date 2021-12-23 14:37:36
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Component
public class TtlPluginProducer {

	@Autowired
	private AmqpTemplate rabbitAmqpTemplate;

	public void sendMsg(String msg) {
		// 向消息队列发送消息;交换器名称,路由键,消息
		rabbitAmqpTemplate.convertAndSend(TtlPluginQueueConfig.DELAYED_EXCHANGE_NAME,
				TtlPluginQueueConfig.DELAYED_ROUTING_KEY, msg, correlationData -> {
					correlationData.getMessageProperties().setDelay(10000);
					return correlationData;
				});
	}
}