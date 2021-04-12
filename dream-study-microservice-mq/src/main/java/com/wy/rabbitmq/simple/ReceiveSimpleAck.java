package com.wy.rabbitmq.simple;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.wy.rabbitmq.util.ConnectionUtil;

/**
 * 基本消息模型消费者,手动进行ACK(消息确认,默认情况是自动确认).
 * 
 * 手动确认ACK模式下必须设置消息失败的重发次数.若不确认ACK,生产者将不停发送消息到队列中,造成内存溢出
 *
 * @author 飞花梦影
 * @date 2021-01-04 21:46:15
 * @git {@link https://github.com/mygodness100}
 */
public class ReceiveSimpleAck {

	private final static String QUEUE_NAME = "simple_queue";

	public static void main(String[] argv) throws Exception {
		// 获取到连接
		Connection connection = ConnectionUtil.getConnection();
		// 创建通道
		final Channel channel = connection.createChannel();
		// 声明队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		// 定义队列的消费者
		DefaultConsumer consumer = new DefaultConsumer(channel) {

			// 获取并处理消息,这个方法类似事件监听,如果有消息的时候,会被自动调用
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				// body 即消息体
				String msg = new String(body);
				System.out.println("received : " + msg + "!");
				// 手动进行ACK
				channel.basicAck(envelope.getDeliveryTag(), false);
			}
		};
		// 监听队列,第二个参数false,手动进行ACK
		channel.basicConsume(QUEUE_NAME, false, consumer);
	}
}