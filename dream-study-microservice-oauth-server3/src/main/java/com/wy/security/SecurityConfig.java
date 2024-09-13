package com.wy.security;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.wy.oauth.jdbc.LoginSuccessHandler;
import com.wy.properties.OAuthServerSecurityProperties;

import lombok.AllArgsConstructor;

/**
 * Security配置,Order必须设置高一点,否则会有调用问题,默认是100
 *
 * @author 飞花梦影
 * @date 2023-04-03 22:40:38
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Order(2)
public class SecurityConfig {

	private final OAuthServerSecurityProperties oauthServerSecurityProperties;

	private final LoginSuccessHandler loginSuccessHandler;

	private final UserAuthenticationProvider userAuthenticationProvider;

	private final PasswordEncoder passwordEncoder;

	/**
	 * 使用数据库中的数据来判断登录是否成功,在登录请求时会自动拦截请求,并进入验证
	 * 
	 * guest:Bcrpt加密->$2a$10$dXULkWNhddYNVH9yQkzwQeJinGE0e22iL4CSEdkm7sRPwa.A27iEi
	 * 123456:Bcrpt加密->$2a$10$lg5hcqs13V3c6FVjr1/mjO31clz7fkjlIKnppDhNDdxJVaWxh/xB6
	 * password:Bcrpt加密->$2a$10$owjsydvplVmh0wI6f.xOM.4TKBc/CoKYTvX.HmnS6Yeu7qlyukAPO
	 */
	@Bean
	AuthenticationManager authenticationManager(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(userAuthenticationProvider);
		// auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
		auth.inMemoryAuthentication()
				.withUser(new User("guest", "$2a$10$lg5hcqs13V3c6FVjr1/mjO31clz7fkjlIKnppDhNDdxJVaWxh/xB6",
						Collections.emptyList()))
				.passwordEncoder(passwordEncoder);
		return auth.build();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize
				// 过滤swagger相关资源
				.requestMatchers("/authenticate", "/oauth/authorize", "/swagger-resources/**", "/swagger-ui/**",
						"/v3/api-docs", "/webjars/**")
				.permitAll()
				.requestMatchers(oauthServerSecurityProperties.getPermitAllSources())
				.permitAll()
				.anyRequest()
				.authenticated())
				.formLogin(form -> form.successHandler(loginSuccessHandler))
				.csrf(csrf -> csrf.disable());
		return http.build();
	}

	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		// 配置需要忽略检查的web url
		return web -> web.ignoring()
				// 忽略OPTIONS请求
				.requestMatchers(HttpMethod.OPTIONS)
				// 忽略指定URL请求
				.requestMatchers(
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