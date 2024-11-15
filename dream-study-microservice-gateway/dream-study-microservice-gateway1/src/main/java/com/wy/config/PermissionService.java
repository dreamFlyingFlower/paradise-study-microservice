package com.wy.config;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

/**
 * 权限处理
 *
 * @author 飞花梦影
 * @date 2024-11-15 11:21:13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Service
public class PermissionService {

	/**
	 * 判断是否有权限
	 * 
	 * @param exchange 当前请求
	 * @param authentication 当前用户
	 * @return true->有权限;false->无权限
	 */
	public boolean hasPermission(ServerWebExchange exchange, Authentication authentication) {
		// 从数据库或调用feign查询认证信息里的用户
		return false;
	}
}