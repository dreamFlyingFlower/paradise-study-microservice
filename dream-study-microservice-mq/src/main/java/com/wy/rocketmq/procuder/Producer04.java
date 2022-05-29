package com.wy.rocketmq.procuder;

import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;

import com.wy.rocketmq.listener.Producer04Listener;

/**
 * RocketMQ原生事务消费者
 *
 * @author 飞花梦影
 * @date 2022-05-29 11:53:58
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class Producer04 {

	public static void main(String[] args) throws Exception {
		TransactionMQProducer producer = new TransactionMQProducer("transaction_producer");
		producer.setNamesrvAddr("192.168.1.150:9876");

		// 设置事务监听器
		producer.setTransactionListener(new Producer04Listener());
		producer.start();
		// 发送消息
		Message message = new Message("pay_topic", "用户A给用户B转账5000元".getBytes("UTF-8"));
		producer.sendMessageInTransaction(message, null);
		Thread.sleep(999999);
		producer.shutdown();
	}
}