package com.wy.rabbitmq.exchange;

import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * 交换机进行ACK的回调函数,要实现RabbitTemplate.ConfirmCallback接口
 * 开启交换机的消息返回机制,而不是丢弃消息,需要实现RabbitTemplate.ReturnsCallback
 *
 * @author 飞花梦影
 * @date 2023-02-24 16:17:25
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
public class ExchangeCallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

	/**
	 * 交换机不管是否收到消息的一个回调方法 CorrelationData 消息相关数据 ack 交换机是否收到消息
	 */
	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		String id = correlationData != null ? correlationData.getId() : "";
		if (ack) {
			log.info("交换机已经收到 id 为:{}的消息", id);
		} else {
			log.info("交换机还未收到 id 为:{}消息,由于原因:{}", id, cause);
		}
	}

	@Override
	public void returnedMessage(ReturnedMessage returnedMessage) {
		log.info("消息:{}被服务器退回，退回原因:{}, 交换机是:{}, 路由 key:{}", new String(returnedMessage.getMessage().getBody()),
				returnedMessage.getReplyText(), returnedMessage.getExchange(), returnedMessage.getRoutingKey());
	}
}