package com.wy.config;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * 自定义权限校验
 * 
 * @author 飞花梦影
 * @date 2021-01-21 10:33:33
 * @git {@link https://github.com/mygodness100}
 */
public class ExtraMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

	/**
	 * Creates the root object for expression evaluation.
	 */
	@Override
	protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication,
			MethodInvocation invocation) {
		ExtraSecurityExpressionRoot root = new ExtraSecurityExpressionRoot(authentication);
		root.setThis(invocation.getThis());
		root.setPermissionEvaluator(getPermissionEvaluator());
		root.setTrustResolver(getTrustResolver());
		root.setRoleHierarchy(getRoleHierarchy());
		root.setDefaultRolePrefix(getDefaultRolePrefix());
		return root;
	}
}