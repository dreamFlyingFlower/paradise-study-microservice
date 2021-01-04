package com.wy.rabbitmq.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wy.rabbitmq.util.ConnectionUtil;

/**
 * 路由键模式生产者
 *
 * @author 飞花梦影
 * @date 2021-01-04 22:36:47
 * @git {@link https://github.com/mygodness100}
 */
public class ProviderDirect {

	public final static String EXCHANGE_NAME = "direct_exchange_test";

	public static void main(String[] argv) throws Exception {
		// 获取到连接
		Connection connection = ConnectionUtil.getConnection();
		// 获取通道
		Channel channel = connection.createChannel();
		// 声明exchange,指定类型为direct
		channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
		// 消息内容
		String message = "商品删除了,id = 1001";
		// 发送消息,并且指定routingkey为:insert
		channel.basicPublish(EXCHANGE_NAME, "delete", null, message.getBytes());
		System.out.println("Send '" + message + "'");
		channel.close();
		connection.close();
	}
}