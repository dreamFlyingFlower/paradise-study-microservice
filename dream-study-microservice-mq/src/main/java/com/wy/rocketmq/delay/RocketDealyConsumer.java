package com.wy.rocketmq.delay;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 延迟队列消费者
 *
 * @author 飞花梦影
 * @date 2022-06-26 18:10:43
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class RocketDealyConsumer {

	public static void main(String[] args) throws Exception {
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group1");
		consumer.setNamesrvAddr("192.168.25.135:9876;192.168.25.138:9876");
		consumer.subscribe("DelayTopic", "*");

		consumer.registerMessageListener(new MessageListenerConcurrently() {

			@Override
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
				for (MessageExt msg : msgs) {
					System.out.println("消息ID：【" + msg.getMsgId() + "】,延迟时间："
							+ (System.currentTimeMillis() - msg.getStoreTimestamp()));
				}
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});
		consumer.start();
		System.out.println("消费者启动");
	}
}