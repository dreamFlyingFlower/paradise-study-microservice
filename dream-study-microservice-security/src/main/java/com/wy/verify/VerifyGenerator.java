package com.wy.verify;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * 生成校验实体对象,如验证码,短信等
 * 
 * @auther 飞花梦影
 * @date 2019-09-24 23:21:38
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@FunctionalInterface
public interface VerifyGenerator {

	/**
	 * 生成校验码
	 * 
	 * @param request 请求,响应
	 * @return 验证实体类
	 */
	VerifyEntity generateVerify(ServletWebRequest request);
}