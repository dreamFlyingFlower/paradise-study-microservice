package com.wy.verify;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * @apiNote 校验,如验证码,短信等
 * @author ParadiseWY
 * @date 2019年9月24日
 */
@FunctionalInterface
public interface VerifyGenerator {

	/**
	 * 生成校验码
	 * @param request 请求,响应
	 * @return 验证实体类
	 */
	VerifyEntity generateVerify(ServletWebRequest request);
}