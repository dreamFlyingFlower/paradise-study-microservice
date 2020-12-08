package com.wy.zuul;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;

/**
 * @apiNote zuul的限流功能,应该放在请求被转发之前
 * @author ParadiseWY
 * @date 2019年9月16日
 */
public class ZuulsLimit extends ZuulFilter {

	/**
	 * gvaua写的令牌桶限流,将请求放入一个容器中,限制容器的容量的,当达到容量上限时,不再接受请求
	 */
	private static final RateLimiter RATE_LIMITER = RateLimiter.create(100);

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		if (!RATE_LIMITER.tryAcquire()) {
			// 没有拿到令牌
			throw new RuntimeException("没有拿到令牌,被限流了");
		}
		return null;
	}

	/**
	 * 限流类型应当是前置类型
	 * 
	 * @return
	 */
	@Override
	public String filterType() {
		return FilterConstants.PRE_TYPE;
	}

	/**
	 * 优先级应该是zuul中最高的
	 * 
	 * @return
	 */
	@Override
	public int filterOrder() {
		return FilterConstants.SERVLET_DETECTION_FILTER_ORDER - 1;
	}
}