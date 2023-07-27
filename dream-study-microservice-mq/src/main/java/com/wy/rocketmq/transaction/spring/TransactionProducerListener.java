package com.wy.rocketmq.transaction.spring;

import java.util.Date;
import java.util.UUID;

import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
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
@RocketMQTransactionListener
@ExtRocketMQTemplateConfiguration(group = "tx_producer_group")
@SuppressWarnings({ "unused", "rawtypes" })
public class TransactionProducerListener implements RocketMQLocalTransactionListener {

	/**
	 * 如果半消息发送成功,执行本地事物
	 * 
	 * @param msg
	 * @param arg
	 * @return
	 */
	@Override
	public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
		try {
			// 本地事物成功
			// dosomething save or update or delete
			return RocketMQLocalTransactionState.COMMIT;
		} catch (Exception e) {
			// 本地事务失败
			return RocketMQLocalTransactionState.ROLLBACK;
		}
	}

	/**
	 * 消息回查
	 * 
	 * @param msg 消息
	 * @return 回查状态
	 */
	@Override
	public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
		// TODO 消息回查
		// 查询日志记录,从数据库查询
		TxLog txLog = new TxLog();
		if (txLog == null) {
			// 成功
			return RocketMQLocalTransactionState.COMMIT;
		} else {
			// 失败
			return RocketMQLocalTransactionState.ROLLBACK;
		}
	}

	@Data
	static class TxLog {

		private String txLogId;

		private String content;

		private Date date;
	}
}