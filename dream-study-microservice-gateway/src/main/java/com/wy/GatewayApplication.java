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
 * SpringCloud Gateway网关,API管理,官网 http://spring.io/guides/gs/gateway/
 * 
 * {@link GatewayAutoConfiguration}:Gateway自动配置入口,加载了Gateway需要注入的类
 * 	{@link GatewayClassPathWarningAutoConfiguration}:检查是否配置webfux依赖
 * {@link GatewayLoadBalancerClientAutoConfiguration}:网关需要使用的负载均衡
 * {@link DispatcherHandler#handle}:前端请求处理转发
 * {@link AbstractHandlerMapping#getHandler}:根据Web请求获得转发处理
 * {@link RoutePredicateHandlerMapping#getHandlerInternal}:处理转发请求
 * {@link RoutePredicateHandlerMapping#lookupRoute}:主要的路由转发类
 * 
 * 执行流程:
 * <pre>
 * 1.Gateway Client向Gateway Server发送请求
 * 2.请求首先会被HttpWebHandlerAdapter进行提取组装成网关上下文
 * 3.然后网关的上下文会传递到DispatcherHandler,它负责将请求分发给RoutePredicateHandlerMapping
 * 4.RoutePredicateHandlerMapping负责路由查找,并根据路由断言判断路由是否可用
 * 5.如果过断言成功,由FilteringWebHandler创建过滤器链并调用
 * 6. 请求会一次经过PreFilter--微服务--PostFilter的方法,终返回响应
 * 
 * Gateway Client->HttpWebHandlerAdapter(组装网关上下文)->DispatcherHandler(循环遍历Mapping,获取Handler)
 * ->RoutePredicateHandlerMapping(匹配路由信息,通过路由断言,判断路由是否可用)
 * ->lookupRoute->断言失败->DispatcherHandler(创建过滤器链,调用过滤器)->继续断言
 * ->lookupRoute->断言成功->FilteringWebHandler(创建过滤器链,调用过滤器)->执行Filter
 * </pre>
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