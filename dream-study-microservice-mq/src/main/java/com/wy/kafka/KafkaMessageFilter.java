package com.wy.kafka;import org.apache.kafka.clients.consumer.ConsumerRecord;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.context.annotation.Bean;import org.springframework.kafka.annotation.KafkaListener;import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;import org.springframework.kafka.core.ConsumerFactory;import org.springframework.stereotype.Component;/** * Kafka消息过滤器 * * @author 飞花梦影 * @date 2021-04-24 18:16:55 * @git {@link https://github.com/dreamFlyingFlower} */@Componentpublic class KafkaMessageFilter {	@Autowired	ConsumerFactory<String, Object> consumerFactory;	/**	 * 消息过滤器	 * 	 * @return 消息过滤器	 */	@Bean	public ConcurrentKafkaListenerContainerFactory<String, Object> filterContainerFactory() {		ConcurrentKafkaListenerContainerFactory<String, Object> factory =				new ConcurrentKafkaListenerContainerFactory<>();		factory.setConsumerFactory(consumerFactory);		// 被过滤的消息将被丢弃		factory.setAckDiscarded(true);		// 消息过滤策略,设置消息的值为偶数时处理		factory.setRecordFilterStrategy(consumerRecord -> {			if (Integer.parseInt(consumerRecord.value().toString()) % 2 == 0) {				return false;			}			// 返回true消息则被过滤			return true;		});		return factory;	}	/**	 * 消息过滤监听	 * 	 * @param record 消息记录	 */	@KafkaListener(topics = { "testTopic1" }, containerFactory = "filterContainerFactory")	public void onMessage6(ConsumerRecord<?, ?> record) {		System.out.println(record.value());	}}