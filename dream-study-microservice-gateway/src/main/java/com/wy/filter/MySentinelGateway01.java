package com.wy.filter;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;

/**
 * Sentinel+Gateway限流:自定义API分组,更细粒度的限流规则定义
 * 
 * 基于Sentinel 的Gateway限流是通过其提供的Filter来完成的,使用时只需注入对应的SentinelGatewayFilter实例以及
 * SentinelGatewayBlockExceptionHandler实例即可
 * 
 * @author 飞花梦影
 * @date 2022-02-26 23:10:14
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
public class MySentinelGateway01 {

	/**
	 * 配置初始化的限流参数
	 */
	@PostConstruct
	public void initGatewayRules() {
		Set<GatewayFlowRule> rules = new HashSet<>();
		rules.add(new GatewayFlowRule("product_api1").setCount(1).setIntervalSec(1));
		rules.add(new GatewayFlowRule("product_api2").setCount(1).setIntervalSec(1));
		GatewayRuleManager.loadRules(rules);
	}

	/**
	 * 自定义API分组
	 */
	@PostConstruct
	private void initCustomizedApis() {
		Set<ApiDefinition> definitions = new HashSet<>();
		ApiDefinition api1 = new ApiDefinition("product_api1").setPredicateItems(new HashSet<ApiPredicateItem>() {

			private static final long serialVersionUID = 1L;

			{
				// 以/product-api/product/api1 开头的请求
				add(new ApiPathPredicateItem().setPattern("/product-api/product/api1/**")
						.setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
			}
		});
		ApiDefinition api2 = new ApiDefinition("product_api2").setPredicateItems(new HashSet<ApiPredicateItem>() {

			private static final long serialVersionUID = 1L;

			{
				// 以/product-api/product/api2/demo1 完成的url路径匹配
				add(new ApiPathPredicateItem().setPattern("/product-api/product/api2/demo1"));
			}
		});
		definitions.add(api1);
		definitions.add(api2);
		GatewayApiDefinitionManager.loadApiDefinitions(definitions);
	}
}