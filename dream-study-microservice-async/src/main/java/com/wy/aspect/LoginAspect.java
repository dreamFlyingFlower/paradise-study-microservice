package com.wy.aspect;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dream.enums.TipEnum;
import com.dream.lang.StrHelper;
import com.dream.result.Result;
import com.wy.properties.AsyncProperties;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 统一登录拦截器
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:55:37
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix = "dream.async", value = "enabled", havingValue = "true")
public class LoginAspect {

	@Autowired
	private AsyncProperties asyncProperties;

	@Value("${spring.application.name}")
	private String applicationName;

	@Pointcut("within(com.wy.controller.AsyncController)")
	public void pointcut() {
	}

	@Around(value = "pointcut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		if (!asyncProperties.getAsyncLogin().isEnabled()) {
			return joinPoint.proceed();
		}

		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
		try {
			HttpServletRequest request = servletRequestAttributes.getRequest();
			StringBuilder url = new StringBuilder("登录URL?redirect=");
			url.append("应用URL");
			url.append(applicationName);
			url.append("/async/index.html");
			String authorization = request.getHeader("authorization");
			if (StrHelper.isEmpty(authorization) || !this.login(authorization)) {
				this.print(servletRequestAttributes, TipEnum.TIP_LOGIN_FAIL.getCode(), url.toString());
				return null;
			}
			return joinPoint.proceed();
		} catch (Exception e) {
			log.error("统一认证处理失败", e);
			this.print(servletRequestAttributes, TipEnum.TIP_REQUEST_FAIL.getCode(), "请联系管理员");
			return null;
		}
	}

	/**
	 * 根据token校验是否登录
	 * 
	 * @param authorization
	 * @return
	 */
	private boolean login(String authorization) {
		HttpRequest httpRequest = HttpUtil.createPost(asyncProperties.getAsyncLogin().getUrl());
		httpRequest.header("authorization", authorization);
		HttpResponse response = httpRequest.execute();
		String result = response.body();
		if (StrHelper.isEmpty(result)) {
			return false;
		}
		return JSONUtil.parseObj(result).getBool("success");
	}

	/**
	 * 失败响应
	 * 
	 * @param servletRequestAttributes
	 * @param code
	 * @param msg
	 * @throws IOException
	 */
	private void print(ServletRequestAttributes servletRequestAttributes, Integer code, String msg) throws IOException {
		servletRequestAttributes.getResponse().getWriter().print(Result.result(code, msg, null));
	}
}