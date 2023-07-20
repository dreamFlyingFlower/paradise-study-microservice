package com.wy.kafka.consumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

/**
 * 原生Kafka消费者
 *
 * @author 飞花梦影
 * @date 2023-07-15 22:00:02
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
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

	private static Properties buildProperties() {
		Properties props = new Properties();
		props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.150:9092");
		props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test");
		props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		return props;
	}

	/**
	 * 自动提交
	 */
	public static void test01() {
		Properties props = new Properties();
		props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.150:9092");
		// 指定消费组ID
		props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test");
		// 指定自动提交,不手动进行ack确认
		props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		// 当partition中没有初始偏移量或当前偏移量不存在时,如何处理
		// earliest:自动重置偏移量到最早的偏移量
		// latest:自动重置偏移量到最新的偏移量
		// none:如果消费组原来的(previous)偏移量不存在,则向消费者抛异常
		// anything:向消费者抛异常
		props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "anything");
		props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
			// 消费订阅哪一个Topic或者几个Topic
			consumer.subscribe(Arrays.asList(TOPIC_NAME));
			while (true) {
				// 如果Topic中没有可以消费的数据,等待多少时间再拉取,单位毫秒
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
		Properties props = buildProperties();

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
		Properties props = buildProperties();

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
		Properties props = buildProperties();

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
			// dream-topic - 0,1两个partition
			TopicPartition p0 = new TopicPartition(TOPIC_NAME, 0);
			TopicPartition p1 = new TopicPartition(TOPIC_NAME, 1);
			System.out.println(p1);

			// 消费订阅哪一个Topic或者几个Topic
			// consumer.subscribe(Arrays.asList(TOPIC_NAME));

			// 手动指定消费者订阅某个Topic的某个分区
			// 手动分配分区不支持增量分配,如果先前有分配分区,则该操作会覆盖之前的分配
			// 如果给出的主题分区是空的,则等价于调用unsubscribe方法
			// 手动分配主题分区的方法不使用消费组管理功能,当消费组成员变了,或者集群或主题的元数据改变了,不会触发分区分配的再平衡
			// 手动分区分配assign(Collection)不能和自动分区分配subscribe(Collection,
			// ConsumerRebalanceListener)一起使用
			// 如果启用了自动提交偏移量,则在新的分区分配替换旧的分区分配之前,会对旧的分区分配中的消费偏移量进行异步提交
			consumer.assign(Arrays.asList(p0));

			// 获取给当前消费者分配的分区集合.如果订阅是通过调用assign()直接分配主题分区,则返回相同的集合
			// 如果使用了主题订阅,该方法返回当前分配给该消费者的主题分区集合;如果分区订阅还没开始进行分区分配,或者正在重新分配分区,则会返回none
			consumer.assignment();

			// 获取对用户授权的所有主题分区元数据.该方法会对服务器发起远程调用
			consumer.listTopics();

			// 获取指定主题的分区元数据.如果当前消费者没有关于该主题的元数据,就会对服务器发起远程调用
			consumer.partitionsFor(TOPIC_NAME);

			// 对于给定的主题分区,列出它们第一个消息的偏移量.如果指定的分区不存在,该方法可能会永远阻塞
			// 该方法不改变分区的当前消费者偏移量
			// consumer.beginningOffsets(null);

			// 将偏移量移动到每个给定分区的最后一个.该方法延迟执行,只有当调用过poll()或position()之后才可以使用
			// 如果没有指定分区,则将当前消费者分配的所有分区的消费者偏移量移动到最后
			// 如果设置了隔离级别为:isolation.level=read_committed,则会将消费偏移量移动到最后一个稳定的偏移量,即下一个要消费的现在还是未提交状态的事务消息
			// consumer.seekToEnd(null);

			// 将给定主题分区的消费偏移量移动到指定的偏移量,即当前消费者下一条要消费的消息偏移量
			// 若该方法多次调用,则最后一次的覆盖前面的.如果在消费中间随意使用,可能会丢失数据
			// consumer.seek(p1, 0);

			// 将给定每个分区的消费者偏移量移动到它们的起始偏移量.
			// 该方法懒执行,只有当调用过poll()或position()之后才会执行;如果没有提供分区,则将所有分配给当前消费者的分区消费偏移量移动到起始偏移量
			// consumer.seekToBeginning(null);

			// 检查指定主题分区的消费偏移量
			consumer.position(p1);

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

	/**
	 * 手动指定offset的起始位置,及手动提交offset
	 */
	public static void controlOffset() {
		Properties props = buildProperties();

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
			// dream-topic - 0,1两个partition
			TopicPartition p0 = new TopicPartition(TOPIC_NAME, 0);

			// 消费订阅某个Topic的某个分区
			consumer.assign(Arrays.asList(p0));

			while (true) {
				// 手动指定offset起始位置
				// 人为控制offset起始位置,如果出现程序错误,重复消费一次
				// 第一次从0消费【一般情况】
				// 比如一次消费了100条, offset置为101并且存入Redis
				// 每次poll之前,从redis中获取最新的offset位置
				// 每次从这个位置开始消费
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

	/**
	 * 流量控制 - 限流
	 */
	private static void controlPause() {
		Properties props = buildProperties();

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
						// 接收到record信息以后,去令牌桶中拿取令牌
						// 如果获取到令牌,则继续业务处理
						// 如果获取不到令牌, 则pause等待令牌
						// 当令牌桶中的令牌足够, 则将consumer置为resume状态
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
		Properties props = buildProperties();
		// 端口需要根据Kafka中配置的SSL地址修改
		props.setProperty("bootstrap.servers", "192.168.1.150:8989");
		// 协议
		props.setProperty("security.protocol", "SSL");
		// 算法, 使用默认即可
		props.setProperty("ssl.endpoint.identification.algorithm", "");
		// 证书地址,可以是绝对路径,也可以是项目中的相对路径.注意,此处的证书是客户端使用的证书,Kafka使用服务器的证书
		props.setProperty("ssl.truststore.location", "client.truststore.jks");
		// 证书密码
		props.setProperty("ssl.truststore.password", "dream");

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
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