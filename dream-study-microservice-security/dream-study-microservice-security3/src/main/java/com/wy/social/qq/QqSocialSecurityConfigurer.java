package com.wy.social.qq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.wy.sms.SmsAuthenticationProvider;

/**
 * 自定义SocialConfigurer,在将SocialAuthenticationFilter加入之前做一些自定义操作,需要重写postProcess()
 * 
 * @auther 飞花梦影
 * @date 2019-09-26 09:41:35
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class QqSocialSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	@Autowired
	private AuthenticationSuccessHandler loginSuccessHandler;

	@Autowired
	private AuthenticationFailureHandler loginFailHandler;

	@Autowired
	private UserDetailsService userDetailsService;

	/**
	 * 重写需要放到过滤器链上的filter
	 * 
	 * @param http
	 * @throws Exception
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		QqAuthenticationFilter qqAuthenticationFilter = new QqAuthenticationFilter();
		qqAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		qqAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
		qqAuthenticationFilter.setAuthenticationFailureHandler(loginFailHandler);

		SmsAuthenticationProvider smsCodeAuthenticationProvider = new SmsAuthenticationProvider();
		smsCodeAuthenticationProvider.setUserDetailsService(userDetailsService);

		http.authenticationProvider(smsCodeAuthenticationProvider)
				.addFilterAfter(qqAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	}
}