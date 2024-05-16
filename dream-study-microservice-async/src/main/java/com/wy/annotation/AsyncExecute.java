package com.wy.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wy.enums.AsyncType;

/**
 * 异步执行注解
 *
 * @author 飞花梦影
 * @date 2024-05-16 11:19:27
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AsyncExecute {

	/**
	 * 异步执行策略
	 * 
	 * @return AsyncType
	 */
	AsyncType type();

	/**
	 * 延迟处理时间
	 * 
	 * @return 延迟时间
	 */
	int delayTime() default 0;

	/**
	 * 业务描述
	 * 
	 * @return 备注
	 */
	String remark();
}