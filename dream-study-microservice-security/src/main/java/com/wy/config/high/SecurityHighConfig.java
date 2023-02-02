package com.wy.config.high;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.wy.jwt.JwtTokenFilter;
import com.wy.security.LoginAuthEntryPoint;
import com.wy.service.UserService;

/**
 * SpringSecurity5.7以上配置,WebSecurityConfigurerAdapter在该版本中已废弃
 *
 * @author 飞花梦影
 * @date 2023-02-01 16:51:06
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityHighConfig {

	@Autowired
	private UserService userService;

	/**
	 * jwt 校验过滤器,从 http 头部 Authorization 字段读取 token 并校验
	 */
	@Bean
	public JwtTokenFilter jwtTokenFilter() {
		return new JwtTokenFilter();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 获取AuthenticationManager(认证管理器),登录时认证使用
	 * 
	 * @param authenticationConfiguration
	 * @return
	 * @throws Exception
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	/**
	 * 使用自定义的基于数据库与JWT的认证方案
	 * 
	 * @param httpSecurity
	 * @return
	 * @throws Exception
	 */
	@Bean
	AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
		AuthenticationManager authenticationManager = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(userService).passwordEncoder(passwordEncoder()).and().build();
		return authenticationManager;
	}

	/**
	 * 通过创建 SecurityFilterChain 来配置 HttpSecurity,相当于
	 * {@link WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)}
	 * 
	 * @param http HttpSecurity
	 * @throws Exception
	 */
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authz) -> authz.anyRequest().authenticated())
				// 自定义认证处理器,默认为 ProviderManager
				.authenticationManager(new SelfAuthenticationManager());
		return http.csrf().disable()
				// 基于 token,不需要 session
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				// 设置 jwtAuthError 处理认证失败,鉴权失败
				.exceptionHandling().authenticationEntryPoint(new LoginAuthEntryPoint(null))
				// 自定义权限错误处理器
				.accessDeniedHandler(new AccessDeniedHandlerImpl()).and()
				// 下面开始设置权限
				.authorizeRequests(authorize -> authorize.antMatchers("/**").permitAll().antMatchers("/**").permitAll()
						.anyRequest().authenticated())
				// 添加 JWT 过滤器,JWT 过滤器在用户名密码认证过滤器之前
				.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
				// 认证用户时用户信息加载配置
				.userDetailsService(userService).build();
	}

	/**
	 * 相当于{@link WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.WebSecurity)}
	 */
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().antMatchers("/ignore1", "/ignore2");
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
}