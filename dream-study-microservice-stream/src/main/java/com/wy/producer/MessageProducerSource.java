package com.wy.producer;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MessageProducerSource {

	/**
	 * 信道channel的名称,需要和配置文件中消息生产者的名称相同
	 */
	String OUTPUT = "output";

	/**
	 * 返回消息信道.{@link OutPut}表示消息生产者,输出消息,配置channel的名称
	 */
	@Output(MessageProducerSource.OUTPUT)
	MessageChannel output();
}