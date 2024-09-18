package com.wy.config.authorization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录成功处理类
 *
 * @author 飞花梦影
 * @date 2024-09-18 22:14:31
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		log.debug("登录成功.");
		Result<String> success = Result.ok();
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(JsonHelpers.toString(success));
		response.getWriter().flush();
	}
}