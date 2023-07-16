package com.wy.kafka;

import org.apache.kafka.common.serialization.Deserializer;

/**
 * 自定义序列化器,Kafka已经定义了基本类型的序列化方式,生产者使用序列化,消费者使用反序列化
 *
 * @author 飞花梦影
 * @date 2023-07-16 09:13:57
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class MyKafkaDeserializer implements Deserializer<String> {

	@Override
	public String deserialize(String topic, byte[] data) {
		return null;
	}

}