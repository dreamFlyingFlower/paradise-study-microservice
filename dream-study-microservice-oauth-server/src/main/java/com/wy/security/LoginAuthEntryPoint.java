package com.wy.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.alibaba.fastjson.JSON;

import dream.flying.flower.lang.StrHelper;
import dream.flying.flower.result.Result;

/**
 * 前后端分离时,未登录时不能跳转后台的登录页面,需要返回json数据给前端进行判断
 * 
 * 该方法继承{@link LoginUrlAuthenticationEntryPoint}或重写{@link AuthenticationEntryPoint},
 * 无无参构造,不可使用Configuration,否则启动报错,只能new.在调用父类有参构造的时候,loginFormUrl可为不为空任意字符串
 * 
 * @author 飞花梦影
 * @date 2019-02-13 09:35:36
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class LoginAuthEntryPoint extends LoginUrlAuthenticationEntryPoint {

	public LoginAuthEntryPoint(String loginFormUrl) {
		super("/");
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.setContentType("application/json;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.write(JSON.toJSONString(Result.builder().code(501)
				.msg(StrHelper.isBlank(authException.getMessage()) ? "您还未登录,请登录!" : authException.getMessage())
				.build()));
		out.flush();
		out.close();
	}
}