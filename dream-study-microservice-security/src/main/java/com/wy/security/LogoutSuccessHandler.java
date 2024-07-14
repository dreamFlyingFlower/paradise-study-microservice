package com.wy.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.alibaba.fastjson2.JSON;

import dream.flying.flower.result.Result;

/**
 * 登出
 * 
 * @author 飞花梦影
 * @date 2019-02-13 10:42:55
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		response.setContentType("application/json;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.write(JSON.toJSONString(Result.ok("登出成功", null)));
		out.flush();
		out.close();
	}
}