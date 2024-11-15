package com.wy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import lombok.RequiredArgsConstructor;

/**
 * 在网关上做复杂的权限控制
 *
 * @author 飞花梦影
 * @date 2024-11-15 11:17:53
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Deprecated
@Configuration
@EnableResourceServer
@RequiredArgsConstructor
public class SecurityConfig extends ResourceServerConfigurerAdapter {

	private final SecurityExpressionHandler securityExpressionHandler;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId("gateway");
		// 注入自定义表达式
		resources.expressionHandler(securityExpressionHandler);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/token/**")
				.permitAll()
				.anyRequest()
				.access("permissionService.hasPermission(request,authentication)");
	}
}