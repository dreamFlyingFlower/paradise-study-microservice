package com.wy.kafka.wechat.conf;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConf {

	@Autowired
	private KafkaProperties kafkaProperties;

	@Bean
	public Producer<String, Object> kafkaProducer() {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		properties.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getAcksConfig());
		properties.put(ProducerConfig.RETRIES_CONFIG, "0");
		properties.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
		properties.put(ProducerConfig.LINGER_MS_CONFIG, "1");
		properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");

		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
		        "org.apache.kafka.common.serialization.StringSerializer");
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
		        "org.apache.kafka.common.serialization.StringSerializer");

		// Producer的主对象
		Producer<String, Object> producer = new KafkaProducer<>(properties);

		return producer;
	}
}