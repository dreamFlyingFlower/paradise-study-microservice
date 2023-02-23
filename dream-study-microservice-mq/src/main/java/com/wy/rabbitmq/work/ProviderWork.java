package com.wy.rabbitmq.work;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.wy.rabbitmq.util.ConnectionUtil;

/**
 * 工作模式生产者,多个消费者需要监听同一个队列
 *
 * @author 飞花梦影
 * @date 2021-01-04 21:58:36
 * @git {@link https://github.com/mygodness100}
 */
public class ProviderWork {

	private final static String QUEUE_NAME = "test_work_queue";

	public static void main(String[] argv) throws Exception {
		// 获取到连接
		Connection connection = ConnectionUtil.getConnection();
		// 获取通道
		Channel channel = connection.createChannel();
		// 声明队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		// 循环发布任务
		for (int i = 0; i < 50; i++) {
			// 消息内容
			String message = "task .. " + i;
			// MessageProperties.PERSISTENT_TEXT_PLAIN:生产者消息持久化
			channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
			System.out.println("Sent '" + message + "'");
			Thread.sleep(i * 2);
		}
		// 关闭通道和连接
		channel.close();
		connection.close();
	}
}