package com.wy.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.wy.exception.AuthException;

import dream.flying.flower.enums.TipEnum;
import dream.flying.flower.framework.web.helper.WebHelpers;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义匿名用户访问无权限资源时的异常
 *
 * @author 飞花梦影
 * @date 2023-04-05 23:16:15
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Deprecated
@Component
@Slf4j
public class CustomizeAuthenticationEntryHandler implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
			throws IOException, ServletException {
		log.error("CustomizeAuthenticationEntryHandler :: {} {} ", e.getMessage(), request.getRequestURL());
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		String message = e.getMessage();
		Integer code = HttpServletResponse.SC_UNAUTHORIZED;
		if (e instanceof AuthException) {
			AuthException ae = (AuthException) e;
			message = ae.getMessage();
			code = ae.getCode();
		}
		if (e instanceof InsufficientAuthenticationException) {
			message = TipEnum.TIP_LOGIN_FAIL_NOT_LOGIN.getMsg();

			Throwable cause = e.getCause();
			if (cause instanceof InvalidTokenException) {
				message = TipEnum.TIP_AUTH_TOKEN_ERROR.getMsg();
				code = TipEnum.TIP_AUTH_TOKEN_ERROR.getCode();
			}
		}
		WebHelpers.writeError(response, code, message);
	}
}