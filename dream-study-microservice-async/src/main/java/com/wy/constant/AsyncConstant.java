package com.wy.constant;

/**
 * 常量
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:56:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface AsyncConstant {

	/**
	 * 执行代理方法防止死循环
	 */
	ThreadLocal<Boolean> PUBLISH_EVENT = ThreadLocal.withInitial(() -> false);

	/**
	 * 队列后缀
	 */
	String QUEUE_SUFFIX = "_async_queue";
}