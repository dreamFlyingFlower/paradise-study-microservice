package com.wy.rabbitmq.receiver;

import java.io.IOException;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

/**
 * RabbitMQ消费者
 * 
 * <pre>
 * {@link RabbitListener}:表明该类是一个消费者
 * {@link RabbitListener#bindings}:指定队列绑定信息,包括队列信息,路由信息等
 * {@link QueueBinding}:队列绑定信息,包括队列信息,路由信息等
 * {@link QueueBinding#value()}:队列信息
 * {@link QueueBinding#exchange()}:交换器信息
 * {@link QueueBinding#key()}:路由键规则
 * {@link Queue}:队列信息
 * {@link Queue#value()}:队列的名称
 * {@link Queue#autoDelete()}:是否是一个可删除的临时队列,true->是,false->默认不是
 * {@link Exchange}:路由信息
 * {@link Exchange#value()}:交换器信息
 * {@link Exchange#type()}:交换器类型
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2019-04-16 13:40:22
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RabbitListener(bindings = @QueueBinding(value = @Queue(value = "${mq.top.queue.error}", autoDelete = "true"),
		exchange = @Exchange(value = "${mq.top.exchange}", type = ExchangeTypes.TOPIC), key = "*.error.log"))
@Component
public class ReceiverTopic2 {

	@RabbitHandler
	public void receiveMsg(String msg, Message message, Channel channel) {
		System.out.println(msg);
		try {
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}