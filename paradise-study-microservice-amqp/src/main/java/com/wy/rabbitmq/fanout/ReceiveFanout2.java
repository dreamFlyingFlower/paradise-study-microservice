package com.wy.rabbitmq.fanout;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.wy.rabbitmq.util.ConnectionUtil;

/**
 * Fanout,广播模式消费者2
 *
 * @author 飞花梦影
 * @date 2021-01-04 22:28:53
 * @git {@link https://github.com/mygodness100}
 */
public class ReceiveFanout2 {

	public final static String QUEUE_NAME = "fanout_exchange_queue_2";

	public static void main(String[] argv) throws Exception {
		// 获取到连接
		Connection connection = ConnectionUtil.getConnection();
		// 获取通道
		Channel channel = connection.createChannel();
		// 声明队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		// 绑定队列到交换机
		channel.queueBind(QUEUE_NAME, ProviderFanout.EXCHANGE_NAME, "");
		// 定义队列的消费者
		DefaultConsumer consumer = new DefaultConsumer(channel) {

			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String msg = new String(body);
				System.out.println(" [消费者2] received : " + msg + "!");
			}
		};
		// 监听队列,手动返回完成
		channel.basicConsume(QUEUE_NAME, true, consumer);
	}
}