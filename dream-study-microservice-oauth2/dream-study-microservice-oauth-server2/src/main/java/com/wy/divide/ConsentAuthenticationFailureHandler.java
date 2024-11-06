package com.wy.divide;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;

import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.framework.security.constant.ConstSecurity;
import dream.flying.flower.result.Result;

/**
 * 授权确认失败处理类,在调用确认授权接口失败时响应json
 *
 * @author 飞花梦影
 * @date 2024-11-04 22:22:11
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class ConsentAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		// 获取当前认证信息
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// 获取具体的异常
		OAuth2AuthenticationException authenticationException = (OAuth2AuthenticationException) exception;
		OAuth2Error error = authenticationException.getError();
		// 异常信息
		String message;
		if (authentication == null) {
			message = "登录已失效";
		} else {
			// 第二次点击“拒绝”会因为之前取消时删除授权申请记录而找不到对应的数据,导致抛出 [invalid_request] OAuth 2.0
			// Parameter: state
			message = error.toString();
		}

		// 授权确认页面提交的请求,因为授权申请与授权确认提交公用一个过滤器,这里判断一下
		if (request.getMethod().equals(HttpMethod.POST.name())
				&& UrlUtils.isAbsoluteUrl(ConstSecurity.CONSENT_PAGE_URI)) {
			// 写回json异常
			Result<Object> result = Result.error(HttpStatus.BAD_REQUEST.value(), message);
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write(JsonHelpers.toString(result));
			response.getWriter().flush();
		} else {
			// 在地址栏输入授权申请地址或设备码流程的验证地址错误(user_code错误)
			response.sendError(HttpStatus.BAD_REQUEST.value(), error.toString());
		}

	}
}