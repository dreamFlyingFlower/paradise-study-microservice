package com.wy.kafka;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 消费者消息监听异常处理器
 *
 * @author 飞花梦影
 * @date 2021-04-24 18:12:59
 */
@Component
@Slf4j
public class CustomizeConsumerErrorHandler {

	/**
	 * 新建一个异常处理器
	 * 
	 * @return 消息异常处理对象
	 */
	@Bean("consumerAwareErrorHandler")
	public ConsumerAwareListenerErrorHandler consumerAwareErrorHandler() {
		return (message, exception, consumer) -> {
			System.out.println("消费异常：" + message.getPayload());
			return null;
		};
	}

	/**
	 * 将异常处理器的BeanName放到@KafkaListener注解的errorHandler属性里面
	 * 
	 * @param record 消息监听记录
	 * @throws Exception
	 */
	@KafkaListener(topics = { "testTopic1" }, errorHandler = "consumerAwareErrorHandler")
	public void onMessage4(ConsumerRecord<?, ?> record) throws Exception {
		log.error("Kafka监听消息异常");
		throw new Exception("简单消费-模拟异常");
	}

	/**
	 * 批量消费也一样,异常处理器的message.getPayload()也可以拿到各条消息的信息
	 * 
	 * @param records 消息监听记录
	 * @throws Exception
	 */
	@KafkaListener(topics = "testTopic1", errorHandler = "consumerAwareErrorHandler")
	public void onMessage5(List<ConsumerRecord<?, ?>> records) throws Exception {
		System.out.println("批量消费一次...");
		log.error("Kafka监听消息异常");
		throw new Exception("批量消费-模拟异常");
	}
}