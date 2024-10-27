package com.wy.sentinel;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;

import lombok.extern.slf4j.Slf4j;

/**
 * 定义限流和降级后的处理方法2:将限流和降级方法外置到单独的类中
 * 
 * @author 飞花梦影
 * @date 2022-02-26 17:19:43
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class MySentinel02 {

	int i = 0;

	@SentinelResource(value = "message", blockHandlerClass = OrderServiceImpl3BlockHandlerClass.class,
			blockHandler = "blockHandler", fallbackClass = OrderServiceImpl3FallbackClass.class, fallback = "fallback")
	public String message() {
		i++;
		if (i % 3 == 0) {
			throw new RuntimeException();
		}
		return "message4";
	}
}

@Slf4j
class OrderServiceImpl3BlockHandlerClass {

	// 这里必须使用static修饰方法
	public static String blockHandler(BlockException ex) {
		log.error("{}", ex);
		return "接口被限流或者降级了...";
	}
}

@Slf4j
class OrderServiceImpl3FallbackClass {

	// 这里必须使用static修饰方法
	public static String fallback(Throwable throwable) {
		log.error("{}", throwable);
		return "接口发生异常了...";
	}
}