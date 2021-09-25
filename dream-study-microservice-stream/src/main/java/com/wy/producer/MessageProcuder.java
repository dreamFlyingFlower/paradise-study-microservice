package com.wy.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Date;

/**
 * {@link EnableBinding}:将信道channel和exchange绑定在一起,根据文档需要指定一个包含{@link Input}和(或){@link Output}的接口
 * 
 * @author 飞花梦影
 * @date 2021-09-25 11:23:27
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableBinding(MessageProducerSource.class)
public class MessageProcuder {

	/** 消息的发送信道 */
	@Autowired
	private MessageProducerSource messageProducerSource;

	public void send(String msg) {
		// 通过信道发消息,会阻塞线程,直到消息返回,可能会抛异常
		boolean flag = messageProducerSource.output().send(MessageBuilder.withPayload(msg).build());
		// 通过洗脑发消息,会阻塞运行,直到消息返回.设置消息返回的超时时间,单位ms
		// messageProducerSource.output().send(MessageBuilder.withPayload(msg).build(), 1000l);
		System.out.println("消息发送:<" + msg + "> 完成,时间:" + new Date() + ", flag=" + flag);
	}
}