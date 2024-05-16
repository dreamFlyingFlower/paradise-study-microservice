package com.wy.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.wy.annotation.AsyncExecute;
import com.wy.constant.AsyncConstant;
import com.wy.handler.context.AsyncContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 异步执行切面
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:52:48
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix = "dream.async", value = "enabled", havingValue = "true")
public class AsyncAspect {

	@Autowired
	private ApplicationEventPublisher publisher;

	@Around("@annotation(asyncExecute)")
	public Object proceed(ProceedingJoinPoint joinPoint, AsyncExecute asyncExecute) throws Throwable {
		if (AsyncConstant.PUBLISH_EVENT.get()) {
			try {
				// 直接执行
				return joinPoint.proceed();
			} finally {
				AsyncConstant.PUBLISH_EVENT.remove();
			}
		} else {
			AsyncContext asyncContext = new AsyncContext();
			asyncContext.setJoinPoint(joinPoint);
			asyncContext.setAsyncExecute(asyncExecute);
			// 发布事件
			publisher.publishEvent(asyncContext);
			log.info("异步执行事件发布成功,策略类型:{},业务描述:{}", asyncExecute.type(), asyncExecute.remark());

			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Class<?> returnType = signature.getMethod().getReturnType();
			if (returnType != Void.TYPE && returnType.isPrimitive()) {
				// 8种基本类型需特殊处理（byte、short、char、int、long、float、double、boolean）
				return returnType == Boolean.TYPE ? Boolean.TRUE : 1;
			}
			return null;
		}
	}
}