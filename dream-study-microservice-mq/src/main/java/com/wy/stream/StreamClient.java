package com.wy.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @apiNote springstream的客户端,发送消息,可以整合rabbitmq或kafka,都是为了简化中间件的消息 发送和接口.
 *          注意注解的包,且方法返回的对象是固定的,input和output的队列名必须是相同的,否则无法传递消息
 * @author ParadiseWY
 * @date 2019年9月15日 上午7:31:53
 */
public interface StreamClient {

	@Input("msgClient")
	SubscribableChannel inputChannel();
	
	@Output("msgClient")
	MessageChannel outPutChannel();
	
	@Input("otherClient")
	SubscribableChannel inputChannel1();
	
	@Output("otherClient")
	MessageChannel outPutChannel1();
}