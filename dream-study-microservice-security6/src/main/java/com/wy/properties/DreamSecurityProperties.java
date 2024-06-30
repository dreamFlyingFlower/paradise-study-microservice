package com.wy.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * springsecurity配置
 * 
 * @author 飞花梦影
 * @date 2024-07-01 00:17:47
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties("dream.security")
public class DreamSecurityProperties {

	/**
	 * 不需要验证的资源URL,需要从根路径开始填写
	 */
	private String[] permitSources;

	/**
	 * JWT私钥
	 */
	private String jwtSecurityKey = "";

	/**
	 * 验证码,短信等验证配置
	 */
	private VerifyProperties code = new VerifyProperties();
}