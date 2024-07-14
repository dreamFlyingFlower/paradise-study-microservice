package com.wy.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wy.common.AuthException;
import com.wy.properties.UserProperties;
import com.wy.verify.VerifyHandlerFactory;
import com.wy.verify.VerifyInfo;

import dream.flying.flower.collection.ListHelper;
import dream.flying.flower.lang.StrHelper;

/**
 * 登录请求验证码拦截器,拦截登录时的验证码
 * 
 * 被拦截的登录请求中必须携带verifyType参数,该参数是用来表明验证的类型,如图片验证,短信验证或其他.
 * 该参数必须直接拼接在url后面,防止因为请求类型的不同而无法获得该参数.
 * 其他参数除了用户名和密码之外,还需要将verifyType的值作为参数,需要验证的值做为该参数的值传入请求中.
 * 如verifyType=image,则还需要参数image=需要验证的值
 * 
 * @auther 飞花梦影
 * @date 2019-09-30 23:48:09
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class VerifyFilter extends OncePerRequestFilter implements InitializingBean {

	/**
	 * 默认需要进行图片验证的url
	 */
	private List<String> defaultFilterUrls = new ArrayList<>(Arrays.asList("/login", "/user/login"));

	/**
	 * 校验码处理
	 */
	@Autowired
	private VerifyHandlerFactory verifyHandlerFactory;

	/**
	 * 验证失败异常处理,不可直接扔出异常,无法被正确捕捉
	 */
	@Autowired
	private AuthenticationFailureHandler failureHandler;

	/**
	 * 用户自定义配置信息
	 */
	@Autowired
	private UserProperties userProperties;

	/**
	 * 验证请求url与配置的url是否匹配的工具类
	 */
	private AntPathMatcher pathMatcher = new AntPathMatcher();

	/**
	 * 初始化要拦截的url配置信息
	 */
	@Override
	public void afterPropertiesSet() throws ServletException {
		super.afterPropertiesSet();
		handlerVerifyUrl();
	}

	/**
	 * 将系统中配置的需要校验验证码的URL根据校验的类型放入map
	 */
	protected void handlerVerifyUrl() {
		List<String> imageVerifyUrls = userProperties.getVerify().getImage().getUrls();
		if (ListHelper.isNotEmpty(imageVerifyUrls)) {
			defaultFilterUrls.addAll(imageVerifyUrls);
		}
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		if (userProperties.getVerify().isEnabled()) {
			try {
				VerifyInfo type = getVerifyType(request);
				if (type != null) {
					logger.info("校验请求(" + request.getRequestURI() + ")中的验证码,验证码类型" + type);
					verifyHandlerFactory.getHandler(type).verify(new ServletWebRequest(request, response),
							type.getVerifyType());
					logger.info("验证码校验通过");
				}
			} catch (AuthException e) {
				failureHandler.onAuthenticationFailure(request, response, e);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	/**
	 * 获取校验码的类型,如果当前请求不需要校验,则返回null
	 * 
	 * @param request
	 * @return
	 */
	private VerifyInfo getVerifyType(HttpServletRequest request) {
		for (String url : defaultFilterUrls) {
			if (pathMatcher.match(url, request.getRequestURI())) {
				String requestType = request.getParameter("verifyType");
				if (StrHelper.isBlank(requestType)) {
					throw new AuthException("未指定登录验证类型verifyType");
				}
				VerifyInfo verifyInfo = verifyHandlerFactory.getVerifyInfo(requestType);
				if (verifyInfo == null) {
					throw new AuthException("验证类型错误");
				}
				return verifyInfo;
			}
		}
		return null;
	}
}