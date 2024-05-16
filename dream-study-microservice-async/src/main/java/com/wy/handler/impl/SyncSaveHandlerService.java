package com.wy.handler.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.wy.entity.AsyncRequestEntity;
import com.wy.enums.AsyncExecuteStatus;
import com.wy.enums.AsyncType;
import com.wy.handler.context.AsyncContext;

import dream.framework.web.helper.SpringContextHelpers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 先同步处理失败再保存数据库
 *
 * @author 飞花梦影
 * @date 2024-05-16 14:01:24
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Component
@AllArgsConstructor
public class SyncSaveHandlerService extends AbstractHandlerService {

	@Override
	public List<String> listType() {
		return Collections.singletonList(AsyncType.SYNC_SAVE.name());
	}

	@Override
	public boolean execute(AsyncContext context) {
		// 同步处理,由于不能影响主线程事务,但是异步方法上面又有事务所有需要开启新事物
		TransactionStatus status = null;
		PlatformTransactionManager transactionManager = null;
		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
			definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			transactionManager = SpringContextHelpers.getBean(PlatformTransactionManager.class);
			status = transactionManager.getTransaction(definition);
		}
		try {
			// 同步处理
			context.getJoinPoint().proceed();
			if (null != status) {
				transactionManager.commit(status);
			}
		} catch (Throwable e) {
			log.warn("先同步处理失败：{}", context.getAsyncRequestDto(), e);
			if (null != status) {
				transactionManager.rollback(status);
			}
			// 保存数据库
			AsyncRequestEntity asyncReq = this.saveAsyncReq(context.getAsyncRequestDto(), AsyncExecuteStatus.ERROR.getCode());
			if (null == asyncReq) {
				// 降级为仅异步消息处理
				asyncProducer.send(context.getAsyncRequestDto());
			}
		}
		return true;
	}
}