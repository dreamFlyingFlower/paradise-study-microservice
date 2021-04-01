package com.wy.activemq.producer;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

/**
 * Spring整合ActiveMQ消息发送者
 *
 * @author 飞花梦影
 * @date 2019-05-20 20:59:26
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Service
public class Producer3 {

	/**
	 * 不同模式的ActiveMQ应该用不同的jmsTemplate,默认的destination也应该不一样
	 */
	@Autowired
	private JmsTemplate jmsTemplate;

	public void sendMsg(String msg) {
		// 修改JmsTemplate消息发布模式,默认是点对点模式,true表示发布/订阅模式
		jmsTemplate.setPubSubDomain(false);
		// 发送到默认的点对点队列
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
		// 发送到指定队列名
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
		// 自定义消息
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
}