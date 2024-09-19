package com.wy.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.result.Result;

/**
 * 安全登录成功的处理
 * 
 * 此处处理json返回值的方法需要根据框架中处理json的jar改变:<br>
 * 若使用原生的jackson处理json,则此处需要使用objectmapper将结果转化为字符串,否则jackson的配置将无效,如忽略密码<br>
 * 若使用fastjson处理json,则此处使用使用fastjson的相关方法转换结果,否则fastjson的配置将无效
 * 
 * @author 飞花梦影
 * @date 2019-01-25 15:41:58
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// 什么都不做,那就直接调用父类的方法
		// super.onAuthenticationSuccess(request, response, authentication);

		// 允许跨域
		response.setHeader("Access-Control-Allow-Origin", "*");
		// 允许自定义请求头token(允许head跨域)
		response.setHeader("Access-Control-Allow-Headers",
				"token, Accept, Origin, X-Requested-With, Content-Type, Last-Modified");

		// 这里可以根据实际情况,来确定是跳转到页面或者json格式
		// 返回json格式
		Result<?> result = Result.ok("登录成功", null);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(JsonHelpers.toString(result));
		response.getWriter().flush();

		// 重定向到某个页面
		// new DefaultRedirectStrategy().sendRedirect(request, response, "/profile");
	}
}