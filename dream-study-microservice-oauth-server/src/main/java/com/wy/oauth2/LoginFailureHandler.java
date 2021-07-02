package com.wy.oauth2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wy.exception.AuthException;
import com.wy.result.Result;

/**
 * 安全登录失败的处理
 * 
 * @author 飞花梦影
 * @date 2019-01-25 15:44:54
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		int errorCode = 0;
		if (exception instanceof AuthException) {
			AuthException authException = (AuthException) exception;
			errorCode = authException.getCode();
		}
		Result<?> result = Result.builder().code(errorCode).msg(exception.getMessage()).build();
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(result));
	}
}