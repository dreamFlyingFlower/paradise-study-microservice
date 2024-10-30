package com.wy.oauth2;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

/**
 * 自定义解析token.默认获取token是在header中,还要拼接一个前缀Bearer,重写BearerTokenResolver可自定义取token的方式
 *
 * @author 飞花梦影
 * @date 2024-10-30 16:50:21
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class CustomizerBearerTokenResolver implements BearerTokenResolver {

	private static final String BEARER_TOKEN_PARAM = "access_token";

	@Override
	public String resolve(HttpServletRequest request) {
		// 从 URI 参数中获取 Token
		String token = request.getParameter(BEARER_TOKEN_PARAM);
		return (token != null && !token.isEmpty()) ? token : null;
	}
}