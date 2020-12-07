package com.wy.activemq.consumer;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.stereotype.Component;

/**
 * @description spring整合activemq,消费者
 * @author ParadiseWy
 * @date 2019年5月20日 下午9:41:42
 * @git {@link https://github.com/mygodness100}
 */
@Component
public class Consumer3 implements MessageListener {

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
}