package com.wy.filter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;

import reactor.core.publisher.Mono;

/**
 * Sentinel+Gateway限流
 * 
 * 基于Sentinel 的Gateway限流是通过其提供的Filter来完成的,使用时只需注入对应的SentinelGatewayFilter实例以及
 * SentinelGatewayBlockExceptionHandler实例即可
 * 
 * @author 飞花梦影
 * @date 2022-02-26 23:10:14
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
public class MySentinelGateway {

	private final List<ViewResolver> viewResolvers;

	private final ServerCodecConfigurer serverCodecConfigurer;

	public MySentinelGateway(ObjectProvider<List<ViewResolver>> viewResolversProvider,
			ServerCodecConfigurer serverCodecConfigurer) {
		this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
		this.serverCodecConfigurer = serverCodecConfigurer;
	}

	// 初始化一个限流的过滤器
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public GlobalFilter sentinelGatewayFilter() {
		return new SentinelGatewayFilter();
	}

	// 配置初始化的限流参数
	@PostConstruct
	public void initGatewayRules() {
		Set<GatewayFlowRule> rules = new HashSet<>();
		rules.add(
				// 资源名称,对应路由id
				new GatewayFlowRule("product_route")
						// 限流阈值
						.setCount(1)
						// 统计时间窗口,单位是秒,默认是 1 秒
						.setIntervalSec(1));
		GatewayRuleManager.loadRules(rules);
	}

	// 配置限流的异常处理器
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
		return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
	}

	// 自定义限流异常页面
	@PostConstruct
	public void initBlockHandlers() {
		BlockRequestHandler blockRequestHandler = new BlockRequestHandler() {

			public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
				Map<String, Object> map = new HashMap<>();
				map.put("code", 0);
				map.put("message", "接口被限流了");
				return ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(map));
			}
		};
		GatewayCallbackManager.setBlockHandler(blockRequestHandler);
	}
}