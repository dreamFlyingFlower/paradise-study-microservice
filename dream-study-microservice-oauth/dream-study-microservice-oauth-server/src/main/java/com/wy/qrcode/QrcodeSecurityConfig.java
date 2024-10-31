package com.wy.qrcode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
@Deprecated
public class QrcodeSecurityConfig extends WebSecurityConfigurerAdapter {

	// 自动注入UserDetailsService
	@Autowired
	private QrcodeUserDetailsService qrcodeUserDetailsService;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
				// 自定义过滤器
				.addFilterAt(qrcodeLoginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				// 配置登陆页/login并允许访问
				.formLogin()
				.loginPage("/login")
				.permitAll()
				// 登出页
				.and()
				.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/backReferer")
				// 其余所有请求全部需要鉴权认证
				.and()
				.authorizeRequests()
				.anyRequest()
				.authenticated()
				.and()
				.csrf()
				.disable();
	}

	/**
	 * 用户验证
	 * 
	 * @param auth
	 */
	@Override
	public void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(defaultQrcodeAuthenticationProvider());
	}

	/**
	 * 自定义密码验证
	 * 
	 * @return
	 */
	@Bean
	public DefaultQrcodeAuthenticationProvider defaultQrcodeAuthenticationProvider() {
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
	public QrcodeLoginAuthenticationFilter qrcodeLoginAuthenticationFilter() {
		QrcodeLoginAuthenticationFilter filter = new QrcodeLoginAuthenticationFilter();
		try {
			filter.setAuthenticationManager(this.authenticationManagerBean());
		} catch (Exception e) {
			e.printStackTrace();
		}
		filter.setAuthenticationSuccessHandler(new CustomizerAuthenticationSuccessHandler());
		filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error"));
		return filter;
	}
}