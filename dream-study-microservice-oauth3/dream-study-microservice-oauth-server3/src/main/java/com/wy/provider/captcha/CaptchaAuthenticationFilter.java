package com.wy.provider.captcha;

import java.io.IOException;

import org.springframework.http.HttpMethod;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.wy.exception.AuthException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 验证码拦截器,参照{@link AbstractAuthenticationProcessingFilter},需要添加到SecurityFilterChain的过滤器链中
 *
 * @author 飞花梦影
 * @date 2024-11-02 14:41:45
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
public class CaptchaAuthenticationFilter extends GenericFilterBean {

	private AuthenticationFailureHandler failureHandler;

	private final RequestMatcher requiresAuthenticationRequestMatcher;

	/**
	 * 初始化该过滤器，设置拦截的地址
	 *
	 * @param defaultFilterProcessesUrl 拦截的地址
	 */
	public CaptchaAuthenticationFilter(String defaultFilterProcessesUrl) {
		Assert.hasText(defaultFilterProcessesUrl, "defaultFilterProcessesUrl cannot be null.");
		requiresAuthenticationRequestMatcher = new AntPathRequestMatcher(defaultFilterProcessesUrl);
		failureHandler = new SimpleUrlAuthenticationFailureHandler(defaultFilterProcessesUrl + "?error");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// 检验是否是post请求并且是需要拦截的地址
		if (!this.requiresAuthenticationRequestMatcher.matches(request)
				|| !request.getMethod().equals(HttpMethod.POST.toString())) {
			chain.doFilter(request, response);
			return;
		}
		// 开始校验验证码
		log.info("Authenticate captcha...");

		// 获取参数中的验证码
		String code = request.getParameter("code");
		try {
			if (ObjectUtils.isEmpty(code)) {
				throw new AuthException("The captcha cannot be empty.");
			}

			// 获取session中存储的验证码
			Object sessionCaptcha = request.getSession(Boolean.FALSE).getAttribute("captcha");
			if (sessionCaptcha instanceof String) {
				String sessionCode = (String) sessionCaptcha;
				if (!sessionCode.equalsIgnoreCase(code)) {
					throw new AuthException("The captcha is incorrect.");
				}
			} else {
				throw new AuthException("The captcha is abnormal. Obtain it again.");
			}
		} catch (AuthenticationException ex) {
			this.failureHandler.onAuthenticationFailure(request, response, ex);
			return;
		}

		log.info("Captcha authenticated.");
		// 验证码校验通过开始执行接下来的逻辑
		chain.doFilter(request, response);
	}

	public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
		Assert.notNull(failureHandler, "failureHandler cannot be null");
		this.failureHandler = failureHandler;
	}
}