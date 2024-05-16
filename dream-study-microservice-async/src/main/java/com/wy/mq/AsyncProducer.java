package com.wy.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.dream.lang.StrHelper;
import com.wy.constant.AsyncConstant;
import com.wy.dto.AsyncRequestDTO;
import com.wy.properties.AsyncMqProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * 异步执行提供者
 *
 * @author 飞花梦影
 * @date 2024-05-16 14:02:17
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Component
public class AsyncProducer {

	@Autowired
	private KafkaTemplate<String, AsyncRequestDTO> kafkaTemplate;

	@Value("${spring.application.name}")
	private String appName;

	@Autowired
	private AsyncMqProperties asyncMqProperties;

	/**
	 * 发送消息
	 *
	 * @param asyncExecDto
	 * @return
	 */
	public boolean send(AsyncRequestDTO asyncExecDto) {
		String queueName = StrHelper.getDefault(asyncMqProperties.getTopic(), appName) + AsyncConstant.QUEUE_SUFFIX;
		try {
			log.info("kafka消息开始发送,queueName：'{}', message：{}", queueName, asyncExecDto);
			kafkaTemplate.send(queueName, asyncExecDto);
			log.info("kafka消息发送成功,queueName：'{}', message：{}", queueName, asyncExecDto);
			return true;
		} catch (Exception e) {
			log.error("kafka消息发送失败,queueName：'{}', message：{}", queueName, asyncExecDto, e);
			return false;
		}
	}
}