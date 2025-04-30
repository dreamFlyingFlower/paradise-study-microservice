package com.wy.filter;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 * 配合{@link HystrixRequestFilter}使用缓存,编码模式使用Hystrix的缓存
 *
 * @author 飞花梦影
 * @date 2025-04-30 12:57:43
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class CacheHystrixCommand extends HystrixCommand<Object> {

	protected CacheHystrixCommand(HystrixCommandGroupKey group) {
		super(group);
	}

	@Override
	protected Object run() throws Exception {
		return null;
	}

	@Override
	protected String getCacheKey() {
		// 缓存需要重写的主要方法
		return super.getCacheKey();
	}
}