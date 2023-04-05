package com.wy.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wy.enums.TipEnum;
import com.wy.exception.AuthException;
import com.wy.result.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义匿名用户访问无权限资源时的异常
 *
 * @author 飞花梦影
 * @date 2023-04-05 23:16:15
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Component
@Slf4j
public class SelfAuthenticationEntryHandler implements AuthenticationEntryPoint {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
			throws IOException, ServletException {
		log.error("MyAuthenticationEntryPoint :: {} {} ", e.getMessage(), request.getRequestURL());
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		ServletOutputStream writer = response.getOutputStream();
		String message = e.getMessage();
		Integer code = HttpServletResponse.SC_UNAUTHORIZED;
		if (e instanceof AuthException) {
			AuthException be = (AuthException) e;
			message = be.getMessage();
			code = be.getCode();
		}
		if (e instanceof InsufficientAuthenticationException) {
			message = TipEnum.TIP_LOGIN_FAIL_NOT_LOGIN.getMsg();

			Throwable cause = e.getCause();
			if (cause instanceof InvalidTokenException) {
				message = TipEnum.TIP_AUTH_TOKEN_ERROR.getMsg();
				code = TipEnum.TIP_AUTH_TOKEN_ERROR.getCode();
			}
		}
		objectMapper.writeValue(writer, Result.error(code, message));
	}
}