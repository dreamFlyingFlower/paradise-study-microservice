package com.wy.rabbitmq.simple;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.wy.rabbitmq.util.ConnectionUtil;

/**
 * 基本消息模型消费者,消息一旦被消费,队列中就没有消息,该模式下一条消息只能被消费一次
 *
 * @author 飞花梦影
 * @date 2021-01-04 21:36:54
 * @git {@link https://github.com/mygodness100}
 */
public class ReceiveSimple1 {

	private final static String QUEUE_NAME = "simple_queue";

	public static void main(String[] argv) throws Exception {
		// 获取到连接
		Connection connection = ConnectionUtil.getConnection();
		// 创建通道
		Channel channel = connection.createChannel();
		// 声明队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		// 定义队列的消费者
		DefaultConsumer consumer = new DefaultConsumer(channel) {

			// 获取消息,并且处理,这个方法类似事件监听,如果有消息的时候,会被自动调用
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				// body 即消息体
				String msg = new String(body);
				System.out.println("received : " + msg + "!");
			}
		};
		// 监听队列,第二个参数:是否自动进行消息确认
		channel.basicConsume(QUEUE_NAME, true, consumer);
	}
}