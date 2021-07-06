package com.wy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.wy.oauth2.LoginSuccessHandler;
import com.wy.properties.ConfigProperties;
import com.wy.security.UserAuthenticationProvider;

/**
 * Security配置类,必须设置Order高一点,否则会有调用问题,默认是100
 *
 * @author 飞花梦影
 * @date 2021-07-02 16:36:46
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
@EnableWebSecurity
@Order(2)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private ConfigProperties config;

	@Autowired
	private LoginSuccessHandler loginSuccessHandler;

	@Autowired
	private UserAuthenticationProvider provider;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers(config.getSecurity().getPermitAllSources()).permitAll().anyRequest()
				.authenticated().and().formLogin().successHandler(loginSuccessHandler).and().csrf().disable();
	}

	/**
	 * 使用数据库中的数据来判断登录是否成功,在登录请求时会自动拦截请求,并进入验证
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(provider);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}