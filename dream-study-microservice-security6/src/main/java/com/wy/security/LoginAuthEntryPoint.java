package com.wy.security;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.alibaba.fastjson2.JSON;

import dream.flying.flower.lang.StrHelper;
import dream.flying.flower.result.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 前后端分离时,未登录时不能跳转后台的登录页面,需要返回json数据,
 * 该方法继承LoginUrlAuthenticationEntryPoint或重写AuthenticationEntryPoint,
 * 无无参构造,不可使用Configuration注解,否则启动报错,只能new一个实例,
 * 在调用父类有参构造的时候,loginFormUrl可为不为空任意字符串
 * 
 * @author ParadiseWY
 * @date 2019年2月13日 上午9:35:36
 * @git {@link https://github.com/mygodness100}
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
		        .msg(StrHelper.isBlank(authException.getMessage()) ? "您还未登录,请登录!" : authException.getMessage()).build()));
		out.flush();
		out.close();
	}
}