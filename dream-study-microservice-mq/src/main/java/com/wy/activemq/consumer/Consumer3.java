package com.wy.activemq.consumer;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Spring整合ActiveMQ,消费者.ActiveMQ默认只会有一种模式,一个队列.若需要多个模式,需要重新配置
 * 
 * 直接实现{@link MessageListener}只能接收默认的Destination,要接收其他Destication的消息则要使用{@link JmsListener}
 * 
 * @author 飞花梦影
 * @date 2019-05-20 21:41:42
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
public class Consumer3 implements MessageListener {

	@Autowired
	private JmsTemplate jmsTemplate;

	@Override
	public void onMessage(Message message) {
		// 若注册session的时候使用的是客户端确认模式,此处必须手动确认接收到消息
		try {
			if (message instanceof ObjectMessage) {
				ObjectMessage om = (ObjectMessage) message;
				Serializable object = om.getObject();
				// 具体的业务操作
				System.out.println(object);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void receive() throws JMSException {
		// 一次接受一条消息,少用
		TextMessage message = (TextMessage) jmsTemplate.receive("queue");
		System.out.println(message.getText());
	}

	@JmsListener(destination = "topic")
	public void readMsg() {

	}

	@JmsListener(destination = "queue")
	public void readQueueMsg() {

	}
}