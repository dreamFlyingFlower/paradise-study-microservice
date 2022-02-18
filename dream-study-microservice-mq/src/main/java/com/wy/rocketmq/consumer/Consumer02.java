package com.wy.rocketmq.consumer;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * RocketMQ消费者,监听消息
 * 
 * @author 飞花梦影
 * @date 2021-04-09 12:00:56
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
@RocketMQMessageListener(topic = "test-rocket-topic", consumerGroup = "test-rocket-group")
public class Consumer02 implements RocketMQListener<MessageExt> {

	/**
	 * 监听队列中的消息,可以是同步,异步,单向
	 * 
	 * @param message 消息
	 */
	@Override
	public void onMessage(MessageExt message) {
		byte[] body = message.getBody();
		System.out.println(new String(body));
		// 消费失败,消息重试,取出当前重试次数
		int times = message.getReconsumeTimes();
		// 当大于一定次数时,将消息写入数据库,由其他程序或人工处理
		if (times > 5) {
			// 其他处理
		}
	}
}