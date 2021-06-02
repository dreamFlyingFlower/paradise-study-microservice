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
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	/**
	 * 该方法的返回值就表示将消息发送到几号分区
	 */
	@Override
	public int partition(String arg0, Object arg1, byte[] arg2, Object arg3, byte[] arg4, Cluster arg5) {
		// 自定义分区规则(这里假设全部发到0号分区)
		return 0;
	}
}