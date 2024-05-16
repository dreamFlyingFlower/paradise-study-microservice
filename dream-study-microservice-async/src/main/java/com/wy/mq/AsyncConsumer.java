package com.wy.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.dream.lang.StrHelper;
import com.wy.constant.AsyncConstant;
import com.wy.dto.AsyncRequestDTO;
import com.wy.properties.AsyncMqProperties;
import com.wy.service.AsyncBizService;

import lombok.extern.slf4j.Slf4j;

/**
 * 异步执行消费者
 *
 * @author 飞花梦影
 * @date 2024-05-16 14:02:05
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Component
public class AsyncConsumer {

	@Value("${spring.application.name}")
	private String appName;

	@Autowired
	private AsyncBizService asyncBizService;

	@Autowired
	private AsyncMqProperties asyncMqProperties;

	/**
	 * 消费消息
	 *
	 * @param asyncExecDto
	 * @return
	 */
	@KafkaListener(topics = "${dream.async.topic:${spring.application.name}}" + AsyncConstant.QUEUE_SUFFIX,
			groupId = "${spring.application.name}")
	public void onConsume(AsyncRequestDTO asyncExecDto) {
		String queueName = StrHelper.getDefault(asyncMqProperties.getTopic(), appName) + AsyncConstant.QUEUE_SUFFIX;
		try {
			log.info("kafka消息开始消费,queueName：'{}',message：{}", queueName, asyncExecDto);
			// 执行方法
			asyncBizService.invoke(asyncExecDto);
			log.info("kafka消息消费成功,queueName：'{}',message：{}", queueName, asyncExecDto);
		} catch (Exception e) {
			log.error("kafka消息消费失败,queueName：'{}',message：{}", queueName, asyncExecDto, e);
			throw e;
		}
	}
}