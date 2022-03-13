package com.wy.activemq.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.wy.activemq.producer.MailParam;

@Component
public class MailServiceImpl {

	@Autowired
	private JavaMailSenderImpl mailSender;

	@Autowired
	private SimpleMailMessage simpleMailMessage;

	@Autowired
	private ThreadPoolTaskExecutor threadPool;

	/**
	 * 发送模板邮件
	 * 
	 * @param mailParamTemp需要设置四个参数 templateName,toMail,subject,mapModel
	 */
	public void mailSend(final MailParam mailParam) {
		threadPool.execute(new Runnable() {

			public void run() {
				try {
					simpleMailMessage.setFrom(simpleMailMessage.getFrom());
					simpleMailMessage.setTo(mailParam.getTo());
					simpleMailMessage.setSubject(mailParam.getSubject());
					simpleMailMessage.setText(mailParam.getContent());
					mailSender.send(simpleMailMessage);
				} catch (MailException e) {
					throw e;
				}
			}
		});
	}
}