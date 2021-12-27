package com.wy.zuul;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * 自定义Zuul过滤器,需要继承{@link ZuulFilter}
 * 
 * 过滤器(filter):是zuul的核心组件,zuul大部分功能都是通过过滤器来实现的.zuul中定义了4种标准过滤器类型:
 * 
 * <pre>
 * PRE:在请求被路由之前调用.可利用这种过滤器实现身份验证,在集群中选择请求的微服务,记录调试信息等
 * ROUTING:将请求路由到微服务.用于构建发送给微服务的请求,并使用HttpClient或Ribbon请求微服务
 * POST:在路由到微服务以后执行.可用来为响应添加标准的HTTP Header,收集统计信息和指标,将响应从微服务发送给客户端等
 * ERROR:在其他阶段发生错误时执行该过滤器
 * </pre>
 *
 * @author 飞花梦影
 * @date 2021-09-23 15:06:38
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Component
public class MyZuulFilter extends ZuulFilter {

	/**
	 * gvaua令牌桶限流,将请求放入一个容器中,限制容器的容量的,当达到容量上限时,不再接受请求
	 */
	private static final RateLimiter RATE_LIMITER = RateLimiter.create(100);

	/**
	 * 返回过滤器的类型
	 */
	@Override
	public String filterType() {
		return FilterConstants.ROUTE_TYPE;
	}

	/**
	 * 返回一个int值来指定过滤器的执行顺序,不同的过滤器允许返回相同的数字,数字越小的越先执行
	 */
	@Override
	public int filterOrder() {
		return FilterConstants.PRE_DECORATION_FILTER_ORDER;
	}

	/**
	 * 返回一个 boolean值来判断该过滤器是否要执行,true表示执行,false表示不执行
	 */
	@Override
	public boolean shouldFilter() {
		return true;
	}

	/**
	 * 具体执行逻辑
	 */
	@Override
	public Object run() throws ZuulException {
		// 从zuul自定义的上下文中拿到request
		RequestContext currentContext = RequestContext.getCurrentContext();
		HttpServletRequest request = currentContext.getRequest();
		System.out.println("访问地址:" + request.getServerName() + ":" + request.getRequestURI());
		// 每秒的请求不能超过100个
		if (!RATE_LIMITER.tryAcquire()) {
			// 没有拿到令牌
			throw new RuntimeException("没有拿到令牌,被限流了");
		}
		// 目前返回值并没有任何意义
		return null;
	}
}