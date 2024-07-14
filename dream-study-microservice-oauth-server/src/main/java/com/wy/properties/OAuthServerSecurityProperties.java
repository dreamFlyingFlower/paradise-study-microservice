package com.wy.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
@Configuration
@ConfigurationProperties("dream.oauth.server.security")
public class OAuthServerSecurityProperties {

	/**
	 * 不需要验证的资源URL,需要从根路径开始填写
	 */
	private String[] permitAllSources = { "/images/**", "/user/getCode/**", "/user/login/**", "/html/**", "/plugins/**",
			"/actuator/**", "/oauth/authorize**", "/oauth/**", "/oauth/token_key", "/messages/**", "/oauth/error",
			"/swagger-ui.html", "/swagger-ui/*", "/swagger-resources/**", "/v2/api-docs", "/v3/api-docs", "/webjars/**",
			"/login" };

	/**
	 * 生成JWT的RSA私钥
	 */
	private String jwtPrivateKey;
}