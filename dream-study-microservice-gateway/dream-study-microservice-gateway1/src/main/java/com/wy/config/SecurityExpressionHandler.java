package com.wy.config;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

/**
 * 网关权限表达式处理器
 *
 * @author 飞花梦影
 * @date 2024-11-15 11:20:22
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Deprecated
@Component
@AllArgsConstructor
public class SecurityExpressionHandler extends OAuth2WebSecurityExpressionHandler {

	private final PermissionService permissionService;

	@Override
	protected StandardEvaluationContext createEvaluationContextInternal(Authentication authentication,
			FilterInvocation invocation) {
		StandardEvaluationContext standardEvaluationContext =
				super.createEvaluationContextInternal(authentication, invocation);
		standardEvaluationContext.setVariable("permissionService", permissionService);
		return standardEvaluationContext;
	}
}