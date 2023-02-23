package com.wy.rabbitmq.simple;

import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.wy.rabbitmq.util.ConnectionUtil;

/**
 * 基本消息模型生产者
 *
 * @author 飞花梦影
 * @date 2021-01-04 21:35:04
 * @git {@link https://github.com/mygodness100}
 */
public class ProviderSimple {

	private final static String QUEUE_NAME = "simple_queue";

	private final static String QUEUE_NAME2 = "simple_queue2";

	public static void main(String[] argv) throws Exception {
		// 获取到连接
		Connection connection = ConnectionUtil.getConnection();
		// 从连接中创建通道,使用通道才能完成消息相关的操作
		Channel channel = connection.createChannel();
		// 开启生产者的消息确认机制,默认不开启,高并发下不建议开启
		channel.confirmSelect();
		// 批量发送消息时,存储唯一编号和消息
		ConcurrentSkipListMap<Long, String> concurrentSkipListMap = new ConcurrentSkipListMap<>();
		// 消息异步确认回调
		channel.addConfirmListener((long deliveryTag, boolean multiple) -> {
			System.out.println("消息发送成功");
			if (multiple) {
				// 如果是批量确认,从concurrentSkipListMap中剔除deliveryTag
				ConcurrentNavigableMap<Long, String> headMap = concurrentSkipListMap.headMap(deliveryTag);
				headMap.clear();
			} else {
				// 如果是单条消息确认,直接移除
				concurrentSkipListMap.remove(deliveryTag);
			}
		}, (long deliveryTag, boolean multiple) -> {
			System.out.println("消息发送失败");
			// 获得未确认的消息
			concurrentSkipListMap.get(deliveryTag);
		});
		// 声明队列,这是一个幂等的操作,只有当它不存在时才会被自动创建
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		// 消息内容
		String message = "Hello World!";
		// 每个消息都有一个唯一编号,当批量发送消息,而进行异步确认时,为了找到可能确认失败的消息,将所有的编号存入队列
		concurrentSkipListMap.put(channel.getNextPublishSeqNo(), message);
		// 向指定的队列中发送消息
		// MessageProperties.PERSISTENT_TEXT_PLAIN指定消息持久化,默认传null,不持久化,持久化意义不大
		channel.basicPublish("", QUEUE_NAME2, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
		channel.basicQos(1);
		System.out.println("Sent '" + message + "'");
		// 关闭通道和连接
		channel.close();
		connection.close();
	}
}