package com.wy.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 缓存POST请求流中的数据,让数据可以复用
 * 
 * @author 飞花梦影
 * @date 2023-01-10 10:35:48
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class CacheRequestBodyFilter implements GlobalFilter, Ordered {

	@Override
	public int getOrder() {
		// 后续的拦截器优先级数据要比当前大,否则拿到的不是缓存数据
		return HIGHEST_PRECEDENCE + 1;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		boolean filter = exchange.getRequest().getURI().getPath().contains("需要缓存的url");

		if (null == exchange.getRequest().getHeaders().getContentType() || !filter) {
			return chain.filter(exchange);
		}

		return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
			// 确保数据缓冲区不被释放,必须要retain
			DataBufferUtils.retain(dataBuffer);
			// defer,just:创建数据源,得到当前数据的副本
			Flux<DataBuffer> cachedFlux =
					Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
			// 重新包装ServerHttpRequest,重写getBoby(),能够返回请求数据
			ServerHttpRequestDecorator serverHttpRequestDecorator =
					new ServerHttpRequestDecorator(exchange.getRequest()) {

						@Override
						public Flux<DataBuffer> getBody() {
							return cachedFlux;
						}
					};
			// 将包装后的ServerHttpRequest向下继续传递
			return chain.filter(exchange.mutate().request(serverHttpRequestDecorator).build());
		});

	}
}