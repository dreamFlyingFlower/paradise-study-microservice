package com.wy.rocketmq.order;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 顺序消息消费者,模拟订单业务:创建,付款,推送,完成
 *
 * @author 飞花梦影
 * @date 2022-06-26 10:19:40
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class RocketOrderConsumer {

	public static void main(String[] args) throws MQClientException {
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group_order");
		consumer.setNamesrvAddr("192.168.1.150:9876");
		/**
		 * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费,如果非第一次启动,那么按照上次消费的位置继续消费
		 */
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

		consumer.subscribe("TopicTest", "*");

		// 使用顺序监听器监听消息
		consumer.registerMessageListener(new MessageListenerOrderly() {

			@Override
			public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
				for (MessageExt msg : msgs) {
					// 每个queue有唯一的consume线程来消费, 订单对每个queue(分区)有序
					System.out.println("consumeThread=" + Thread.currentThread().getName() + "queueId="
							+ msg.getQueueId() + ", content:" + new String(msg.getBody()));
				}
				return ConsumeOrderlyStatus.SUCCESS;
			}
		});

		consumer.start();
		System.out.println("消费者启动");
	}
}