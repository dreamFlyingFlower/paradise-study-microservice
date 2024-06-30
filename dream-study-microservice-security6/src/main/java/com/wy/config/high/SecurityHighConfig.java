package com.wy.config.high;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.wy.config.jwt.JwtAuthenticationFilter;
import com.wy.properties.DreamSecurityProperties;
import com.wy.security.LoginAuthEntryPoint;
import com.wy.service.UserService;

import lombok.AllArgsConstructor;

/**
 * SpringSecurity6以上配置
 * 
 * 自定义数据库用户登录参考{@link JdbcUserDetailsManager}
 *
 * @author 飞花梦影
 * @date 2023-02-01 16:51:06
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class SecurityHighConfig {

	private final DreamSecurityProperties dreamSecurityProperties;

	private final UserService userService;

	/**
	 * jwt 校验过滤器,从 http 头部 Authorization 字段读取 token 并校验
	 */
	@Bean
	JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(dreamSecurityProperties, userService);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 使用一种方式登录,和下面的无参方法不能同时存在
	 * 
	 * @return AuthenticationManager
	 * @throws Exception
	 */
	@Bean
	AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userService)
				.passwordEncoder(passwordEncoder()).and().build();
	}

	/**
	 * 使用多种自定义方式登录
	 * 
	 * @return AuthenticationManager
	 */
	@Bean
	AuthenticationManager authenticationManager() {
		List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
		// 普通数据库登录方式
		authenticationProviders.add(daoAuthenticationProvider());
		// 可以添加更多方式的登录方式
		// authenticationProviders.add(mobileAuthenticationProvider());
		// authenticationProviders.add(weixinAuthenticationProvider());
		ProviderManager providerManager = new ProviderManager(authenticationProviders);
		return providerManager;
	}

	/**
	 * 普通用户数据库用户名密码登录
	 * 
	 * @return DaoAuthenticationProvider
	 */
	@Bean
	DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		daoAuthenticationProvider.setUserDetailsService(userService);
		return daoAuthenticationProvider;
	}

	/**
	 * 通过创建 SecurityFilterChain 来配置 HttpSecurity
	 * 
	 * @param http HttpSecurity
	 * @throws Exception
	 */
	@Bean
	SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.authorizeHttpRequests((authz) -> authz.anyRequest().authenticated())
				// 自定义认证处理器,默认为 ProviderManager
				.authenticationManager(new SelfAuthenticationManager());

		return httpSecurity
				// 禁用csrf
				.csrf(csrf -> csrf.disable())
				// 使用默认的登录方式,添加UsernamePasswordAuthenticationFilter,
				// 自动生成登录页面和注销页面的DefaultLoginPageGeneratingFilter和DefaultLogoutPageGeneratingFilter
				.formLogin(Customizer.withDefaults())
				// 自定义登录配置,仍然会添加UsernamePasswordAuthenticationFilter
				.formLogin(formLogin -> formLogin
						// 自定义登录页面,不使用内置的自动生成页面,若定义该参数,其他默认配置失效,见FormLoginConfigurer#initDefaultLoginFilter
						.loginPage("/login")
						// 访问login页面不需要认证和鉴权
						.permitAll())
				// oauth2配置
				// .oauth2Login(null)
				// saml配置
				// .saml2Login(null)
				// 基于 token,不需要 session
				.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// 设置 jwtAuthError 处理认证失败,鉴权失败
				.exceptionHandling(handling -> handling.authenticationEntryPoint(new LoginAuthEntryPoint(null))
						// 自定义权限错误处理器
						.accessDeniedHandler(new AccessDeniedHandlerImpl()))
				// 设置权限,相当于打开了鉴权模块,它会注册AuthorizationFilter到SecurityFilterChain的最后
				.authorizeHttpRequests(request -> request
						// "/admin"要求有ADD_USER的权限
						.requestMatchers("/admin").hasAuthority("ADD_USER")
						// "/hello"要求有"ROLE_USER"角色权限
						.requestMatchers("/hello").hasRole("USER")
						// 所有请求无需验证即可通过
						.requestMatchers("/**").permitAll()
						// 其他请求只需要身份验证即可,无需其他特殊权限
						.anyRequest().authenticated())
				// 添加 JWT 过滤器,JWT 过滤器在用户名密码认证过滤器之前
				.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				// 登出操作
				.logout(configurer ->
				// 自定义登录API
				configurer.logoutUrl("/api/v1/auth/logout")
						// 自定义登录处理器
						.addLogoutHandler(null)
						// 登出成功操作
						.logoutSuccessHandler(
								(request, response, authentication) -> SecurityContextHolder.clearContext()))
				// 认证用户时用户信息加载配置
				.userDetailsService(userService).build();
	}

	/**
	 * 相当于{@link WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.WebSecurity)}
	 */
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/ignore1", "/ignore2");
	}

	/**
	 * 配置跨源访问(CORS)
	 */
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		// 允许跨域的站点
		corsConfiguration.addAllowedOrigin("*");
		// 允许跨域的请求头
		corsConfiguration.addAllowedHeader("*");
		// 允许跨域的请求类型
		corsConfiguration.addAllowedMethod("*");
		// 允许携带凭证
		corsConfiguration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

	/**
	 * LDAP相关配置,需要引入ldap相关jar
	 * 
	 * @return
	 */
	// @Bean
	// public EmbeddedLdapServerContextSourceFactoryBean contextSourceFactoryBean()
	// {
	// EmbeddedLdapServerContextSourceFactoryBean contextSourceFactoryBean =
	// EmbeddedLdapServerContextSourceFactoryBean.fromEmbeddedLdapServer();
	// contextSourceFactoryBean.setPort(0);
	// return contextSourceFactoryBean;
	// }
	//
	// @Bean
	// AuthenticationManager ldapAuthenticationManager(BaseLdapPathContextSource
	// contextSource) {
	// LdapBindAuthenticationManagerFactory factory = new
	// LdapBindAuthenticationManagerFactory(contextSource);
	// factory.setUserDnPatterns("uid={0},ou=people");
	// factory.setUserDetailsContextMapper(new PersonContextMapper());
	// return factory.createAuthenticationManager();
	// }
}