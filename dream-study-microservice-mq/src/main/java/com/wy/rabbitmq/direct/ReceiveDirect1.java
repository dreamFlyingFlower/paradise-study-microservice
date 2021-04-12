package com.wy.rabbitmq.direct;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.wy.rabbitmq.util.ConnectionUtil;

/**
 * 路由键模式消费者1
 *
 * @author 飞花梦影
 * @date 2021-01-04 22:38:06
 * @git {@link https://github.com/mygodness100}
 */
public class ReceiveDirect1 {

	private final static String QUEUE_NAME = "direct_exchange_queue_1";

	public static void main(String[] argv) throws Exception {
		// 获取到连接
		Connection connection = ConnectionUtil.getConnection();
		// 获取通道
		Channel channel = connection.createChannel();
		// 声明队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		// 绑定队列到交换机,同时指定需要订阅的routingkey,假设此处需要update和delete消息
		channel.queueBind(QUEUE_NAME, ProviderDirect.EXCHANGE_NAME, "update");
		channel.queueBind(QUEUE_NAME, ProviderDirect.EXCHANGE_NAME, "delete");
		// 定义队列的消费者
		DefaultConsumer consumer = new DefaultConsumer(channel) {

			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				String msg = new String(body);
				System.out.println(" [消费者1] received : " + msg + "!");
			}
		};
		// 监听队列,自动ACK
		channel.basicConsume(QUEUE_NAME, true, consumer);
	}
}