package com.wy.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

/**
 * Kafka流式处理,非实时,有延迟
 *
 * @author 飞花梦影
 * @date 2022-07-29 00:11:18
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class MyKafkaStream {

	private static final String INPUT_TOPIC = "dream-stream-in";

	private static final String OUT_TOPIC = "dream-stream-out";

	public static void main(String[] args) {
		Properties props = new Properties();
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.150:9092");
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-app");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		// 如果构建流结构拓扑
		final StreamsBuilder builder = new StreamsBuilder();
		// 构建Wordcount单词出现次数计算
		wordcountStream(builder);
		// 构建foreachStream
		foreachStream(builder);

		KafkaStreams streams = new KafkaStreams(builder.build(), props);

		streams.start();
		// do something...
		streams.close();
	}

	/**
	 * 定义流计算过程
	 * 
	 * @param builder
	 */
	static void foreachStream(final StreamsBuilder builder) {
		KStream<String, String> source = builder.stream(INPUT_TOPIC);
		// 将value值根据空格拆分后返回
		source.flatMapValues(value -> Arrays.asList(value.toLowerCase(Locale.getDefault()).split(" ")))
				.foreach((key, value) -> System.out.println(key + " : " + value));
	}

	/**
	 * 定义流计算过程,经典的大数据wordcount
	 * 
	 * @param builder
	 */
	static void wordcountStream(final StreamsBuilder builder) {
		// 不断从INPUT_TOPIC上获取新数据,并且追加到流上的一个抽象对象,类似于队列
		KStream<String, String> source = builder.stream(INPUT_TOPIC);
		// Hello World dream
		// KTable是数据集合的抽象对象
		// 算子
		final KTable<String, Long> count = source
				// flatMapValues -> 数据拆分,将一行数据拆分为多行数据 key -> 1 , value -> Hello World
				// 拆分为key->1,value->Hello;key->1,value->World
				/*
				 * key 1 , value Hello ;key 2 , value World ;key 3 , value World
				 * 
				 * 最终计算单词出现的次数结果为: Hello 1 World 2
				 */
				.flatMapValues(value -> Arrays.asList(value.toLowerCase(Locale.getDefault()).split(" ")))
				// 合并 -> 按value值合并
				.groupBy((key, value) -> value)
				// 统计出现的总数
				.count();

		// 将结果输入到OUT_TOPIC中
		count.toStream().to(OUT_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));
	}
}