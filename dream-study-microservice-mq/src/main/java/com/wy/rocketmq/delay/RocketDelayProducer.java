package com.wy.rocketmq.delay;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.TimeUnit;

/**
 * RocketMQ延迟队列,延迟时间不支持自定义,只有固定时长:1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m
 * 20m 30m 1h 2h
 *
 * @author 飞花梦影
 * @date 2022-06-26 18:09:27
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class RocketDelayProducer {

	public static void main(String[] args)
			throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
		DefaultMQProducer producer = new DefaultMQProducer("group1");
		producer.setNamesrvAddr("192.168.1.150:9876");
		producer.start();

		for (int i = 0; i < 10; i++) {
			// 参数:消息主题Topic,消息Tag,消息内容
			Message msg = new Message("DelayTopic", "Tag1", ("Hello World" + i).getBytes());
			// 设定延迟时间,次数的2是延迟等级2,不是2S,而是5S
			msg.setDelayTimeLevel(2);
			SendResult result = producer.send(msg);
			System.out.println("发送结果:" + result);
			SendStatus status = result.getSendStatus();
			System.out.println(status);
			TimeUnit.SECONDS.sleep(1);
		}

		// 关闭生产者producer
		producer.shutdown();
	}
}