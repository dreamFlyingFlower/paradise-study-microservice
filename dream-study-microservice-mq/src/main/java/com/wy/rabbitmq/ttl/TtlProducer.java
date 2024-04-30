package com.wy.rabbitmq.ttl;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 延迟队列业务队列生产者
 *
 * @author 飞花梦影
 * @date 2021-12-23 14:37:36
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Component
public class TtlProducer {

	@Autowired
	private AmqpTemplate rabbitAmqpTemplate;

	public void sendMsg(String msg) {
		// 向消息队列发送消息;交换器名称,路由键,消息
		rabbitAmqpTemplate.convertAndSend(TtlQueueConfig.DELAY_EXCHANGE, TtlQueueConfig.PRODUCER_ROUTING_KEY, msg,
				message -> {
					// 设置消息过期时间,单位毫秒,这样设置,如果业务队列绑定死信队列时没有设置过期时间,则10S过期,消息被发送到死信队列
					// 如果业务队列绑定死信队列设置了过期时间,则谁长用谁的???
					// 如果使用不设置死信队列过期时间机制,则发送多条小心时,业务队列只会以第一条数据的过期时间为过期时间,
					// 导致后面的消息即时延迟小,仍然会以第一条数据的延迟时间为准
					// 如消息A延迟20S,消息B延迟5S,先发送A,后发送B,则只有等到A过期之后才会处理B,即B也要延迟20S
					// 上述情况可以安装rabbitmq的插件rabbitma_delayed_message_exchange来解决
					message.getMessageProperties().setExpiration("10000");
					return message;
				});
	}
}