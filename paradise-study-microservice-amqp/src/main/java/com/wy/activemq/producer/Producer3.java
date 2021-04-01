package com.wy.activemq.producer;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

/**
 * @description spring整合activemq
 * @author ParadiseWy
 * @date 2019年5月20日 下午8:59:26
 * @git {@link https://github.com/mygodness100}
 */
@Service
public class Producer3 {

	@Autowired
	private JmsTemplate jmsTemplate;

	public void sendMsg(String msg) {

		jmsTemplate.send(new MessageCreator() {

			/**
			 * 创建一个要发送的序列化消息对象,返回这个消息对象,template自动将消息发送到MOM容器中
			 */
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage("spring发送的消息");
			}
		});

	}

	public void sendMsg(String queue, String msg) {

		jmsTemplate.send(queue, new MessageCreator() {

			/**
			 * 创建一个要发送的序列化消息对象,返回这个消息对象,template自动将消息发送到MOM容器中
			 */
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage("spring发送的消息");
			}
		});

	}

	public void sendMsg() {
		jmsTemplate.send(new MessageCreator() {

			/**
			 * 创建一个要发送的序列化消息对象,返回这个消息对象,template自动将消息发送到MOM容器中
			 */
			@Override
			public Message createMessage(Session session) throws JMSException {
				Message message = session.createObjectMessage(new Serializable() {

					private static final long serialVersionUID = 1L;

				});
				return message;
			}
		});
	}

	public void testQueueReceive() throws JMSException {
		TextMessage message = (TextMessage) jmsTemplate.receive("queue");
		System.out.println(message.getText());
	}
}