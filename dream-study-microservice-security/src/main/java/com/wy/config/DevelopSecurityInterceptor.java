package com.wy.config;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import lombok.extern.slf4j.Slf4j;

/**
 * 资源访问过滤器,拦截访问请求,封装成安全对象FilterInvocation,默认实现{@link FilterSecurityInterceptor},
 * 将调用{@link DevelopAccessDecisionManager}和{@link DevelopFilterSecurityMetadataSource}进行鉴权
 *
 * @author 飞花梦影
 * @date 2021-01-21 10:57:28
 * @git {@link https://github.com/mygodness100}
 */
// @Component
@Slf4j
public class DevelopSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

	private final DevelopFilterSecurityMetadataSource developFilterSecurityMetadataSource;

	private final DevelopAccessDecisionManager developAccessDecisionManager;

	@Autowired
	public DevelopSecurityInterceptor(DevelopFilterSecurityMetadataSource developFilterSecurityMetadataSource,
			DevelopAccessDecisionManager developAccessDecisionManager) {
		this.developFilterSecurityMetadataSource = developFilterSecurityMetadataSource;
		this.developAccessDecisionManager = developAccessDecisionManager;
	}

	/**
	 * 初始化时将自定义的DecisionManager,注入到父类AbstractSecurityInterceptor中
	 */
	@PostConstruct
	public void initSetManager() {
		super.setAccessDecisionManager(developAccessDecisionManager);
	}

	/**
	 * 重写父类AbstractSecurityInterceptor,获取到自定义MetadataSource的方法
	 */
	@Override
	public SecurityMetadataSource obtainSecurityMetadataSource() {
		return this.developFilterSecurityMetadataSource;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		log.info("[自定义过滤器]: {}", " LoginSecurityInterceptor.doFilter()");
		FilterInvocation fi = new FilterInvocation(request, response, chain);
		// 调用父类的beforeInvocation->accessDecisionManager.decide()
		InterceptorStatusToken token = super.beforeInvocation(fi);
		try {
			fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
		} finally {
			// 调用父类的afterInvocation->afterInvocationManager.decide()
			super.afterInvocation(token, null);
		}
	}

	/**
	 * 向父类提供要处理的对象,因为父类被调用的方法参数类型大多是Object,框架需要保证传递进去的安全对象类型相同
	 */
	@Override
	public Class<?> getSecureObjectClass() {
		return FilterInvocation.class;
	}

	@Override
	public void init(FilterConfig filterConfig) {}

	@Override
	public void destroy() {}
}