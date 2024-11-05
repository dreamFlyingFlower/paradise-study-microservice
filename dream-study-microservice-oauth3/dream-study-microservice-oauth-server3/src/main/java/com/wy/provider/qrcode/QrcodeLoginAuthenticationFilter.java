package com.wy.provider.qrcode;

import java.io.IOException;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 自定义过滤器,根据不同登陆类型获取对于参数, 并生成自定义的{@link QrcodeAuthenticationToken}
 *
 * @author 飞花梦影
 * @date 2024-10-30 10:40:53
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Setter
@Getter
public class QrcodeLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public static final String LOGIN_RESTFUL_TYPE_QR = "qrcode";

	private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
			new AntPathRequestMatcher("/qrcode", "POST");

	private boolean postOnly = true;

	public QrcodeLoginAuthenticationFilter() {
		super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
	}

	public QrcodeLoginAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		if (postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		// 登陆类型:user:用户密码登陆;phone:手机验证码登陆;qr:二维码扫码登陆
		String type = obtainParameter(request, "type");
		String mobile = obtainParameter(request, "mobile");
		QrcodeAuthenticationToken authRequest;
		String principal;
		String credentials;

		// 手机验证码登陆
		if ("phone".equals(type)) {
			principal = obtainParameter(request, "phone");
			credentials = obtainParameter(request, "verifyCode");
		}
		// 二维码扫码登陆
		else if ("qr".equals(type)) {
			principal = obtainParameter(request, "qrCode");
			credentials = null;
		}
		// 账号密码登陆
		else {
			principal = obtainParameter(request, "username");
			credentials = obtainParameter(request, "password");
			if (type == null)
				type = "user";
		}
		if (principal == null) {
			principal = "";
		}
		if (credentials == null) {
			credentials = "";
		}
		principal = principal.trim();
		authRequest = new QrcodeAuthenticationToken(principal, credentials, type, mobile);
		// Allow subclasses to set the "details" property
		setDetails(request, authRequest);
		return this.getAuthenticationManager().authenticate(authRequest);
	}

	private void setDetails(HttpServletRequest request, AbstractAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

	private String obtainParameter(HttpServletRequest request, String parameter) {
		return request.getParameter(parameter);
	}
}