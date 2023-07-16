package com.wy.kafka.producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

/**
 * Producer,是线程安全的,即使是循环发送,但是内部仍然是批量发送,见 {@link KafkaProducer#doSend}
 * 
 * <pre>
 * 加载MetricConfig
 * 加载负载均衡器
 * 初始化Serializer
 * 初始化RecordAccumulator,类似于计数器
 * 启动newSender,守护线程
 * </pre>
 *
 * @author 飞花梦影
 * @date 2022-07-23 14:56:02
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class ProducerKafka {

	private final static String TOPIC_NAME = "dream-topic";

	public static void main(String[] args) throws Exception {
		// Producer异步发送
		producerSend();
		// Producer同步发送
		producerSyncSend();
		// Producer异步发送带回调函数
		producerSendWithCallback();
		// Producer异步发送带回调函数和Partition负载均衡
		producerSendWithCallbackAndPartition();
	}

	/**
	 * Producer异步发送
	 */
	public static void producerSend() {
		Properties properties = buildProperties();

		// Producer的主对象
		Producer<String, String> producer = new KafkaProducer<>(properties);

		// 消息对象 - ProducerRecord
		for (int i = 0; i < 10; i++) {
			ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, "key-" + i, "value-" + i);
			producer.send(record);
		}

		// 所有的通道打开都需要关闭
		producer.close();
	}

	/**
	 * Producer同步发送
	 */
	public static void producerSyncSend() throws ExecutionException, InterruptedException, TimeoutException {
		Properties properties = buildProperties();

		// Producer的主对象
		Producer<String, String> producer = new KafkaProducer<>(properties);

		// 自定义消息头信息
		List<Header> headers = new ArrayList<>();
		headers.add(new RecordHeader("server.name", "order".getBytes()));

		// 消息对象 - ProducerRecoder
		for (int i = 0; i < 10; i++) {
			String key = "key-" + i;
			ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, key, "value-" + i);
			// new ProducerRecord<>(TOPIC_NAME, 0, key, i, headers);

			// 发送消息
			Future<RecordMetadata> send = producer.send(record);
			// 会阻塞
			RecordMetadata recordMetadata = send.get();
			// 超时阻塞
			send.get(3, TimeUnit.SECONDS);
			System.out.println(
					key + "partition : " + recordMetadata.partition() + " , offset : " + recordMetadata.offset());
		}

		// 所有的通道打开都需要关闭
		producer.close();
	}

	/**
	 * 构建kafka配置,使用Properties或Map都可以
	 * 
	 * @return Properties
	 */
	private static Properties buildProperties() {
		Properties properties = new Properties();
		// kafka服务地址,多个用逗哥分割
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.150:9092");
		// ACK机制:
		// 0:不需要进行ACK确认,且不会进行重试(重试配置无效),返回的offset总是为-1
		// 1:至少等待leader已经成功将数据写入本地log,但是并没有等待所有follower写入
		// all:leader和follower都需要写入成功才能返回
		properties.put(ProducerConfig.ACKS_CONFIG, "all");
		// 重试次数
		properties.put(ProducerConfig.RETRIES_CONFIG, "0");
		// 批次大小
		properties.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
		// 多长时间发送一次批次数据
		properties.put(ProducerConfig.LINGER_MS_CONFIG, "1");
		// 缓存
		properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
		// key和value序列化
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringSerializer");
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringSerializer");
		return properties;
	}

	/**
	 * Producer异步发送带回调函数
	 */
	public static void producerSendWithCallback() {
		Properties properties = buildProperties();

		// Producer的主对象
		Producer<String, String> producer = new KafkaProducer<>(properties);

		// 消息对象 - ProducerRecoder
		for (int i = 0; i < 10; i++) {
			ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, "key-" + i, "value-" + i);

			producer.send(record, new Callback() {

				@Override
				public void onCompletion(RecordMetadata recordMetadata, Exception e) {
					System.out.println(
							"partition : " + recordMetadata.partition() + " , offset : " + recordMetadata.offset());
				}
			});
		}

		// 所有的通道打开都需要关闭
		producer.close();
	}

	/**
	 * Producer异步发送带回调函数和Partition负载均衡
	 */
	public static void producerSendWithCallbackAndPartition() {
		Properties properties = buildProperties();
		// 指定自定义分区策略
		properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "com.wy.kafka.CustomizePartitiner");

		// Producer的主对象
		Producer<String, String> producer = new KafkaProducer<>(properties);

		// 消息对象 - ProducerRecoder
		for (int i = 0; i < 10; i++) {
			ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, "key-" + i, "value-" + i);

			producer.send(record, new Callback() {

				@Override
				public void onCompletion(RecordMetadata recordMetadata, Exception e) {
					System.out.println(
							"partition : " + recordMetadata.partition() + " , offset : " + recordMetadata.offset());
				}
			});
		}

		// 所有的通道打开都需要关闭
		producer.close();
	}
}