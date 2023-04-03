package com.wy.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * OAuth2通用配置
 *
 * @author 飞花梦影
 * @date 2023-04-03 22:40:38
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Configuration
public class OAuth2Config {

	/**
	 * 设置默认加密方式
	 * 
	 * @return PasswordEncoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}