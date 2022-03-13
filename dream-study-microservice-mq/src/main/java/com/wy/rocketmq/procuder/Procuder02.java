package com.wy.rocketmq.procuder;

import java.util.Date;
import java.util.UUID;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.Data;

/**
 * 事务消息
 * 
 * @author 飞花梦影
 * @date 2022-02-26 23:41:46
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
public class Procuder02 {

	@Autowired
	private RocketMQTemplate rocketMQTemplate;

	public void createOrderBefore(Order order) {
		String txId = UUID.randomUUID().toString();
		// 发送半事务消息
		rocketMQTemplate.sendMessageInTransaction("tx_producer_group",
				MessageBuilder.withPayload(order).setHeader("txId", txId).build(), order);
	}

	// 本地事务
	@Transactional
	public void createOrder(String txId, Order order) {
		// 本地事物代码,数据库修改
		// dosomething save or update or delete
		// 记录日志到数据库,回查使用
		// dosomething save log
	}

	@Data
	static class TxLog {

		private String txLogId;

		private String content;

		private Date date;
	}
}