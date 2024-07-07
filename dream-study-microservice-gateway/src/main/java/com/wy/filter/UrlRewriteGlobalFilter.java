package com.wy.filter;

import java.net.URI;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * URL重写全局拦截器
 *
 * @author 飞花梦影
 * @date 2024-07-06 19:42:26
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class UrlRewriteGlobalFilter implements GlobalFilter, Ordered {

	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// 对uri做处理,然后重新转发
		URI uri = exchange.getRequest().getURI();
		// 这个方法直接修改的是exchange里面的request
		exchange = exchange.mutate().request(builder -> {
			// 直接转发处理后的uri
			builder.uri(uri);
		}).build();

		return chain.filter(exchange);
	}
}