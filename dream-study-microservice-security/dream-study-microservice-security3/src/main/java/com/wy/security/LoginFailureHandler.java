package com.wy.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.result.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录失败处理
 *
 * @author 飞花梦影
 * @date 2019-01-25 15:44:54
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Configuration
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		log.info("登录失败,原因:{}", exception.getMessage());
		Result<?> result = Result.error(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(JsonHelpers.toString(result));
		response.getWriter().flush();
	}
}