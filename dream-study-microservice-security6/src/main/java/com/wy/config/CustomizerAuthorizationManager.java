package com.wy.config;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class CustomizerAuthorizationManager implements AuthorizationManager<HttpServletRequest> {

	/**
	 * 权限鉴定
	 *
	 * @param authentication from
	 *        SecurityContextHolder.getContext()->userDetails.getAuthorities()
	 */
	@Override
	public AuthorizationDecision check(Supplier<Authentication> supplier, HttpServletRequest httpServletRequest) {
		Authentication authentication = supplier.get();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		log.info("[用户权限]: {}", authorities);
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		while (iterator.hasNext()) {
			// 资源的权限
			GrantedAuthority grantedAuthority = iterator.next();

			String authority = grantedAuthority.getAuthority();

			// 用户的权限
			if ("".equals(authority)) {
			}
		}
		throw new AccessDeniedException("权限不足");
	}
}