package com.wy.zuul;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.cloud.netflix.zuul.filters.post.SendErrorFilter;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * 自定义Zuul的错误过滤器,需要先禁用Zuul默认的{@link SendErrorFilter}
 *
 * @author 飞花梦影
 * @date 2021-09-23 15:23:54
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Component
public class MyErrorFilter extends ZuulFilter {

	@Override
	public String filterType() {
		return FilterConstants.ERROR_TYPE;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public int filterOrder() {
		return 1;
	}

	@Override
	public Object run() throws ZuulException {
		try {
			RequestContext context = RequestContext.getCurrentContext();
			ZuulException exception = (ZuulException) context.getThrowable();
			System.out.println("进入系统异常拦截" + exception.getMessage());

			HttpServletResponse response = context.getResponse();
			response.setContentType("application/json; charset=utf8");
			response.setStatus(exception.nStatusCode);
			PrintWriter writer = null;
			try {
				writer = response.getWriter();
				writer.print("{code:" + exception.nStatusCode + ",message:\"" + exception.getMessage() + "\"}");

				// 重定向
				// response.sendRedirect("/url");
				// HttpServletRequest request = context.getRequest();
				// RequestDispatcher dispatcher = request.getRequestDispatcher("/path");
				// dispatcher.forward(request, response);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		} catch (Exception e) {
			ReflectionUtils.rethrowRuntimeException(e);
		}
		return null;
	}
}