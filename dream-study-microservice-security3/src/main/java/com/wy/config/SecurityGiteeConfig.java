package com.wy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

/**
 * 用户通过本系统调用Gitee进行登录认证
 *
 * @author 飞花梦影
 * @date 2024-09-20 11:11:07
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityGiteeConfig {

	/**
	 * 配置内存客户端验证,默认就是内存
	 * 
	 * @return ClientRegistrationRepository
	 */
	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		return new InMemoryClientRegistrationRepository(this.giteeClientRegistration());
	}

	// 配置giee的授权登录信息
	private ClientRegistration giteeClientRegistration() {
		return ClientRegistration
				// 第三方认证服务器唯一标识,自定义,不可重复
				.withRegistrationId("gitee")
				// 第三方认证服务器发放给本系统的客户端ID和客户端密钥
				.clientId("clientId")
				.clientSecret("clientSecret")
				// 第三方认证服务器指定本系统的认证方式
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				// 第三方认证服务器指定本系统的授权模式
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				// 服务提供者回调本系统时的API地址
				.redirectUri("http://localhost:8080/login/oauth2/code/gitee")
				// 授权范围,即创建应用时勾选的权限名
				.scope("user_info")
				// 第三方认证服务器授权地址
				.authorizationUri("https://gitee.com/oauth/authorize")
				// 获取第三方认证服务器token地址
				.tokenUri("https://gitee.com/oauth/token")
				// 获取第三方认证服务器用户信息地址
				.userInfoUri("https://gitee.com/api/v5/user")
				.userNameAttributeName("name")
				.build();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(
						request -> request.requestMatchers("/api/auth/**").permitAll().anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
		// .addFilterBefore(jwtAthFilter, UsernamePasswordAuthenticationFilter.class);
		;
		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsService() {

			@Override
			public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
				return null;
			}
		};
	}
}