package com.wy.oauth.memory.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.wy.properties.SecurityProperties;

/**
 * Security配置类,必须设置Order高一点,否则会有调用问题,默认是100
 *
 * @author 飞花梦影
 * @date 2021-07-02 16:36:46
 * @git {@link https://github.com/dreamFlyingFlower }
 */
// @Configuration
// @EnableWebSecurity
// @Order(2)
public class SecurityMemoryConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private SecurityProperties securityProperties;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(
				// 过滤静态资源
				"/public/**", "/static/**", "/resources/**",
				// swagger api json
				"/swagger**", "/swagger-ui.html", "/v2/api-docs",
				// 用来获取支持的动作
				"/swagger-resources/configuration/ui",
				// 用来获取api-docs的URI
				"/swagger-resources",
				// 安全选项
				"/swagger-resources/configuration/security", "/swagger-resources/**",
				// 在搭建swagger接口文档时,通过浏览器控制台发现该/webjars路径下的文件被拦截,故加上此过滤条件
				"/webjars/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				// 过滤swagger相关资源
				.antMatchers("/authenticate", "/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs", "/webjars/**")
				.permitAll().antMatchers(securityProperties.getPermitAllSources()).permitAll().anyRequest()
				.authenticated().and().formLogin().and().csrf().disable();
	}

	/**
	 * 使用内存中的数据来判断登录是否成功,在登录请求时会自动拦截请求,并进入验证
	 * 
	 * guest:Bcrpt加密->$2a$10$dXULkWNhddYNVH9yQkzwQeJinGE0e22iL4CSEdkm7sRPwa.A27iEi
	 * 123456:Bcrpt加密->$2a$10$lg5hcqs13V3c6FVjr1/mjO31clz7fkjlIKnppDhNDdxJVaWxh/xB6
	 * password:Bcrpt加密->$2a$10$owjsydvplVmh0wI6f.xOM.4TKBc/CoKYTvX.HmnS6Yeu7qlyukAPO
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser(new User("guest",
				"$2a$10$lg5hcqs13V3c6FVjr1/mjO31clz7fkjlIKnppDhNDdxJVaWxh/xB6", Collections.emptyList()))
				.passwordEncoder(passwordEncoder);
	}

	/**
	 * 不同的版本可能不一样,高版本一般不要
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}