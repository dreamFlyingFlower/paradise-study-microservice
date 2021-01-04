package com.wy.rabbitmq.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wy.rabbitmq.util.ConnectionUtil;

/**
 * 基本消息模型生产者
 *
 * @author 飞花梦影
 * @date 2021-01-04 21:35:04
 * @git {@link https://github.com/mygodness100}
 */
public class ProviderSimple {

	private final static String QUEUE_NAME = "simple_queue";

	private final static String QUEUE_NAME2 = "simple_queue2";

	public static void main(String[] argv) throws Exception {
		// 获取到连接
		Connection connection = ConnectionUtil.getConnection();
		// 从连接中创建通道,使用通道才能完成消息相关的操作
		Channel channel = connection.createChannel();
		// 开启生产者的消息确认机制,默认不开启,高并发下不建议开启
		channel.confirmSelect();
		// 消息确认回调
		channel.addConfirmListener((long deliveryTag, boolean multiple) -> {
			System.out.println("消息发送成功");
		}, (long deliveryTag, boolean multiple) -> {
			System.out.println("消息发送失败");
		});
		// 声明队列,这是一个幂等的操作,只有当它不存在时才会被自动创建
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		// 消息内容
		String message = "Hello World!";
		// 向指定的队列中发送消息
		channel.basicPublish("", QUEUE_NAME2, null, message.getBytes());
		System.out.println("Sent '" + message + "'");
		// 关闭通道和连接
		channel.close();
		connection.close();
	}
}