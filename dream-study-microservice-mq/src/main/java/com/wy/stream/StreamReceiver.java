package com.wy.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

/**
 * 监听消息发送队列,接受消息.监听的消息名要和接口中input和output的消息队列名相同
 * 
 * 接收消息的时候,每个分布式项目都会产生一个rabbit的队列名称,但是是不同的,即会多次接受消息,需要在服务端定义绑定的队列名
 * 
 * @author 飞花梦影
 * @date 2019-09-15 07:36:28
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Component
@EnableBinding(StreamClient.class)
public class StreamReceiver {

	/**
	 * 若是直接从一个输入到输出,则不需要写sendto,直接监听即可
	 * 
	 * @param msgObject 监听的消息
	 */
	@StreamListener("msgClient")
	/**
	 * 若是需要将监听的消息发送到另外一个队列,可使用sendto,将消息发送到另外的队列
	 * 
	 * @param msgObject
	 */
	@SendTo("otherClient")
	public void process(Object msgObject) {
		System.out.println(msgObject);
	}

	/**
	 * 另外的队列接受直接接受rabbitmq的消息或接收从sentto发送的消息
	 * 
	 * @param msgObject
	 */
	@StreamListener("otherClient")
	public void process2(Object msgObject) {
		System.out.println(msgObject);
	}
}