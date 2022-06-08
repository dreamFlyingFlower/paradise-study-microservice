package com.wy.configs;

import java.util.Enumeration;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Feign被调用之前的拦截器,可以解决Feign被调用时请求头丢失或其他关于request的问题,包括session,cookie等
 * 
 * 若远程请求异步调用Feign服务,并非在主线程中,则异步请求无法通过RequestContextHolder获取主县城请求头信息,
 * 此时需要在异步调用的线程代码中将主线程的RequestContextHolder.getRequestAttributes()的结果再set进去
 * 
 * @author 飞花梦影
 * @date 2020-12-26 23:29:30
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class FeignInterceptorConfig {

	@Bean
	public RequestInterceptor requestInterceptor() {
		return new RequestInterceptor() {

			@Override
			public void apply(RequestTemplate requestTemplate) {
				// 拿到刚进来的这个请求
				ServletRequestAttributes requestAttributes =
				        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
				HttpServletRequest request = requestAttributes.getRequest();
				// 同步请求头数据
				requestTemplate.header("Cookie", request.getHeader("Cookie"));
				// 将请求头中所有数据进行同步
				Enumeration<String> headerNames = request.getHeaderNames();
				if (headerNames != null) {
					while (headerNames.hasMoreElements()) {
						String name = headerNames.nextElement();
						String values = request.getHeader(name);
						requestTemplate.header(name, values);
					}
				}
			}
		};
	}

	// 获得主线程的RequestAttributes
	RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

	public void testAsync() {
		CompletableFuture.runAsync(() -> {
			// 假设此处异步调用Feign远程服务,需要先将attributes设置到本线程的RequestContextHolder中
			RequestContextHolder.setRequestAttributes(attributes);
			// dosomething
		});
	}
}