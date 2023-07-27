package com.wy.rocketmq.order;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 顺序消息生产者,模拟订单业务:创建,付款,推送,完成
 *
 * @author 飞花梦影
 * @date 2022-06-26 10:19:40
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Slf4j
public class RocketOrderProducer {

	public static void main(String[] args) throws Exception {
		DefaultMQProducer producer = new DefaultMQProducer("group_order");
		producer.setNamesrvAddr("192.168.1.150:9876");
		producer.start();
		String[] tags = new String[] { "TagA", "TagC", "TagD" };

		// 订单列表
		List<OrderStep> orderList = new RocketOrderProducer().buildOrders();

		String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		// 发送消息
		for (int i = 0; i < 10; i++) {
			String body = dateStr + " Hello RocketMQ " + orderList.get(i);
			Message msg = new Message("topic_roder", tags[i % tags.length], "KEY" + i, body.getBytes());

			// 发送消息:消息,消息队列选择器,选择队列的业务标识
			SendResult sendResult = producer.send(msg, new MessageQueueSelector() {

				/**
				 * 消息队列选择
				 * 
				 * @param mqs 所有队列集合
				 * @param msg 消息对象
				 * @param arg 业务标识参数,即当前send()的最后一个参数
				 * @return 消息队列
				 */
				@Override
				public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
					// 根据订单id选择发送queue,该值为orderList.get(i).getOrderId()
					Long id = (Long) arg;
					// 根据orderId进行取模,让消息一直发送到同一个队列,所以此处的队列个数不能变,否则将不再是顺序的
					long index = id % mqs.size();
					return mqs.get((int) index);
				}
			}, orderList.get(i).getOrderId());

			// 直接输出回调成功的信息
			System.out.println(String.format("SendResult status:%s, queueId:%d, body:%s", sendResult.getSendStatus(),
					sendResult.getMessageQueue().getQueueId(), body));

			// 手动处理回调
			producer.send(msg, new MessageQueueSelector() {

				/**
				 * 消息队列选择
				 * 
				 * @param mqs 所有队列集合
				 * @param msg 消息对象
				 * @param arg 业务标识参数,即当前send()的最后一个参数
				 * @return 消息队列
				 */
				@Override
				public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
					// 根据订单id选择发送queue,该值为orderList.get(i).getOrderId()
					Long id = (Long) arg;
					// 根据orderId进行取模,让消息一直发送到同一个队列,所以此处的队列个数不能变,否则将不再是顺序的
					long index = id % mqs.size();
					return mqs.get((int) index);
				}
			}, orderList.get(i).getOrderId(), new SendCallback() {

				@Override
				public void onSuccess(SendResult sendResult) {
					System.out.println(sendResult);
				}

				@Override
				public void onException(Throwable e) {
					log.error(e.getMessage());
				}
			});
		}

		producer.shutdown();
	}

	/**
	 * 订单对象
	 */
	@Getter
	@Setter
	@ToString
	private static class OrderStep {

		private long orderId;

		private String desc;
	}

	/**
	 * 生成模拟订单数据
	 */
	private List<OrderStep> buildOrders() {
		List<OrderStep> orderList = new ArrayList<OrderStep>();

		OrderStep orderDemo = new OrderStep();
		orderDemo.setOrderId(15103111039L);
		orderDemo.setDesc("创建");
		orderList.add(orderDemo);

		orderDemo = new OrderStep();
		orderDemo.setOrderId(15103111039L);
		orderDemo.setDesc("付款");
		orderList.add(orderDemo);

		orderDemo = new OrderStep();
		orderDemo.setOrderId(15103111039L);
		orderDemo.setDesc("推送");
		orderList.add(orderDemo);

		orderDemo = new OrderStep();
		orderDemo.setOrderId(15103111039L);
		orderDemo.setDesc("完成");
		orderList.add(orderDemo);

		orderDemo = new OrderStep();
		orderDemo.setOrderId(15103111065L);
		orderDemo.setDesc("创建");
		orderList.add(orderDemo);

		orderDemo = new OrderStep();
		orderDemo.setOrderId(15103111065L);
		orderDemo.setDesc("付款");
		orderList.add(orderDemo);

		orderDemo = new OrderStep();
		orderDemo.setOrderId(15103111065L);
		orderDemo.setDesc("完成");
		orderList.add(orderDemo);

		orderDemo = new OrderStep();
		orderDemo.setOrderId(15103117235L);
		orderDemo.setDesc("创建");
		orderList.add(orderDemo);

		orderDemo = new OrderStep();
		orderDemo.setOrderId(15103117235L);
		orderDemo.setDesc("付款");
		orderList.add(orderDemo);

		orderDemo = new OrderStep();
		orderDemo.setOrderId(15103117235L);
		orderDemo.setDesc("完成");
		orderList.add(orderDemo);

		return orderList;
	}
}