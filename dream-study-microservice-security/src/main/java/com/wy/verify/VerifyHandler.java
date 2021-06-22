package com.wy.verify;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * 校验码处理器,封装不同校验码的处理逻辑
 * 
 * @auther 飞花梦影
 * @date 2019-09-24 23:30:01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface VerifyHandler {

	/**
	 * 创建校验码
	 * 
	 * @param request 请求,响应
	 * @param type 校验码类型
	 */
	void generate(ServletWebRequest servletWebRequest, String type);

	/**
	 * 校验验证码
	 * 
	 * @param servletWebRequest 请求
	 * @param type 校验码类型
	 */
	boolean verify(ServletWebRequest servletWebRequest, String type);
}