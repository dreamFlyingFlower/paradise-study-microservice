package com.wy.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 客户端Security配置
 * 
 * @auther 飞花梦影
 * @date 2021-07-03 11:00:36
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Deprecated
@EnableWebSecurity
public class SecurtiyConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.mvcMatcher("/test/**")
				.authorizeRequests()
				.mvcMatchers("/test/**")
				.access("hasAuthority('SCOPE_message.read')")
				.and()
				.oauth2ResourceServer()
				.jwt();
	}
}