package com.wy.config;

import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * 自定义限流解析
 * 
 * <pre>
 * {@link RequestRateLimiterGatewayFilterFactory}:限流拦截器
 * {@link RateLimiter}:SpringGateway自带的限流接口,该接口中的isAllowed会在 RequestRateLimiterGatewayFilterFactory 中调用
 * {@link RateLimiter#isAllowed()}:第一个参数表示请求路由的 ID,根据 routeId 可以获取限流相关的配置;
 * 第二个参数 id 表示要限流的对象的唯一标识,可以是用户名,IP或其他可以从 ServerWebExchange 中得到的信息
 * {@link RedisRateLimiter}:{@link RateLimiter}的唯一实现类,如果要自定义限流,需要重写keyresolver
 * </pre>
 *
 * @author 飞花梦影
 * @date 2022-06-22 23:49:43
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Configuration
public class SelfKeyResolver {

	/**
	 * 基于请求路径的限流规则
	 */
	@Bean
	@Primary
	KeyResolver pathKeyResolver() {
		return new KeyResolver() {

			@Override
			public Mono<String> resolve(ServerWebExchange exchange) {
				// 根据 IP 来限流
				// Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
				return Mono.just(exchange.getRequest().getPath().toString());
			}
		};
	}

	/**
	 * 基于请求参数的限流
	 */
	@Bean
	KeyResolver paramKeyResolver() {
		return new KeyResolver() {

			@Override
			public Mono<String> resolve(ServerWebExchange exchange) {
				return Mono.just(exchange.getRequest().getQueryParams().getFirst("token"));
			}
		};
	}
}