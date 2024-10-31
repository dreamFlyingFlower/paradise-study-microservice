package com.wy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 客户端Security配置
 * 
 * @auther 飞花梦影
 * @date 2021-07-03 11:00:36
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableWebSecurity
public class SecurtiyConfig {

	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		// 配置需要忽略检查的web url
		return web -> web.ignoring()
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

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login").failureUrl("/login-error").permitAll())
				// 配置登录URL
				.oauth2Login(oauth2Login -> oauth2Login.loginPage("/oauth2/authorization/login"))
				// 配置OAuth2 Client和OAuth2 Server交互,启用SSO
				.oauth2Client(Customizer.withDefaults());
		return http.build();
	}

	@Bean
	UserDetailsService users() {
		User.UserBuilder users = User.builder();
		users.passwordEncoder(t -> {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			return passwordEncoder.encode(t);
		});
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(users.username("user1").password("password").roles("USER").build());
		manager.createUser(users.username("admin").password("123456").roles("USER", "ADMIN").build());
		return manager;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}