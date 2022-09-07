package com.wy.filter;

import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * {@link RequestRateLimiterGatewayFilterFactory}的keyResolver对象
 *
 * @author 飞花梦影
 * @date 2022-06-22 23:49:43
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Component
public class SelfKeyResolver {

	/**
	 * 基于请求路径的限流规则
	 */
	@Bean
	@Primary
	public KeyResolver pathKeyResolver() {
		return new KeyResolver() {

			@Override
			public Mono<String> resolve(ServerWebExchange exchange) {
				return Mono.just(exchange.getRequest().getPath().toString());
			}
		};
	}

	/**
	 * 基于请求参数的限流
	 */
	@Bean
	public KeyResolver paramKeyResolver() {
		return new KeyResolver() {

			@Override
			public Mono<String> resolve(ServerWebExchange exchange) {
				return Mono.just(exchange.getRequest().getQueryParams().getFirst("token"));
			}
		};
	}
}