package com.wy.rocketmq.procuder;

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
public class Procuder02Listener implements RocketMQLocalTransactionListener {

	// 执行本地事物
	@Override
	public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
		try {
			// 本地事物
			// dosomething save or update or delete
			return RocketMQLocalTransactionState.COMMIT;
		} catch (Exception e) {
			return RocketMQLocalTransactionState.ROLLBACK;
		}
	}

	// 消息回查
	@Override
	public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
		// 查询日志记录,从数据库查询
		TxLog txLog = new TxLog();
		if (txLog == null) {
			return RocketMQLocalTransactionState.COMMIT;
		} else {
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