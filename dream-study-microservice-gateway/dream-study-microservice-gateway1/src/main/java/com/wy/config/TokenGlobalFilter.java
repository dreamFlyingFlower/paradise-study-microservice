package com.wy.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * 自定义全局Token检查路由器
 * 
 * @author 飞花梦影
 * @date 2022-02-26 21:26:09
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
public class TokenGlobalFilter implements GlobalFilter, Ordered {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String token = exchange.getRequest().getQueryParams().getFirst("token");
		if (StringUtils.isBlank(token)) {
			System.out.println("鉴权失败");
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}
		// 调用chain.filter继续向下游执行
		return chain.filter(exchange);
	}

	/**
	 * 执行拦截顺序,值越小,优先级越高
	 */
	@Override
	public int getOrder() {
		return 0;
	}
}