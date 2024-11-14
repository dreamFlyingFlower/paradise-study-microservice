package com.wy.core;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.DenyAllPermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义权限解析器.该方式无效未尝试,可能有效,替代已经存在的{@link DenyAllPermissionEvaluator},必须自定义自己的Bean,然后在相关注解里使用
 *
 * @author 飞花梦影
 * @date 2024-11-14 11:23:27
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component("permissionEvaluator")
@Slf4j
public class CustomizerPermissionEvaluator implements PermissionEvaluator {

	@Override
	public boolean hasPermission(Authentication authentication, Object target, Object permission) {
		log.warn("Denying user {} permission '{}' on object {}", authentication.getName(), permission, target);
		return true;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission) {
		log.warn("Denying user {} permission '{}' on object with Id {}", authentication.getName(), permission,
				targetId);
		return true;
	}
}