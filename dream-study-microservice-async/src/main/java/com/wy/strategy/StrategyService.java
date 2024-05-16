package com.wy.strategy;

import java.util.List;

import com.wy.strategy.context.StrategyContext;

/**
 * 策略基础接口类 每个策略业务接口类必须继承此类
 *
 * @author 飞花梦影
 * @date 2024-05-16 14:02:59
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface StrategyService<T extends StrategyContext> {

	/**
	 * 策略类型列表
	 * 
	 * @return
	 */
	List<String> listType();

	/**
	 * 处理策略
	 *
	 * @param t
	 * @return
	 */
	boolean handle(T t);
}