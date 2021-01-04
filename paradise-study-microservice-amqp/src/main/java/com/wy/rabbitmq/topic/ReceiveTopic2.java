package com.wy.rabbitmq.topic;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.wy.rabbitmq.util.ConnectionUtil;

/**
 * 订阅模式消费者2
 *
 * @author 飞花梦影
 * @date 2021-01-04 22:45:02
 * @git {@link https://github.com/mygodness100}
 */
public class ReceiveTopic2 {

	private final static String QUEUE_NAME = "topic_exchange_queue_2";

	public static void main(String[] argv) throws Exception {
		// 获取到连接
		Connection connection = ConnectionUtil.getConnection();
		// 获取通道
		Channel channel = connection.createChannel();
		// 声明队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		// 绑定队列到交换机,同时指定需要订阅的routingkey,订阅insert,update,delete
		channel.queueBind(QUEUE_NAME, ProviderTopic.EXCHANGE_NAME, "item.*");
		// 定义队列的消费者
		DefaultConsumer consumer = new DefaultConsumer(channel) {

			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String msg = new String(body);
				System.out.println(" [消费者2] received : " + msg + "!");
			}
		};
		channel.basicConsume(QUEUE_NAME, true, consumer);
	}
}