package com.wy.rocketmq.procuder;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * RocketMQ生产者
 * 
 * @author 飞花梦影
 * @date 2021-04-09 11:57:10
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
public class Producer01 {

	@Autowired
	private RocketMQTemplate rocketMQTemplate;

	/**
	 * 发送同步消息
	 * 
	 * @param topic 主题
	 * @param msg 需要发送的消息
	 */
	public void sendSyncMsg(String topic, String msg) {
		SendResult sendResult = rocketMQTemplate.syncSend(topic, msg);
		System.out.println("等待同步消息的结果,阻塞:" + sendResult);
	}

	/**
	 * 发送异步消息
	 * 
	 * @param topic 主题
	 * @param msg 需要发送的消息
	 */
	public void sendAsyncMsg(String topic, String msg) {
		rocketMQTemplate.asyncSend(topic, msg, new SendCallback() {

			/**
			 * 消息发送成功的回调
			 */
			@Override
			public void onSuccess(SendResult sendResult) {
				System.out.println("等待异步消息的结果:" + sendResult);
			}

			/**
			 * 消息发送失败的回调
			 */
			@Override
			public void onException(Throwable arg0) {
				System.out.println("等待异步消息抛出的异常:" + arg0.getMessage());
			}
		});
	}

	/**
	 * 单向发送消息,只管发,不管结果
	 * 
	 * @param topic 主题
	 * @param msg 需要发送的消息
	 */
	public void sendOneWay(String topic, String msg) {
		rocketMQTemplate.sendOneWay(topic, msg);
	}

	/**
	 * 同步发送对象,将对象转成json串发出,消费者同样以字符串的方式接受,之后再转换
	 * 
	 * @param topic 主题
	 * @param msg 需要发送的消息
	 */
	public void convertAndSend(String topic, Object msg) {
		rocketMQTemplate.convertAndSend(topic, msg);
	}

	/**
	 * 同步延迟消息
	 * 
	 * @param topic 主题
	 * @param msg 需要发送的消息
	 */
	public void syncSendDelay(String topic, Object msg) {
		// spring的Message对象
		Message<Object> message = MessageBuilder.withPayload(msg)
				// 设置消息的tag
				.setHeader(MessageConst.PROPERTY_TAGS, "A").build();
		// 发消息的超时时间,默认单位毫秒
		rocketMQTemplate.syncSend(topic, message, 3);
		// 发消息的超时时间,队列中消息延迟时间等级,从1s/5s/10s/30s/1m/2m/3m/4m...10m/20m/30m/1h/2h,总共18等级
		// 5等级表示1m钟之后发送给消费者,同步发送延迟消息
		rocketMQTemplate.syncSend(topic, message, 3, 5);
		// 异步发送延迟消息,Message是rocket的Message对象
		org.apache.rocketmq.common.message.Message message2 = new org.apache.rocketmq.common.message.Message();
		// 设置延迟等级
		message2.setDelayTimeLevel(5);
		try {
			// 异步发送延迟消息
			rocketMQTemplate.getProducer().send(message2, new SendCallback() {

				@Override
				public void onSuccess(SendResult arg0) {

				}

				@Override
				public void onException(Throwable arg0) {

				}
			});
		} catch (MQClientException | RemotingException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 同步顺序消息
	 */
	public void testSyncSendOrderly() {
		// 第三个参数用于队列的选择
		rocketMQTemplate.syncSendOrderly("test-topic-1", "这是一条同步顺序消息", "queue");
		// 异步顺序消息
		rocketMQTemplate.asyncSendOrderly("test-topic-1", "这是一条异步顺序消息", "queue", new SendCallback() {

			@Override
			public void onSuccess(SendResult arg0) {

			}

			@Override
			public void onException(Throwable arg0) {

			}
		});
	}
}