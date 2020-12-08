package com.wy.verify;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * @apiNote 校验码处理器，封装不同校验码的处理逻辑
 * @author ParadiseWY
 * @date 2019年9月24日 下午10:26:37
 */
public interface VerifyHandler {

	/**
	 * 创建校验码
	 * 
	 * @param request
	 */
	void generate(ServletWebRequest servletWebRequest, String type);

	/**
	 * 校验验证码
	 * 
	 * @param servletWebRequest 请求
	 */
	boolean verify(ServletWebRequest servletWebRequest, String type);
}