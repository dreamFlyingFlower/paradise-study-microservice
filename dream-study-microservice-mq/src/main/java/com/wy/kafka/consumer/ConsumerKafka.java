package com.wy.kafka.consumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

public class ConsumerKafka {

	private final static String TOPIC_NAME = "dream-topic";

	public static void main(String[] args) {
		test01();
		// 手动提交offset
		commitedOffset();
		// 手动对每个Partition进行提交
		commitedOffsetWithTopic();
		// 手动订阅某个或某些分区,并提交offset
		commitedOffsetWithPartition();
		// 手动指定offset的起始位置,及手动提交offset
		controlOffset();
		// 流量控制
		controlPause();
	}

	/**
	 * 自动提交
	 */
	public static void test01() {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "192.168.1.150:9092");
		props.setProperty("group.id", "test");
		props.setProperty("enable.auto.commit", "true");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
			// 消费订阅哪一个Topic或者几个Topic
			consumer.subscribe(Arrays.asList(TOPIC_NAME));
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
				for (ConsumerRecord<String, String> record : records)
					System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n", record.partition(),
							record.offset(), record.key(), record.value());
			}
		}
	}

	/**
	 * 手动提交offset
	 */
	public static void commitedOffset() {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "192.168.1.150:9092");
		// 消费分组
		props.setProperty("group.id", "test");
		// 手动提交
		props.setProperty("enable.auto.commit", "false");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);) {
			// 消费订阅哪一个Topic或者几个Topic
			consumer.subscribe(Arrays.asList(TOPIC_NAME));
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
				for (ConsumerRecord<String, String> record : records) {
					// 想把数据保存到数据库,成功就成功,不成功写redis或其他
					System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n", record.partition(),
							record.offset(), record.key(), record.value());
					// 如果失败,则回滚, 不要提交offset
				}

				// 如果成功,手动通知offset提交
				consumer.commitAsync();
			}
		}
	}

	/**
	 * 手动提交offset,并且手动控制Topic中的partition
	 */
	public static void commitedOffsetWithTopic() {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "192.168.1.150:9092");
		props.setProperty("group.id", "test");
		props.setProperty("enable.auto.commit", "false");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
			// 消费订阅哪一个Topic或者几个Topic
			consumer.subscribe(Arrays.asList(TOPIC_NAME));

			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
				// 每个partition单独处理
				for (TopicPartition partition : records.partitions()) {
					List<ConsumerRecord<String, String>> pRecord = records.records(partition);
					for (ConsumerRecord<String, String> record : pRecord) {
						System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n", record.partition(),
								record.offset(), record.key(), record.value());

					}
					// 最后一个record的offset
					long lastOffset = pRecord.get(pRecord.size() - 1).offset();
					// 单个partition中的offset,并且进行提交
					Map<TopicPartition, OffsetAndMetadata> offset = new HashMap<>();
					// +1是为了防止重复提交,下一次提交的起点是本次提交的终点
					offset.put(partition, new OffsetAndMetadata(lastOffset + 1));
					// 提交offset
					consumer.commitSync(offset);
					System.out.println("=============partition - " + partition + " end================");
				}
			}
		}
	}

	/**
	 * 手动提交offset,并且手动控制指定partition
	 */
	public static void commitedOffsetWithPartition() {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "192.168.1.150:9092");
		props.setProperty("group.id", "test");
		props.setProperty("enable.auto.commit", "false");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
			// dream-topic - 0,1两个partition
			TopicPartition p0 = new TopicPartition(TOPIC_NAME, 0);
			TopicPartition p1 = new TopicPartition(TOPIC_NAME, 1);
			System.out.println(p1);

			// 消费订阅哪一个Topic或者几个Topic
			// consumer.subscribe(Arrays.asList(TOPIC_NAME));

			// 消费订阅某个Topic的某个分区
			consumer.assign(Arrays.asList(p0));

			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
				// 每个partition单独处理
				for (TopicPartition partition : records.partitions()) {
					List<ConsumerRecord<String, String>> pRecord = records.records(partition);
					for (ConsumerRecord<String, String> record : pRecord) {
						System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n", record.partition(),
								record.offset(), record.key(), record.value());

					}
					long lastOffset = pRecord.get(pRecord.size() - 1).offset();
					// 单个partition中的offset,并且进行提交
					Map<TopicPartition, OffsetAndMetadata> offset = new HashMap<>();
					offset.put(partition, new OffsetAndMetadata(lastOffset + 1));
					// 提交offset
					consumer.commitSync(offset);
					System.out.println("=============partition - " + partition + " end================");
				}
			}
		}
	}

	/*
	 * 手动指定offset的起始位置,及手动提交offset
	 */
	public static void controlOffset() {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "192.168.1.150:9092");
		props.setProperty("group.id", "test");
		props.setProperty("enable.auto.commit", "false");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
			// dream-topic - 0,1两个partition
			TopicPartition p0 = new TopicPartition(TOPIC_NAME, 0);

			// 消费订阅某个Topic的某个分区
			consumer.assign(Arrays.asList(p0));

			while (true) {
				// 手动指定offset起始位置
				/*
				 * 1、人为控制offset起始位置 2、如果出现程序错误,重复消费一次
				 */
				/*
				 * 1、第一次从0消费【一般情况】 2、比如一次消费了100条, offset置为101并且存入Redis
				 * 3、每次poll之前,从redis中获取最新的offset位置 4、每次从这个位置开始消费
				 */
				consumer.seek(p0, 700);

				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
				// 每个partition单独处理
				for (TopicPartition partition : records.partitions()) {
					List<ConsumerRecord<String, String>> pRecord = records.records(partition);
					for (ConsumerRecord<String, String> record : pRecord) {
						System.err.printf("patition = %d , offset = %d, key = %s, value = %s%n", record.partition(),
								record.offset(), record.key(), record.value());

					}
					long lastOffset = pRecord.get(pRecord.size() - 1).offset();
					// 单个partition中的offset,并且进行提交
					Map<TopicPartition, OffsetAndMetadata> offset = new HashMap<>();
					offset.put(partition, new OffsetAndMetadata(lastOffset + 1));
					// 提交offset
					consumer.commitSync(offset);
					System.out.println("=============partition - " + partition + " end================");
				}
			}
		}
	}

	/*
	 * 流量控制 - 限流
	 */
	private static void controlPause() {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "192.168.1.150:9092");
		props.setProperty("group.id", "test");
		props.setProperty("enable.auto.commit", "false");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
			// dream-topic - 0,1两个partition
			TopicPartition p0 = new TopicPartition(TOPIC_NAME, 0);
			TopicPartition p1 = new TopicPartition(TOPIC_NAME, 1);

			// 消费订阅某个Topic的某个分区
			consumer.assign(Arrays.asList(p0, p1));
			long totalNum = 40;
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
				// 每个partition单独处理
				for (TopicPartition partition : records.partitions()) {
					List<ConsumerRecord<String, String>> pRecord = records.records(partition);
					long num = 0;
					for (ConsumerRecord<String, String> record : pRecord) {
						System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n", record.partition(),
								record.offset(), record.key(), record.value());
						/*
						 * 1、接收到record信息以后,去令牌桶中拿取令牌 2、如果获取到令牌,则继续业务处理 3、如果获取不到令牌, 则pause等待令牌
						 * 4、当令牌桶中的令牌足够, 则将consumer置为resume状态
						 */
						num++;
						if (record.partition() == 0) {
							if (num >= totalNum) {
								consumer.pause(Arrays.asList(p0));
							}
						}

						if (record.partition() == 1) {
							if (num == 40) {
								consumer.resume(Arrays.asList(p0));
							}
						}
					}

					long lastOffset = pRecord.get(pRecord.size() - 1).offset();
					// 单个partition中的offset,并且进行提交
					Map<TopicPartition, OffsetAndMetadata> offset = new HashMap<>();
					offset.put(partition, new OffsetAndMetadata(lastOffset + 1));
					// 提交offset
					consumer.commitSync(offset);
					System.out.println("=============partition - " + partition + " end================");
				}
			}
		}
	}

	/**
	 * 访问Kafka需要证书,需要将SSL证书(client.truststore.jks)放到指定目录中
	 */
	public static void testSSL() {
		Properties props = new Properties();
		// 端口需要根据Kafka中配置的SSL地址修改
		props.setProperty("bootstrap.servers", "192.168.1.150:8989");
		props.setProperty("group.id", "test");
		props.setProperty("enable.auto.commit", "true");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		// 协议
		props.setProperty("security.protocol", "SSL");
		// 算法, 使用默认即可
		props.setProperty("ssl.endpoint.identification.algorithm", "");
		// 证书地址,可以是绝对路径,也可以是项目中的相对路径.注意,此处的证书是客户端使用的证书,Kafka使用服务器的证书
		props.setProperty("ssl.truststore.location", "client.truststore.jks");
		// 证书密码
		props.setProperty("ssl.truststore.password", "dream");
		
		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
			// 消费订阅哪一个Topic或者几个Topic
			consumer.subscribe(Arrays.asList(TOPIC_NAME));
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
				for (ConsumerRecord<String, String> record : records)
					System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n", record.partition(),
							record.offset(), record.key(), record.value());
			}
		}
	}
}