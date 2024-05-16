package com.wy.handler;

import com.wy.handler.context.AsyncContext;
import com.wy.strategy.StrategyService;

/**
 * 异步执行接口
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:59:54
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface HandlerService extends StrategyService<AsyncContext> {

	/**
	 * 执行异步策略
	 * 
	 * @param asyncContext
	 * @return
	 */
	boolean execute(AsyncContext asyncContext);
}