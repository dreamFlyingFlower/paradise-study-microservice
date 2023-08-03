package com.wy.rocketmq.procuder;

import java.util.Arrays;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * 使用原生MQ发送消息
 *
 * @author 飞花梦影
 * @date 2022-05-28 23:55:20
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class Producer02 {

	/**
	 * 发送同步消息
	 * 
	 * @param msg 需要发送的消息
	 * @throws InterruptedException
	 * @throws MQBrokerException
	 * @throws RemotingException
	 * @throws MQClientException
	 */
	public void sendMsg(String msg)
			throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
		DefaultMQProducer producer = new DefaultMQProducer("group");
		// 设置nameserver地址
		producer.setNamesrvAddr("192.168.1.150:9876");
		// 设置消息发送失败重试次数
		producer.setRetryTimesWhenSendFailed(3);
		producer.setRetryTimesWhenSendAsyncFailed(3);
		// 设置发送超时时限为5s,默认3s
		producer.setSendMsgTimeout(5000);
		// 指定新创建的Topic的Queue数量为4,默认为4
		producer.setDefaultTopicQueueNums(4);
		// 建立连接
		producer.start();
		Message message = new Message("topic", "tag", msg.getBytes());
		// 设置用户属性,用于消息过滤,消费者可使用 MessageSelector 进行值过滤
		// 开启过滤功能需要在broker的配置文件中添加 enablePropertyFilter=true
		message.putUserProperty("id", "10");
		message.putUserProperty("age", "22");
		// 发送同步消息
		SendResult sendResult = producer.send(message);
		// 批量发送消息,单次数量不能超过4M,超过4M要将消息分割分多次发.Topic要相同,不能是延迟消息
		producer.send(Arrays.asList(message));
		// 发送异步消息
		producer.send(message, new SendCallback() {

			@Override
			public void onSuccess(SendResult sendResult) {

			}

			@Override
			public void onException(Throwable e) {

			}
		});
		// 发送单向消息
		producer.sendOneway(message);
		// 指定发送超时时间
		producer.send(message, 3000);
		// 消息id
		System.out.println(sendResult.getMsgId());
		// 消息发送结果
		// SEND_OK:成功
		// FLUSH_DISK_TIMEOUT:刷盘超时.当Broker设置的刷盘策略为同步刷盘时才可能出现这种异常状态.异步刷盘不会出现
		// FLUSH_SLAVE_TIMEOUT:Slave同步超时.当Broker集群设置的Master-Slave的复制方式为同步复制时才可能出现这种异常状态.异步复制不会出现
		// SLAVE_NOT_AVAILABLE:没有可用的Slave.当Broker集群设置为Master-Slave的复制方式为同步复制时才可能出现这种异常状态.异步复制不会出现
		System.out.println(sendResult.getSendStatus());
		// 消息队列
		System.out.println(sendResult.getMessageQueue());
		// 消息offset值
		System.out.println(sendResult.getQueueOffset());
		// 异步若关闭太快会接收不到消息,生产环境不会关闭
		producer.shutdown();
	}
}