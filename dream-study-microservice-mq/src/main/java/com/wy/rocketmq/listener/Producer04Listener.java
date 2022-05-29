package com.wy.rocketmq.listener;

import java.util.HashMap;
import java.util.Map;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * RocketMQ原生半消息事务监听
 *
 * @author 飞花梦影
 * @date 2022-05-29 11:54:44
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class Producer04Listener implements TransactionListener {

	// 模拟记录消息事务状态,应该放在数据库或redis
	private static Map<String, LocalTransactionState> STATE_MAP = new HashMap<>();

	/**
	 * 执行具体的业务逻辑
	 *
	 * @param msg 发送的消息对象
	 * @param arg 其他参数
	 * @return 事务状态
	 */
	@Override
	public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
		try {
			System.out.println("用户A账户减500元.");
			Thread.sleep(500); // 模拟调用服务
			// 模拟异常
			// System.out.println(1/0);
			System.out.println("用户B账户加500元.");
			Thread.sleep(800);
			STATE_MAP.put(msg.getTransactionId(), LocalTransactionState.COMMIT_MESSAGE);
			// 二次提交确认
			return LocalTransactionState.COMMIT_MESSAGE;
		} catch (Exception e) {
			e.printStackTrace();
		}

		STATE_MAP.put(msg.getTransactionId(), LocalTransactionState.ROLLBACK_MESSAGE);
		// 回滚
		return LocalTransactionState.ROLLBACK_MESSAGE;
	}

	/**
	 * 消息回查
	 *
	 * @param msg
	 * @return
	 */
	@Override
	public LocalTransactionState checkLocalTransaction(MessageExt msg) {
		System.out.println("状态回查 ---> " + msg.getTransactionId() + " " + STATE_MAP.get(msg.getTransactionId()));
		return STATE_MAP.get(msg.getTransactionId());
	}
}