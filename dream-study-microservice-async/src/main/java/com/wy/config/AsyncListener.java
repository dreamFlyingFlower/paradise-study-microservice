package com.wy.config;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.Assert;

import com.wy.handler.HandlerService;
import com.wy.handler.context.AsyncContext;
import com.wy.strategy.StrategyFactory;

/**
 * 事件监听
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:54:29
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
public class AsyncListener {

	/**
	 * 处理事件
	 * 
	 * @param fallbackExecution true:没有事务正在运行,依然处理事件
	 * @param phase TransactionPhase.AFTER_COMPLETION 事务提交,事务回滚都处理事件
	 * @param context
	 */
	@TransactionalEventListener(fallbackExecution = true, phase = TransactionPhase.AFTER_COMPLETION)
	public void asyncHandler(AsyncContext asyncContext) {
		HandlerService handlerService =
				StrategyFactory.doStrategy(asyncContext.getAsyncExecute().type().name(), HandlerService.class);
		Assert.notNull(handlerService, "异步执行策略不存在");
		handlerService.handle(asyncContext);
	}
}