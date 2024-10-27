package com.wy.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.wy.oauth.CustomizeTokenEnhancer;

/**
 * 密码加解密方式
 *
 * @author 飞花梦影
 * @date 2024-07-13 23:46:23
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Deprecated
@Configuration
public class OAuthConfig {

	/**
	 * 设置默认加密方式
	 * 
	 * @return PasswordEncoder
	 */
	@Bean
	@ConditionalOnMissingBean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 设置默认的token生成方式
	 * 
	 * @return TokenEnhancer
	 */
	@Bean
	@ConditionalOnMissingBean
	TokenEnhancer tokenEnhancer() {
		return new CustomizeTokenEnhancer();
	}
}