package com.wy.sentinel;

import com.alibaba.csp.sentinel.annotation.SentinelResource;

import lombok.extern.slf4j.Slf4j;

/**
 * 定义限流和降级后的处理方法1:直接将限流和降级方法定义在方法中
 * 
 * @author 飞花梦影
 * @date 2022-02-26 17:19:43
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
public class MySentinel01 {

	int i = 0;

	/**
	 * value:自定义的资源名称;blockHandler:指定发生降级时进入的方法;fallback:指定发生异常时进入的方法
	 * 
	 * @return
	 */
	@SentinelResource(value = "message", blockHandler = "blockHandler", fallback = "fallback")
	public String message() {
		i++;
		if (i % 3 == 0) {
			throw new RuntimeException();
		}
		return "message";
	}

	/**
	 * 降级方法的参数可以和原方法相同
	 * 
	 * @return
	 */
	public String blockHandler() {
		return "接口被限流或者降级了...";
	}

	public String fallback(Throwable throwable) {
		log.error("{}", throwable);
		return "接口发生异常了...";
	}
}