package com.wy.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @apiNote springsecurity配置
 * @author ParadiseWY
 * @date 2019年9月23日
 */
@Getter
@Setter
public class SecurityProperties {

	/**
	 * 不需要验证的资源URL,需要从根路径开始填写
	 */
	private String[] permitSources;

	/**
	 * 验证码,短信等验证配置
	 */
	private VerifyProperties code = new VerifyProperties();
}