package com.wy.activemq.consumer;

import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.wy.activemq.producer.MailParam;

import lombok.extern.slf4j.Slf4j;

/**
 * 邮件消费者
 * 
 * @author 飞花梦影
 * @date 2022-03-13 11:26:40
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Service
@Slf4j
public class Consumer4 implements SessionAwareMessageListener<Message> {

	@Autowired
	private MailServiceImpl mailServiceImpl;

	@Override
	public synchronized void onMessage(Message message, Session session) {
		try {
			ActiveMQTextMessage msg = (ActiveMQTextMessage) message;
			final String ms = msg.getText();
			log.info("==>receive message:" + ms);
			MailParam mailParam = JSONObject.parseObject(ms, MailParam.class);
			if (mailParam == null) {
				return;
			}

			try {
				mailServiceImpl.mailSend(mailParam);
			} catch (Exception e) {
				// 发送异常,重新放回队列
				// activeMqJmsTemplate.send(sessionAwareQueue, new MessageCreator() {
				// public Message createMessage(Session session) throws JMSException {
				// return session.createTextMessage(ms);
				// }
				// });
				log.error("==>MailException:", e);
			}
		} catch (Exception e) {
			log.error("==>", e);
		}
	}
}