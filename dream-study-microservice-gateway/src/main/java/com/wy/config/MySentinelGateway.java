package com.wy.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.wy.result.Result;

import reactor.core.publisher.Mono;

/**
 * Gateway整合Sentinel限流
 *
 * @author 飞花梦影
 * @date 2021-12-27 17:14:55
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
public class MySentinelGateway {

	private final List<ViewResolver> viewResolvers;

	private final ServerCodecConfigurer serverCodecConfigurer;

	public MySentinelGateway(ObjectProvider<List<ViewResolver>> viewResolversProvider,
			ServerCodecConfigurer serverCodecConfigurer) {
		this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
		this.serverCodecConfigurer = serverCodecConfigurer;
	}

	/**
	 * 配置限流的异常处理器
	 * 
	 * @return
	 */
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
		// Register the block exception handler for Spring Cloud Gateway.
		return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
	}

	/**
	 * 初始化一个限流的过滤器
	 * 
	 * @return
	 */
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public GlobalFilter sentinelGatewayFilter() {
		return new SentinelGatewayFilter();
	}

	/**
	 * 自定义API分组,一种更细粒度的限流规则定义,可以实现某个方法的细粒度限流
	 */
	@PostConstruct
	private void initCustomizedApis() {
		Set<ApiDefinition> definitions = new HashSet<>();
		ApiDefinition api1 = new ApiDefinition("order_api").setPredicateItems(new HashSet<ApiPredicateItem>() {

			private static final long serialVersionUID = -6640367415566843441L;

			{
				add(new ApiPathPredicateItem().setPattern("/order-serv/api/**")
						.setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
			}
		});
		definitions.add(api1);
		GatewayApiDefinitionManager.loadApiDefinitions(definitions);
	}

	/**
	 * 添加多个不同规则的限流
	 */
	@PostConstruct
	private void initGatewayRules() {
		Set<GatewayFlowRule> rules = new HashSet<>();
		rules.add(new GatewayFlowRule("product_route")
				// 表示一秒钟1超过了3次就会限流
				.setCount(3).setIntervalSec(1));
		rules.add(new GatewayFlowRule("order_api").setCount(1).setIntervalSec(1));
		GatewayRuleManager.loadRules(rules);
	}

	/**
	 * 自定义在限流的时候返回的错误
	 */
	@PostConstruct
	public void initBlockHandlers() {
		BlockRequestHandler blockRequestHandler = new BlockRequestHandler() {

			public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
				return ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(Result.error("接口被限流了")));
			}
		};
		GatewayCallbackManager.setBlockHandler(blockRequestHandler);
	}
}