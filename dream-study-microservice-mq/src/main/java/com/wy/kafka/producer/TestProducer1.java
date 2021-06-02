package com.wy.kafka.producer;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.kafka.core.KafkaTemplate;import org.springframework.kafka.support.SendResult;import org.springframework.stereotype.Component;import org.springframework.util.concurrent.ListenableFutureCallback;import lombok.extern.slf4j.Slf4j;/** * 简单的生产者 * * @author 飞花梦影 * @date 2021-04-24 17:43:03 */@Component@Slf4jpublic class TestProducer1 {	@Autowired	private KafkaTemplate<String, Object> kafkaTemplate;	/**	 * 消息生产者:发送简单消息,无返回值	 * 	 * @param message 需要发送的消息	 */	public void sendSimpleMsg(String message) {		kafkaTemplate.send("testTopic1", message);	}	/**	 * 发送有回调方法的消息	 * 	 * @param message	 */	public void sendCallBackMsg1(String message) {		kafkaTemplate.send("testTopic1", message).addCallback(success -> {			// 消息发送到的topic			String topic = success.getRecordMetadata().topic();			// 消息发送到的分区			int partition = success.getRecordMetadata().partition();			// 消息在分区内的offset			long offset = success.getRecordMetadata().offset();			System.out.println("发送消息成功:" + topic + "-" + partition + "-" + offset);		}, failure -> {			log.error("Kafka调用失败:" + failure.getMessage());		});	}	/**	 * 发送有回调方法的消息	 * 	 * @param message	 */	public void sendCallBackMsg2(String message) {		kafkaTemplate.send("testTopic1", message)				.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {					@Override					public void onSuccess(SendResult<String, Object> result) {						// 消息发送到的topic						String topic = result.getRecordMetadata().topic();						// 消息发送到的分区						int partition = result.getRecordMetadata().partition();						// 消息在分区内的offset						long offset = result.getRecordMetadata().offset();						System.out.println("发送消息成功:" + topic + "-" + partition + "-" + offset);					}					@Override					public void onFailure(Throwable ex) {						log.error("Kafka调用失败:" + ex.getMessage());					}				});	}	/**	 * Kafka事务提交	 */	public void sendTransaction() {		// 声明事务:后面报错消息不会发出去		kafkaTemplate.executeInTransaction(operations -> {			operations.send("testTopic1", "test executeInTransaction");			throw new RuntimeException("fail");		});		// 不声明事务:后面报错但前面消息已经发送成功了		kafkaTemplate.send("testTopic1", "test executeInTransaction");		throw new RuntimeException("fail");	}}