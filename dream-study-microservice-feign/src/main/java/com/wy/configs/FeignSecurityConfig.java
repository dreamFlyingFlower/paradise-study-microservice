package com.wy.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.auth.BasicAuthRequestInterceptor;

/**
 * Feign安全验证拦截器
 * 
 * @author 飞花梦影
 * @date 2021-09-21 17:07:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class FeignSecurityConfig {

	/**
	 * 当注册服务中配置了安全访问时,需要添加访问服务的用户名和密码
	 * 
	 * @return 认证服务拦截
	 */
	@Bean
	public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
		return new BasicAuthRequestInterceptor("user", "password123");
	}
}