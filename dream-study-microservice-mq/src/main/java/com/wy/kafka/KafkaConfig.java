package com.wy.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;

/**
 * Kafka配置,必须先配置zookeeper(https://www.cnblogs.com/shanyou/p/3221990.html)
 * 	
 * 配置文件,在application-kafka中:
 * 
 * <pre>
 * spring.kafka.producer.batch-size:批量大小
 * spring.kafka.producer.properties.linger.ms:当生产端积累的消息达到batch-size或接收到消息linger.ms后,生产者就会将消息提交给kafka
 * linger.ms为0表示每接收到一条消息就提交给kafka,这时候batch-size其实就没用了
 * </pre>
 * 
 * 自定义分区:kafka中每个topic被划分为多个分区,生产者将消息发送到topic时,具体追加到哪个分区呢?这就是所谓的分区策略,
 * Kafka为我们提供了默认的分区策略,同时它也支持自定义分区策略,其路由机制为
 * 
 * <pre>
 * 1.若发送消息时指定了分区,即自定义分区策略,则直接将消息append到指定分区
 * 2.若发送消息时未指定patition,但指定了key(kafka允许为每条消息设置一个key),则对key值进行hash计算,
 * 		根据计算结果路由到指定分区,这种情况下可以保证同一个Key的所有消息都进入到相同的分区
 * 3.patition 和 key 都未指定,则使用kafka默认的分区策略,轮询选出一个 patition
 * </pre>
 *
 *	@author ParadiseWY
 *	@date 2020-12-08 12:59:49
 * @git {@link https://github.com/mygodness100}
 */
@Configuration
public class KafkaConfig {
	/**
	 * 创建一个名为topicName的Topic,设置分区数为8,分区副本为2
	 * 
	 * @return topic
	 */
	public NewTopic newTopic() {
		// 修改分区数并不会导致数据的丢失,但是分区数只能增大不能减小
		// new NewTopic("topicName", 10, (short) 2);
		return new NewTopic("topicName", 8, (short) 2);
	}
}
