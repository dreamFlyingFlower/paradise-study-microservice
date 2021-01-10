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
 * fanout:广播模式,不需要routingkey,只需要交换器相同即可,每个队列中的消费者实例都会消费一次消息,多个队列多次消费
 * 
 * 若开启了全局手动确认ACK,即在配置文件中配置,则必须每条消息都手动确认ACK返回true, 否则生产者会不停发送消息
 * 
 * @author ParadiseWY
 * @date 2019-04-16 17:07:26
 * @git {@link https://github.com/mygodness100}
 */
@RabbitListener(bindings = @QueueBinding(value = @Queue(value = "${mq.fanout.queue}", autoDelete = "true"),
		exchange = @Exchange(value = "${mq.fanout.exchange}", type = ExchangeTypes.FANOUT)))
@Component
public class ReceiverFanout {

	@RabbitHandler
	public void receiveMsg(Channel channel, Message message, String msg) {
		try {
			// Ack手动确认消息已经被消费,false表示只确认当前的消息,true表示队列中所有的消息都被确认
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			/**
			 * Ack返回false,第一个boolean表示是否确认当前消息,true表示该队列中所有消息,false表示当前消息
			 * 第二个boolean表示丢弃消息或重新回到队列中,true表示重新回到队列,false表示丢弃
			 */
			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
			// Ack拒绝消息
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println(msg);
	}
}