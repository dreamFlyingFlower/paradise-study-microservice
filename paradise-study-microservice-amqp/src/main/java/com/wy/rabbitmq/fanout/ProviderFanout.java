package com.wy.rabbitmq.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wy.rabbitmq.util.ConnectionUtil;

/**
 * Fanout,广播模式生产者
 *
 * @author 飞花梦影
 * @date 2021-01-04 22:28:14
 * @git {@link https://github.com/mygodness100}
 */
public class ProviderFanout {

	public final static String EXCHANGE_NAME = "fanout_exchange_test";

	public static void main(String[] argv) throws Exception {
		// 获取到连接
		Connection connection = ConnectionUtil.getConnection();
		// 获取通道
		Channel channel = connection.createChannel();
		// 声明exchange,指定类型为fanout
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		// 消息内容
		String message = "Hello everyone";
		// 发布消息到Exchange
		channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
		System.out.println(" [生产者] Sent '" + message + "'");
		channel.close();
		connection.close();
	}
}