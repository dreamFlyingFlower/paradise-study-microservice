package com.wy.filter;

import org.springframework.cloud.gateway.filter.ForwardRoutingFilter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyRoutingFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter;
import org.springframework.cloud.gateway.filter.WebClientHttpRoutingFilter;
import org.springframework.cloud.gateway.filter.WebClientWriteResponseFilter;
import org.springframework.cloud.gateway.filter.WebsocketRoutingFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import dream.flying.flower.lang.StrHelper;
import reactor.core.publisher.Mono;

/**
 * {@link GlobalFilter}:全局拦截器,应用到所有的路由上,可以实现权限统一校验,安全验证等 {@link GatewayFilter}:应用到单个或一个分组的路由上
 * 
 * <pre>
 * {@link ReactiveLoadBalancerClientFilter}:通过负载均衡客户端根据路由URL解析转换成真实URL
 * {@link NettyRoutingFilter}:通过httpclient转发请求真实URL并将响应写入到当前的响应中
 * {@link NettyWriteResponseFilter}:同 NettyRoutingFilter
 * {@link WebsocketRoutingFilter}:负责处理WebSocket类型的请求响应信息
 * {@link ForwardRoutingFilter}:解析路径,并将路径转发
 * {@link RouteToRequestUrlFilter}:转换路由中的URL
 * {@link WebClientHttpRoutingFilter}:通过{@link WebClient}客户端转发请求真实URL,并将响应写入当前响应中
 * {@link WebClientWriteResponseFilter}:同 WebClientHttpRoutingFilter
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2021-12-27 16:15:11
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Component
public class MyGatewayFilter implements GlobalFilter {

	/**
	 * 简单的鉴权
	 * 
	 * @param exchange 请求和响应的上下文
	 */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String token = exchange.getRequest().getQueryParams().getFirst("token");
		if (StrHelper.isBlank(token)) {
			System.out.println("鉴权失败");
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			// return exchange.getResponse().setComplete();
			DataBuffer buffer = exchange.getResponse().bufferFactory().wrap("登录失败".getBytes());
			return exchange.getResponse().writeWith(Mono.just(buffer));
		}
		return chain.filter(exchange);
	}
}