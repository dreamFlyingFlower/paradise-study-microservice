package com.wy.divide;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.core.util.JsonUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.ObjectUtils;

import com.itextpdf.text.pdf.security.SecurityConstants;

import dream.flying.flower.result.Result;
import lombok.extern.slf4j.Slf4j;

/**
 * 修改重定向至登录页面处理,容在请求校验设备码时登录信息过期处理
 *
 * @author 飞花梦影
 * @date 2024-11-04 22:25:56
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
public class LoginTargetAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	/**
	 * @param loginFormUrl URL where the login page can be found. Should either be
	 *        relative to the web-app context path (include a leading {@code /}) or
	 *        an absolute URL.
	 */
	public LoginTargetAuthenticationEntryPoint(String loginFormUrl) {
		super(loginFormUrl);
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		String deviceVerificationUri = "/oauth2/device_verification";
		// 兼容设备码前后端分离
		if (request.getRequestURI().equals(deviceVerificationUri) && request.getMethod().equals(HttpMethod.POST.name())
				&& UrlUtils.isAbsoluteUrl(DEVICE_ACTIVATE_URI)) {
			// 如果是请求验证设备激活码(user_code)时未登录并且设备码验证页面是前后端分离的那种则写回json
			Result<String> success = Result.error(HttpStatus.UNAUTHORIZED.value(), ("登录已失效，请重新打开设备提供的验证地址"));
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write(JsonUtils.objectCovertToJson(success));
			response.getWriter().flush();
			return;
		}

		// 获取登录表单的地址
		String loginForm = determineUrlToUseForThisRequest(request, response, authException);
		if (!UrlUtils.isAbsoluteUrl(loginForm)) {
			// 不是绝对路径调用父类方法处理
			super.commence(request, response, authException);
			return;
		}

		StringBuffer requestUrl = request.getRequestURL();
		if (!ObjectUtils.isEmpty(request.getQueryString())) {
			requestUrl.append("?").append(request.getQueryString());
		}

		// 2023-07-11添加逻辑：重定向地址添加nonce参数，该参数的值为sessionId
		// 绝对路径在重定向前添加target参数
		String targetParameter = URLEncoder.encode(requestUrl.toString(), StandardCharsets.UTF_8);
		String targetUrl = loginForm + "?target=" + targetParameter + "&" + SecurityConstants.NONCE_HEADER_NAME + "="
				+ request.getSession(Boolean.FALSE).getId();
		log.debug("重定向至前后端分离的登录页面：{}", targetUrl);
		this.redirectStrategy.sendRedirect(request, response, targetUrl);
	}
}