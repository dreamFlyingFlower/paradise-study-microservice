package com.wy.kafka;

import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;

/**
 * Kafka自定义分区,将消息发送到指定的partition,需要实现kafka指定接口,同时需要在配置文件中指定自定义分区的class
 * 
 * {@link DefaultPartitioner}:Kafka默认进行分区的类,如果生产者提供了分区,则使用指定分区;若未提供,则使用key序列化之后的值的hash值对分区数取模;
 * 如果key和分区都没有提供,则使用轮询分配分区号:会先在可用的分区中分配分区号,如果没有可用分区,则在该主题所有分区中分配分区号
 *
 * @author 飞花梦影
 * @date 2021-04-24 17:59:28
 * @git {@link https://github.com/dreamFlyingFlower}
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
	 * @param keyBytes key的序列化字节数组,根据该数组进行分区计算.如果没有key,则为null
	 * @param value 根据value值进行分区计算,如果没有,则为null
	 * @param valueBytes value的序列化字节数组,根据此值进行分区计算.如果没有,则为null
	 * @param cluster 当前集群的元数据
	 * @return
	 */
	@Override
	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
		// 自定义分区规则(这里假设全部发到0号分区)
		return 0;
	}
}