package com.wy.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * Security相关配置
 *
 * @author 飞花梦影
 * @date 2021-07-02 16:29:11
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Getter
@Setter
public class SecurityProperties {

	/**
	 * 不需要验证的资源,url
	 */
	private String[] permitAllSources = { "/images/**", "/user/getCode/**", "/user/login/**", "/html/**", "/plugins/**",
			"/actuator/**", "/oauth/authorize**", "/oauth/token_key","/messages/**","/oauth/error","/login" };
}