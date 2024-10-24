package com.wy.grant;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 配置自定义AuthenticationProvider、自定义TokenGranter
 *
 * @author 飞花梦影
 * @date 2024-10-24 15:06:28
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@EnableWebSecurity
@Deprecated
public class PhoneOAuth2SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private PhoneUserDetailsService phoneUserDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// 创建一个登录用户,此处也可以用数据库
		auth.inMemoryAuthentication()
				.withUser("admin")
				.password(passwordEncoder.encode("123456"))
				.authorities("admin_role");
	}

	/**
	 * 手机验证码登录的认证提供者,若有多个认证方式,可添加多个
	 * 
	 * @return AuthenticationManager
	 */
	@Override
	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		List<AuthenticationProvider> authenticationProviders = new ArrayList<>();

		PhoneAuthenticationProvider phoneAuthenticationProvider = new PhoneAuthenticationProvider();
		phoneAuthenticationProvider.setRedisTemplate(redisTemplate);
		phoneAuthenticationProvider.setPhoneUserDetailsService(phoneUserDetailsService);

		authenticationProviders.add(phoneAuthenticationProvider);
		ProviderManager providerManager = new ProviderManager(authenticationProviders);
		return providerManager;
	}
}