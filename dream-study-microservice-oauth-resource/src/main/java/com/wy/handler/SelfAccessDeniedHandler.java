package com.wy.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wy.enums.TipEnum;
import com.wy.result.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义认证过的用户访问无权限资源时的异常处理
 *
 * @author 飞花梦影
 * @date 2023-04-05 23:10:25
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Component
@Slf4j
public class SelfAccessDeniedHandler implements AccessDeniedHandler {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		PrintWriter writer = response.getWriter();
		log.info("MyAccessDeniedHandler 用户无权访问 [{}]", accessDeniedException.getMessage());
		objectMapper.writeValue(writer, Result.error(TipEnum.TIP_AUTH_DENIED));
	}
}