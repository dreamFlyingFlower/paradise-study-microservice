package com.wy.kafka;

import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

/**
 * Kafka自定义分区,将消息发送到指定的partition,需要实现kafka指定接口,同时需要在配置文件中指定自定义分区的class
 *
 * @author 飞花梦影
 * @date 2021-04-24 17:59:28
 */
public class CustomizePartitiner implements Partitioner {

	@Override
	public void configure(Map<String, ?> arg0) {

	}

	@Override
	public void close() {

	}

	/**
	 * 该方法的返回值就表示将消息发送到几号分区
	 * 
	 * @param topic 主题
	 * @param key 生产者发送时使用的key,可以通过该规则key进行自定义分区
	 * @param keyBytes
	 * @param value
	 * @param valueBytes
	 * @param cluster
	 * @return
	 */
	@Override
	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
		// 自定义分区规则(这里假设全部发到0号分区)
		return 0;
	}
}