package com.wy.kafka;

import org.apache.kafka.common.serialization.Serializer;

/**
 * 自定义序列化器,Kafka已经定义了基本类型的序列化方式,生产者使用序列化,消费者使用反序列化
 *
 * @author 飞花梦影
 * @date 2023-07-16 09:13:57
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class MyKafkaSerializer implements Serializer<String> {

	@Override
	public byte[] serialize(String topic, String data) {
		return null;
	}

}