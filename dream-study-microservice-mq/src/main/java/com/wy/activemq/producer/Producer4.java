package com.wy.activemq.producer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

/**
 * 利用ActiveMQ发邮件
 * 
 * @author 飞花梦影
 * @date 2022-03-13 11:22:02
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Service
public class Producer4 {

	@Autowired
	private JmsTemplate activeJmsTemplate;

	/**
	 * 发送消息
	 * 
	 * @param mail
	 */
	public void sendMessage(final MailParam mail) {
		activeJmsTemplate.send(new MessageCreator() {

			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(JSONObject.toJSONString(mail));
			}
		});
	}
}