package com.wy.config;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestMatcherDelegatingAuthorizationManager;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义鉴权管理器,根据URL资源权限和用户角色权限进行鉴权,用在登录时,不能应用于{@link PreAuthorize}等注解
 * 
 * 参照{@link RequestMatcherDelegatingAuthorizationManager}
 * 
 * @author 飞花梦影
 * @date 2021-01-21 10:50:54
 * @git {@link https://github.com/mygodness100}
 */
// @Component
@Slf4j
public class DevelopAccessDecisionManager implements AuthorizationManager<HttpServletRequest> {

	/**
	 * 权限鉴定
	 *
	 * @param authentication from SecurityContextHolder.getContext()->userDetails.getAuthorities()
	 * @param object 是一个安全对象类型,FilterInvocation.class
	 * @param configAttributes from MetaDataSource.getAttributes(),已经被框架做了非空判断
	 */
	@Override
	public AuthorizationDecision check(Supplier<HttpServletRequest> authentication, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub

		log.info("[资源权限]: {}", configAttributes);
		log.info("[用户权限]: {}", authentication.getAuthorities());
		Iterator<ConfigAttribute> it = configAttributes.iterator();
		while (it.hasNext()) {
			// 资源的权限
			ConfigAttribute resourceAttr = it.next();
			String resourceRole = "ROLE_" + ((SecurityConfig) resourceAttr).getAttribute();

			// 用户的权限
			for (GrantedAuthority userAuth : authentication.getAuthorities()) {
				log.info("[资源角色==用户角色] ？ {} == {}", resourceRole.trim(), userAuth.getAuthority().trim());
				if (resourceRole.trim().equals(userAuth.getAuthority().trim())) {
					return;
				}
			}
		}
		throw new AccessDeniedException("权限不足");
	
		return null;
	}
}