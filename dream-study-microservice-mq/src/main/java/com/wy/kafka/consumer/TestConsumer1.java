package com.wy.kafka.consumer;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

/**
 * Kafka消费者
 *
 * @author 飞花梦影
 * @date 2021-04-24 17:45:29
 */
@Component
public class TestConsumer1 {

	/**
	 * 消费监听:监听整个Topic中的消息
	 * 
	 * @param record 消费者消费对象
	 */
	@KafkaListener(topics = { "testTopic1" })
	public void onMessage1(ConsumerRecord<?, ?> record) {
		// 消费的哪个topic,partition的消息,打印出消息内容
		System.out.println("简单消费:" + record.topic() + "-" + record.partition() + "-" + record.value());
	}

	/**
	 * 监听指定Topic,partition,offset的消息
	 * 
	 * 同时监听testTopic1和testTopic2,监听testTopic1的0号分区,监听testTopic2的0号分区和1号分区里面offset从8开始的消息
	 * 
	 * <pre>
	 * id:消费者id,用来进行消费者标识
	 * groupId:消费组id,可以和配置文件中的默认组不同
	 * topics:监听的Topic,可监听多个,但是会监听Topic中所有的消息
	 * topicPartitions:配置更加详细的监听信息,可指定topic,parition,offset监听
	 * </pre>
	 */
	@KafkaListener(id = "consumer1", groupId = "group1",
			topicPartitions = { @TopicPartition(topic = "testTopic1", partitions = { "0" }),
					@TopicPartition(topic = "testTopic2", partitions = "0",
							partitionOffsets = @PartitionOffset(partition = "1", initialOffset = "8")) })
	public void onMessage2(ConsumerRecord<?, ?> record) {
		System.out.println("topic:" + record.topic() + "|partition:" + record.partition() + "|offset:" + record.offset()
				+ "|value:" + record.value());
	}

	/**
	 * 批量消费,需要配置文件中开启,用List来接收消息
	 * 
	 * <pre>
	 * spring.kafka.listener.type=batch:设置批量消费
	 * spring.kafka.consumer.max-poll-records=50:批量消费每次最多消费多少条消息
	 * </pre>
	 * 
	 * @param records
	 */
	@KafkaListener(id = "consumer2", groupId = "felix-group", topics = "testTopic1")
	public void onMessage3(List<ConsumerRecord<?, ?>> records) {
		System.out.println(">>>批量消费一次,records.size()=" + records.size());
		for (ConsumerRecord<?, ?> record : records) {
			System.out.println(record.value());
		}
	}

	/**
	 * 消息转发,从testTopic1接收到的消息经过处理后转发到testTopic2
	 * 
	 * @param record 消息
	 * @return 转发后的消息
	 */
	@KafkaListener(topics = { "testTopic1" })
	@SendTo("testTopic2")
	public String onMessage7(ConsumerRecord<?, ?> record) {
		return record.value() + "-forward message";
	}
}