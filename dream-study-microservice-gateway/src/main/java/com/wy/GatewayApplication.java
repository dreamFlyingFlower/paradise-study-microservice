package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayClassPathWarningAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerClientAutoConfiguration;
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.handler.AbstractHandlerMapping;

/**
 * springcloud gateway网关,api管理,spring用来替代zuul,需要spring5.0,boot2.0以上
 * http://spring.io/guides/gs/gateway/
 * 
 * {@link GatewayAutoConfiguration}:Gateway自动配置入口,加载了Gateway需要注入的类
 * 	{@link GatewayClassPathWarningAutoConfiguration}:检查是否配置webfux依赖
 * {@link GatewayLoadBalancerClientAutoConfiguration}:网关需要使用的负载均衡
 * {@link DispatcherHandler#handle}:前端请求处理转发
 * {@link AbstractHandlerMapping#getHandler}:根据Web请求获得转发处理
 * {@link RoutePredicateHandlerMapping#getHandlerInternal}:处理转发请求
 * {@link RoutePredicateHandlerMapping#lookupRoute}:主要的路由转发类
 * 
 * @author 飞花梦影
 * @date 2020-12-08 10:52:37
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
@EnableHystrix
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
}