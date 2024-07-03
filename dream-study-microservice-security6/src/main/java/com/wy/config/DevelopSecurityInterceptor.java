package com.wy.config;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 资源访问过滤器,拦截访问请求,封装成安全对象FilterInvocation,默认实现{@link AuthorizationFilter},
 * 将调用{@link CustomizerAuthorizationManager}进行鉴权
 *
 * @author 飞花梦影
 * @date 2021-01-21 10:57:28
 * @git {@link https://github.com/mygodness100}
 */
// @Component
@Slf4j
public class DevelopSecurityInterceptor extends AuthorizationFilter {

	private final CustomizerAuthorizationManager customizerAuthorizationManager;

	public DevelopSecurityInterceptor(CustomizerAuthorizationManager customizerAuthorizationManager) {
		super(customizerAuthorizationManager);
		this.customizerAuthorizationManager = customizerAuthorizationManager;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		log.info("[自定义过滤器]: {}", " LoginSecurityInterceptor.doFilter()");
		FilterInvocation filterInvocation = new FilterInvocation(request, response, chain);
		filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
		AuthorizationDecision decision = this.customizerAuthorizationManager
				.check(() -> SecurityContextHolder.getContext().getAuthentication(), (HttpServletRequest) request);
		if (decision != null && !decision.isGranted()) {
			throw new AccessDeniedException("Access Denied");
		}
		chain.doFilter(request, response);
	}
}