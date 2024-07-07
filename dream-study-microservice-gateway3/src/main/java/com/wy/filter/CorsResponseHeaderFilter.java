package com.wy.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.DedupeResponseHeaderGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * 解决跨域请求头中VARY和Access-Control-Allow-Origin重复的问题,效果等同于配置中的{@link DedupeResponseHeaderGatewayFilterFactory}
 * 
 * {@link DedupeResponseHeaderGatewayFilterFactory.Strategy#RETAIN_UNIQUE}:如果请求中设置的Origin的值与自己设置的是同一个,
 * 例如生产环境设置的都是自己的域名xxx.com或者开发测试环境设置的都是*,那么可以选用RETAIN_UNIQUE策略,去重后返回到前端
 * {@link DedupeResponseHeaderGatewayFilterFactory.Strategy#RETAIN_FIRST}:如果请求中设置的Oringin的值与自己设置的不是同一个,
 * RETAIN_UNIQUE策略就无法生效,比如*和xxx.com是两个不一样的Origin,最终还是会返回两个Access-Control-Allow-Origin的头.
 * 从{@link DefaultCorsProcessor#checkOrigin()}可以看出,response的header里先加入的是自己配置的Access-Control-Allow-Origin的值,
 * 所以,通用跨域可以将策略设置为RETAIN_FIRST,只保留自己设置的*
 *
 * @author 飞花梦影
 * @date 2023-01-10 10:35:48
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class CorsResponseHeaderFilter implements GlobalFilter, Ordered {

	@Override
	public int getOrder() {
		// 指定此过滤器位于NettyWriteResponseFilter之后,即待处理完响应体后接着处理响应头
		return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER + 1;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			exchange.getResponse().getHeaders().entrySet().stream()
					.filter(kv -> (kv.getValue() != null && kv.getValue().size() > 1))
					.filter(kv -> (kv.getKey().equals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)
							|| kv.getKey().equals(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)
							|| kv.getKey().equals(HttpHeaders.VARY)))
					.forEach(kv -> {
						// Vary只需要去重即可
						if (kv.getKey().equals(HttpHeaders.VARY)) {
							kv.setValue(kv.getValue().stream().distinct().collect(Collectors.toList()));
						} else {
							List<String> value = new ArrayList<>();
							if (kv.getValue().contains(CorsConfiguration.ALL)) {
								// 如果包含*,则取*
								value.add(CorsConfiguration.ALL);
								kv.setValue(value);
							} else {
								// 否则默认取第一个
								value.add(kv.getValue().get(0));
								kv.setValue(value);
							}
						}
					});
		}));
	}
}