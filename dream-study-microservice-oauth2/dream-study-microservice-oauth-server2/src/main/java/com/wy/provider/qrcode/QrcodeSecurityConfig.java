package com.wy.provider.qrcode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import dream.flying.flower.framework.security.handler.CustomizerAuthenticationSuccessHandler;

/**
 * 注入SpringSecurity中
 *
 * @author 飞花梦影
 * @date 2024-10-30 11:12:49
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableWebSecurity
public class QrcodeSecurityConfig {

	@Autowired
	private QrcodeUserDetailsService qrcodeUserDetailsService;

	@Bean
	SecurityFilterChain qrcodeSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				// 自定义过滤器
				.addFilterAt(qrcodeLoginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				// 配置登陆页/login并允许访问
				.formLogin(formLogin -> formLogin.loginPage("/login").permitAll())
				// 登出页
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/backReferer"))
				// 其余所有请求全部需要鉴权认证
				.authorizeHttpRequests(request -> request.anyRequest().authenticated())
				.csrf(csrf -> csrf.disable());
		return httpSecurity.build();
	}

	/**
	 * 用户验证
	 * 
	 * @param auth
	 * @throws Exception
	 */
	@Bean
	AuthenticationManager configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(defaultQrcodeAuthenticationProvider());
		return auth.build();
	}

	/**
	 * 自定义密码验证
	 * 
	 * @return
	 */
	@Bean
	DefaultQrcodeAuthenticationProvider defaultQrcodeAuthenticationProvider() {
		DefaultQrcodeAuthenticationProvider provider = new DefaultQrcodeAuthenticationProvider();
		// 设置userDetailsService
		provider.setUserDetailsService(qrcodeUserDetailsService);
		// 禁止隐藏用户未找到异常
		provider.setHideUserNotFoundExceptions(false);
		// 使用BCrypt进行密码的hash
		provider.setPasswordEncoder(new BCryptPasswordEncoder(6));
		return provider;
	}

	/**
	 * 自定义登陆过滤器
	 * 
	 * @return
	 */
	@Bean
	QrcodeLoginAuthenticationFilter qrcodeLoginAuthenticationFilter() {
		QrcodeLoginAuthenticationFilter filter = new QrcodeLoginAuthenticationFilter();
		filter.setAuthenticationSuccessHandler(new CustomizerAuthenticationSuccessHandler());
		filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error"));
		return filter;
	}
}