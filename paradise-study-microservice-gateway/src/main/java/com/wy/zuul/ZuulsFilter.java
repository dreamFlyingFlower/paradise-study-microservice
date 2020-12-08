package com.wy.zuul;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.wy.utils.StrUtils;


/**
 * @apiNote zuul的拦截器,拦截所有的url.需要重写的一些参数可以在FilterContants中找到,或直接查看父类
 * @author ParadiseWY
 * @date 2019年9月16日
 */
@Configuration
public class ZuulsFilter extends ZuulFilter {

	/**
	 * 自定义拦截的操作
	 * @return 不返回任何值即是通过
	 * @throws ZuulException
	 */
	@Override
	public Object run() throws ZuulException {
		// 从zuul自定义的上下文中拿到request
		RequestContext context = RequestContext.getCurrentContext();
		HttpServletRequest request = context.getRequest();
		String token = request.getHeader("token");
		if(StrUtils.isBlank(token)) {
			// 表示验证不通过,无响应
			context.setSendZuulResponse(false);
			context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
		}
		return null;
	}

	/**
	 * 是否执行拦截
	 * @return true执行
	 */
	@Override
	public boolean shouldFilter() {
		return true;
	}

	/**
	 * 拦截的优先级,值越小拦截的等级越高.可以使用0,最好是根据filterType来使用,see FilterConstants 上的注解
	 * @return
	 */
	@Override
	public int filterOrder() {
		return FilterConstants.PRE_DECORATION_FILTER_ORDER-1;
	}

	/**
	 * 拦截的类型,可直接写字符串,也可以从see FilterConstants 中查找
	 * @return
	 */
	@Override
	public String filterType() {
		return FilterConstants.PRE_TYPE;
	}
}