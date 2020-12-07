package com.wy.activemq.consumer;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * @description subscribe:Topic模式消息接收者.
 * @author ParadiseWy
 * @date 2019年5月20日 下午9:41:42
 * @git {@link https://github.com/mygodness100}
 */
public class Consumer2 {

	public void handlerMsg() {
		ConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin",
				"tcp://192.168.1.146:61616");
		try (Connection conn = factory.createConnection();) {
			conn.start();
			Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue("first-topic");
			MessageConsumer consumer = session.createConsumer(queue);
			// receice方法是一个主动获取消息的方法,执行一次,拉取一个消息,开发少用,常用的就是注册监听器
			// Message message = consumer.receive();
			// String text = ((TextMessage)message).getText();
			// System.out.println(text);
			// 重复启动该类,就会产生多个消费者
			// 注册监听器,注册成功后,队列中消息的变化会自动触发onMessage方法,接收并处理消息
			// 监听器一旦注册,永久有效,除非consumer关闭.监听器可注册多个,此时mq自动循环调用多个监听器,
			// 处理队列中的消息,实现的是并行处理.
			consumer.setMessageListener(new MessageListener() {
				@Override
				public void onMessage(Message message) {
					// 若注册session的时候使用的是客户端确认模式,此处必须手动确认接收到消息
					try {
						message.acknowledge();
						System.out.println(((TextMessage) message).getText());
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			});
			// 保证监听器执行完成之后关闭
			System.in.read();
		} catch (JMSException | IOException e) {
			e.printStackTrace();
		}
	}
}