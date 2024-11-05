package com.wy.divide;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.framework.security.constant.ConstAuthorization;
import dream.flying.flower.result.Result;

/**
 * 校验设备码成功响应类,在校验设备码成功后响应json
 *
 * @author 飞花梦影
 * @date 2024-11-04 22:25:14
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class DeviceAuthorizationResponseHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// 写回json数据
		Result<Object> result = Result.ok(ConstAuthorization.DEVICE_ACTIVATED_URI);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(JsonHelpers.toString(result));
		response.getWriter().flush();
	}
}