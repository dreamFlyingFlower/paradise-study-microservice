package com.wy.security;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.wy.properties.OAuthServerSecurityProperties;

import dream.flying.flower.framework.security.handler.CustomizerAuthenticationSuccessHandler;
import lombok.AllArgsConstructor;

/**
 * Security配置,Order必须设置高一点,否则会有调用问题,默认是100
 *
 * @author 飞花梦影
 * @date 2023-04-03 22:40:38
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Deprecated
@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Order(2)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final OAuthServerSecurityProperties oauthServerSecurityProperties;

	private final CustomizerAuthenticationSuccessHandler loginSuccessHandler;

	private final UserAuthenticationProvider userAuthenticationProvider;

	private final PasswordEncoder passwordEncoder;

	@Bean
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	/**
	 * 使用数据库中的数据来判断登录是否成功,在登录请求时会自动拦截请求,并进入验证
	 * 
	 * guest:Bcrpt加密->$2a$10$dXULkWNhddYNVH9yQkzwQeJinGE0e22iL4CSEdkm7sRPwa.A27iEi
	 * 123456:Bcrpt加密->$2a$10$lg5hcqs13V3c6FVjr1/mjO31clz7fkjlIKnppDhNDdxJVaWxh/xB6
	 * password:Bcrpt加密->$2a$10$owjsydvplVmh0wI6f.xOM.4TKBc/CoKYTvX.HmnS6Yeu7qlyukAPO
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(userAuthenticationProvider);
		// auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
		auth.inMemoryAuthentication()
				.withUser(new User("guest", "$2a$10$lg5hcqs13V3c6FVjr1/mjO31clz7fkjlIKnppDhNDdxJVaWxh/xB6",
						Collections.emptyList()))
				.passwordEncoder(passwordEncoder);
	}

	/**
	 * 设置拦截器 给资源添加权限
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf()
				.disable()
				.authorizeRequests()
				// 过滤swagger相关资源
				.antMatchers("/authenticate", "/oauth/authorize", "/swagger-resources/**", "/swagger-ui/**",
						"/v3/api-docs", "/webjars/**")
				.permitAll()
				.antMatchers(oauthServerSecurityProperties.getPermitAllSources())
				.permitAll()
				.antMatchers("/login")
				.permitAll()

				.antMatchers("/test/test1")
				.hasAnyAuthority("p1")
				.antMatchers("/test/test2")
				.hasAnyAuthority("p2")
				.anyRequest()
				.authenticated()
				.and()
				.formLogin()
				.successHandler(loginSuccessHandler);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// 配置不走SpringSecurity过滤器链的url,被忽略的请求不会被SecurityContextPersistenceFilter拦截,也不会存入Session,特别是登录,不能放这
		web.ignoring()
				// 忽略OPTIONS请求
				.antMatchers(HttpMethod.OPTIONS)
				// 忽略指定URL请求
				.antMatchers(
						// 过滤静态资源
						"/public/**", "/static/**", "/resources/**", "/js/**", "/css/**", "/images/**",
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
}