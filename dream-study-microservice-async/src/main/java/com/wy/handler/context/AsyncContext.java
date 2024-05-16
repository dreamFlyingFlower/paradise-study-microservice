package com.wy.handler.context;

import org.aspectj.lang.ProceedingJoinPoint;

import com.wy.annotation.AsyncExecute;
import com.wy.dto.AsyncRequestDTO;
import com.wy.strategy.context.StrategyContext;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AsyncContext
 *
 * @author 飞花梦影
 * @date 2024-05-16 14:00:10
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AsyncContext extends StrategyContext {

	private static final long serialVersionUID = 1L;

	/**
	 * 切面方法
	 */
	private ProceedingJoinPoint joinPoint;

	/**
	 * 异步执行策略
	 */
	private AsyncExecute asyncExecute;

	/**
	 * 异步执行数据
	 */
	private AsyncRequestDTO asyncRequestDto;
}