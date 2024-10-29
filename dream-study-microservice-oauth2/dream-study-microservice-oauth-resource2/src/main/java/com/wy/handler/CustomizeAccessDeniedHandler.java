package com.wy.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import dream.flying.flower.enums.TipEnum;
import dream.flying.flower.framework.web.helper.WebHelpers;
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
public class CustomizeAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		log.info("CustomizeAccessDeniedHandler 用户无权访问 [{}]", accessDeniedException.getMessage());
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		WebHelpers.writeError(response, TipEnum.TIP_AUTH_DENIED);
	}
}