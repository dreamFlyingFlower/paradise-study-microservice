package com.wy.rabbitmq.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wy.rabbitmq.util.ConnectionUtil;

/**
 * 订阅模式生产者
 *
 * @author 飞花梦影
 * @date 2021-01-04 22:44:37
 * @git {@link https://github.com/mygodness100}
 */
public class ProviderTopic {

	public final static String EXCHANGE_NAME = "topic_exchange_test";

	public static void main(String[] argv) throws Exception {
		// 获取到连接
		Connection connection = ConnectionUtil.getConnection();
		// 获取通道
		Channel channel = connection.createChannel();
		// 声明exchange,指定类型为topic
		channel.exchangeDeclare(EXCHANGE_NAME, "topic");
		// 消息内容
		String message = "新增商品 : id = 1001";
		// 发送消息,并且指定routingkey为:insert
		channel.basicPublish(EXCHANGE_NAME, "item.insert", null, message.getBytes());
		System.out.println(" [商品服务：] Sent '" + message + "'");
		channel.close();
		connection.close();
	}
}