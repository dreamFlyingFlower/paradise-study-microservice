package com.wy.consumer;

import java.util.Date;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;

/**
 * 消息接收者.将信道channel和exchange绑定在一起,根据文档需要指定一个包含{@link Input}和(或){@link Output}的接口
 * 
 * {@link StreamListener}:监听消息队列,消费消息,需要指定消费者的信道的名称
 * 
 * 消息分组:在消费者中配置group参数,详见配置文件
 * 默认情况下是没有分组(group)的,此时若消费者或MQ宕机,消息就会丢失.配置了group之后,消息就会持久化,但是该参数对生产者无效
 * 分组只是有利于消息的持久化,但是其他没有分组的消费者,仍然能接收到生产者发送的消息,只是不会持久化
 * 没有分组时,一个消息可被多个消费者(任何group)消费,分组可以让多个group相同微服务消费者只有一个服务消费,生产者无需配置group
 * 没有分组时,若有多个微服务,每一个绑定了指定channel的消费者,都会收到消息,此时就相当于广播,消费者都可以消费相同的消息
 * 
 * 默认情况下SpringCloudStream传送消息属于广播消息,匹配方式是#,表示所有消费者都可以匹配上,
 * 可以通过指定路由键RoutingKey实现按需求匹配消息消费端进行消息接收处理
 * 
 * @author 飞花梦影
 * @date 2021-09-25 13:04:24
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableBinding(MessageConsumerSource.class)
public class MessageConsumer {

	@StreamListener(MessageConsumerSource.INPUT)
	public void input(Message<String> message) {
		System.out.println("消息接收:<" + message.getPayload() + "> 完成,时间:" + new Date());
	}
}