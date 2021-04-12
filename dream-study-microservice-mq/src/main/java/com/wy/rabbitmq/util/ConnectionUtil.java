package com.wy.rabbitmq.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 原生方式与RabbitMQ建立连接
 *
 * @author 飞花梦影
 * @date 2021-01-04 21:34:00
 * @git {@link https://github.com/mygodness100}
 */
public class ConnectionUtil {

	/**
	 * 建立与RabbitMQ的连接
	 * 
	 * @return 连接
	 * @throws Exception
	 */
	public static Connection getConnection() throws Exception {
		// 定义连接工厂
		ConnectionFactory factory = new ConnectionFactory();
		// 设置服务地址
		factory.setHost("192.168.1.150");
		// 端口
		factory.setPort(5672);
		// 设置账号信息,用户名,密码,vhost
		factory.setVirtualHost("/");
		factory.setUsername("guest");
		factory.setPassword("guest");
		// 通过工程获取连接
		Connection connection = factory.newConnection();
		return connection;
	}
}