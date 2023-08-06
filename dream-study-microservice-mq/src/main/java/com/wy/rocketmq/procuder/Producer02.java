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
		// 客户端本机IP地址.某些机器会发生无法识别客户端IP地址情况,需要应用在代码中强制指定
		producer.setClientIP("127.0.0.1");
		// 客户端实例名称.客户端创建的多个 Producer、Consumer实际是共用一个内部实例,这个实例包含网络连接、线程资源等
		producer.setInstanceName("instanceName");
		// 通信层异步回调线程数
		producer.setClientCallbackExecutorThreads(4);
		// 轮询Name Server间隔时间,单位毫秒
		producer.setPollNameServerInterval(30000);
		// 向Broker发送心跳间隔时间,单位毫秒
		producer.setHeartbeatBrokerInterval(30000);
		// 持久化Consumer消费进度间隔时间,单位毫秒
		producer.setPersistConsumerOffsetInterval(5000);

		// 设置消息发送失败重试次数
		producer.setRetryTimesWhenSendFailed(3);
		producer.setRetryTimesWhenSendAsyncFailed(3);
		// 在发送消息时,自动创建服务器不存在的topic,需要指定Key,该Key可用于配置发送消息所在topic的默认路由
		producer.setCreateTopicKey("topic");
		// 指定新创建的Topic的Queue数量为4,默认为4
		producer.setDefaultTopicQueueNums(4);
		// 设置发送超时时限为5s,默认3s
		producer.setSendMsgTimeout(5000);
		// 消息Body超过多大开始压缩(Consumer收到消息会自动解压缩),单位字节
		producer.setCompressMsgBodyOverHowmuch(4096);
		// 如果发送消息返回sendResult,但是sendStatus!=SEND_OK,是否重试发送
		producer.setRetryAnotherBrokerWhenNotStoreOK(false);
		// 客户端限制的消息大小,超过报错,同时服务端也会限制,所以需要跟服务端配合使用.默认4M,单位字节
		// producer.setMaxMessageSize(4096);
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