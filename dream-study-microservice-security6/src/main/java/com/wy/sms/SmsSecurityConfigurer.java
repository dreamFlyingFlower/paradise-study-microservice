package com.wy.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * 短信安全适配器
 *
 * @author 飞花梦影
 * @date 2024-07-01 23:44:41
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
public class SmsSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	@Autowired
	private AuthenticationSuccessHandler loginSuccessHandler;

	@Autowired
	private AuthenticationFailureHandler loginFailHandler;

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		SmsAuthenticationFilter smsCodeAuthenticationFilter = new SmsAuthenticationFilter();
		smsCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
		smsCodeAuthenticationFilter.setAuthenticationFailureHandler(loginFailHandler);

		SmsAuthenticationProvider smsCodeAuthenticationProvider = new SmsAuthenticationProvider();
		smsCodeAuthenticationProvider.setUserDetailsService(userDetailsService);

		http.authenticationProvider(smsCodeAuthenticationProvider).addFilterAfter(smsCodeAuthenticationFilter,
				UsernamePasswordAuthenticationFilter.class);
	}
}