package com.wy.rocketmq.order.spring;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2023-07-27 11:11:48
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Component
public class OrderProducer {

	@Autowired
	private RocketMQTemplate rocketMQTemplate;

	/**
	 * 同步顺序发送消息
	 * 
	 * @param id
	 * @return
	 */
	public SendResult syncSend(Integer id) {
		return rocketMQTemplate.syncSendOrderly("topic_roder", id, String.valueOf(id));
	}

	/**
	 * 异步顺序发送消息
	 * 
	 * @param id
	 * @param callback
	 */
	public void asyncSend(Integer id, SendCallback callback) {
		rocketMQTemplate.asyncSendOrderly("topic_roder", id, String.valueOf(id), callback);
	}

	/**
	 * 单向发送消息,只管发,不管结果
	 * 
	 * @param id
	 */
	public void onewaySend(Integer id) {
		rocketMQTemplate.sendOneWayOrderly("topic_roder", id, String.valueOf(id));
	}
}