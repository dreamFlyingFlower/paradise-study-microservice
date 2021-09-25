package com.wy.consumer;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MessageConsumerSource {

	/**
	 * 信道channel的名称,需要和配置文件中消息消费者的名称相同
	 */
	String INPUT = "input";

	/**
	 * 返回消息信道.{@link Input}表示消息消费者,消费信息,配置channel的名称
	 */
	@Input(MessageConsumerSource.INPUT)
	SubscribableChannel input();
}