package com.wy.filter;

import java.util.Arrays;
import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.AddRequestHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.AddRequestParameterGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.AddResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.DedupeResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.FallbackHeadersGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.PrefixPathGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.PreserveHostHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.RedirectToGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.RemoveRequestHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.RemoveResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.RequestSizeGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.RewritePathGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.RewriteResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SaveSessionGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SecureHeadersGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SetPathGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SetResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SetStatusGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.StripPrefixGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.headers.RemoveHopByHopHeadersFilter;
import org.springframework.web.server.ServerWebExchange;

import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

/**
 * {@link GatewayFilter}:局部过滤器,应用到单个路由或者一个分组的路由上
 * 
 * 自定义局部过滤器,类名结尾必须是GatewayFilterFactory
 * 
 * Gateway内置局部过滤器,在配置中的key只需要写剔除GatewayFilterFactory的值即可,如-AddRequestHeader=key,value:
 * 
 * <pre>
 * {@link AddRequestHeaderGatewayFilterFactory}:为原始请求添加Header,参数为Header的名称及值
 * {@link AddRequestParameterGatewayFilterFactory}:为原始请求添加请求参数,参数为参数名称及值
 * {@link AddResponseHeaderGatewayFilterFactory}:为原始响应添加Header,参数为Header的名称及值
 * {@link DedupeResponseHeaderGatewayFilterFactory}:剔除响应头中重复的值,需要去重的Header名称及去重策略
 * {@link HystrixGatewayFilterFactory}:为路由引入Hystrix的断路器保护,参数为 HystrixCommand 的名称,2.4以下版本有效
 * {@link FallbackHeadersGatewayFilterFactory}:为fallbackUri的请求头中添加具体的异常信息,参数为Header的名称
 * {@link PrefixPathGatewayFilterFactory}:为原始请求路径添加前缀,参数为前缀路径
 * {@link PreserveHostHeaderGatewayFilterFactory}:为请求添加一个preserveHostHeader=true的属性,
 * 		路由过滤器会检查该属性以决定是否要发送原始的Host,无参数
 * {@link RequestRateLimiterGatewayFilterFactory}:用于对请求限流,限流算法为令牌桶.
 * 		参数为keyResolver,rateLimiter,statusCode,denyEmptyKey,emptyKeyStatus
 * {@link RedirectToGatewayFilterFactory}:将原始请求重定向到指定的URL,参数为http状态码及重定向的url
 * {@link RemoveHopByHopHeadersFilter}:为原始请求删除IETF组织规定的一系列Header,默认启用,可通过配置删除
 * {@link RemoveRequestHeaderGatewayFilterFactory}:为原始请求删除某个Header,参数为Header名称
 * {@link RemoveResponseHeaderGatewayFilterFactory}:为原始响应删除某个Header,参数为Header名称
 * {@link RewritePathGatewayFilterFactory}:重写原始的请求路径.参数为原始路径正则表达式以及重写后路径的正则表达式
 * {@link RewriteResponseHeaderGatewayFilterFactory}:重写原始响应中的某个Header.参数为Header名称,值的正则,重写后的值
 * {@link SaveSessionGatewayFilterFactory}:在转发请求之前,强制执行WebSession::save操作.无参数
 * {@link SecureHeadersGatewayFilterFactory}:为原始响应添加一系列起安全作用的响应头.无参数,支持修改这些安全响应头的值
 * {@link SetPathGatewayFilterFactory}:修改原始的请求路径.参数为修改后的路径
 * {@link SetResponseHeaderGatewayFilterFactory}:修改原始响应中某个Header的值.参数为Header名称,修改后的值
 * {@link SetStatusGatewayFilterFactory}:修改原始响应的状态码.参数为HTTP 状态码,可以是数字,也可以是字符串
 * {@link StripPrefixGatewayFilterFactory}:用于截断原始请求的路径.参数为使用数字表示要截断的路径的数量
 * {@link RetryGatewayFilterFactory}:针对不同的响应进行重试.参数为retries、statuses、methods、series
 * {@link RequestSizeGatewayFilterFactory}:设置允许接收最大请求包的大小,如果请求包大小超过设置的值,则返回413.参数为请求包大小,单位字节,默认值5M
 * {@link ModifyRequestBodyGatewayFilterFactory}:在转发请求之前修改原始请求体内容,参数为修改后的请求体内容
 * {@link ModifyResponseBodyGatewayFilterFactory}:修改原始响应体的内容.参数为修改后的响应体内容
 * </pre>
 * 
 * 有三种过滤器,执行顺序如下:默认过滤器default-filters->只对具体某个路由生效的局部过滤器filters->使用java代码编写的全局过滤器GlobalFilter
 *
 * @author 飞花梦影
 * @date 2021-12-27 16:20:54
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class MyGatewayFilterFactory extends AbstractGatewayFilterFactory<MyGatewayFilterFactory.Config> {

	private static final String BEGIN_TIME = "beginTime";

	public MyGatewayFilterFactory() {
		super(MyGatewayFilterFactory.Config.class);
	}

	// 读取配置文件中的参数赋值到配置类中
	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList("show");
	}

	@Override
	public GatewayFilter apply(Config config) {
		return new GatewayFilter() {

			@Override
			public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
				if (!config.show) {
					// 如果配置类中的show为false,表示放行
					return chain.filter(exchange);
				}
				exchange.getAttributes().put(BEGIN_TIME, System.currentTimeMillis());
				// pre的逻辑 chain.filter().then(Mono.fromRunable(()->{ post的逻辑 }))
				return chain.filter(exchange).then(Mono.fromRunnable(() -> {
					Long startTime = exchange.getAttribute(BEGIN_TIME);
					if (startTime != null) {
						System.out.println(exchange.getRequest().getURI() + "请求耗时: "
								+ (System.currentTimeMillis() - startTime) + "ms");
					}
				}));
			}
		};
	}

	@Setter
	@Getter
	static class Config {

		private boolean show;
	}
}