package com.wy.core;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.wy.config.ClientSecurityConfig;

/**
 * 客户端Security配置,主配置见{@link ClientSecurityConfig}
 * 
 * @auther 飞花梦影
 * @date 2021-07-03 11:00:36
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableWebSecurity
public class FederatedIdentitySecurtiyConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login").failureUrl("/login-error").permitAll())
				// 配置登录URL
				.oauth2Login(oauth2Login -> oauth2Login.loginPage("/oauth2/authorization/login")
						// 自定义调用第三方认证服务成功的用户处理方式
						.successHandler(new FederatedIdentityAuthenticationSuccessHandler()))
				// 配置OAuth2 Client和OAuth2 Server交互,启用SSO
				.oauth2Client(Customizer.withDefaults());
		return http.build();
	}
}