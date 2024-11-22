package com.wy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.wy.context.RedisSecurityContextRepository;
import com.wy.helpers.SecurityOAuth2Helpers;
import com.wy.properties.OAuthServerSecurityProperties;
import com.wy.provider.captcha.CaptchaAuthenticationFilter;

import dream.flying.flower.framework.security.entrypoint.LoginRedirectAuthenticationEntryPoint;
import dream.flying.flower.framework.security.handler.LoginFailureHandler;
import dream.flying.flower.framework.security.handler.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;

/**
 * SpringSecurity5.8.14安全配置,可以和{@link AuthorizationServerConfig}放在一个类中
 * 
 * @author 飞花梦影
 * @date 2024-09-18 22:02:11
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class SecurityConfig {

	/**
	 * 登录地址,前后端分离就填写完整的url路径,不分离填写相对路径
	 */
	private final String LOGIN_URL = "http://127.0.0.1:5173";

	private final RedisSecurityContextRepository redisSecurityContextRepository;

	private final OAuthServerSecurityProperties oauthServerSecurityProperties;

	/**
	 * 配置密码解析器,注意重复注入
	 *
	 * @return BCryptPasswordEncoder
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 配置资源相关的过滤器链,不能和上面的{@link #authorizationServerSecurityFilterChain()}放一起,会有冲突
	 * 
	 * 用于身份验证的SpringSecurity过滤器链,用于处理身份验证相关的请求和响应.负责验证用户的身份,并生成相应的凭据,以便后续的授权和访问控制
	 *
	 * @param http security核心配置类
	 * @return 过滤器链
	 * @throws Exception 抛出
	 */
	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		// 禁用 csrf 与 cors
		http.csrf(AbstractHttpConfigurer::disable);
		http.cors(AbstractHttpConfigurer::disable);

		// 添加跨域过滤器
		http.addFilter(corsFilter());
		// 在UsernamePasswordAuthenticationFilter拦截器之前添加验证码校验拦截器，并拦截POST的登录接口
		http.addFilterBefore(new CaptchaAuthenticationFilter("/login"), UsernamePasswordAuthenticationFilter.class);

		http.authorizeHttpRequests((authorize) -> authorize
				// 放行静态资源
				.requestMatchers("/assets/**", "/webjars/**", "/login", "/getCaptcha", "/getSmsCaptcha")
				.permitAll()
				// 认证相关
				.requestMatchers("/oauth2/*")
				.permitAll()
				.requestMatchers("/introspect/*")
				.permitAll()
				.requestMatchers("/issuer/*")
				.permitAll()
				// 放行swagger相关资源
				.requestMatchers("/authenticate", "/oauth/authorize", "/swagger-resources/**", "/swagger-ui/**",
						"/v3/api-docs", "/webjars/**")
				.permitAll()
				.requestMatchers(oauthServerSecurityProperties.getPermitAllSources())
				.permitAll()
				// 其他请求都要认证
				.anyRequest()
				.authenticated());

		// 指定登录相关参数
		http.formLogin(formLogin -> {
			formLogin
					// 登录页页面
					.loginPage("/login")
					// 登录API
					.loginProcessingUrl("/login")
					// 登录成功转发地址,如果使用了自定义登录方式,需要处理刷新时的认证流程
					.successForwardUrl(LOGIN_URL);
			if (UrlUtils.isAbsoluteUrl(LOGIN_URL)) {
				// 绝对路径代表是前后端分离,登录成功和失败改为写回json,不重定向了
				formLogin.successHandler(new LoginSuccessHandler());
				formLogin.failureHandler(new LoginFailureHandler());
			}
		});

		// 指定登出相关参数
		http.logout(logout -> {
			logout
					// 指定登出的URL
					.logoutUrl("/logout")
					// 登出成功后的重定向URL
					.logoutSuccessUrl("/login?logout")
					// 登出时使http会话失效
					.invalidateHttpSession(true)
					// 登出时删除指定cookie
					.deleteCookies("JESSIONID");
		});

		// 添加BearerTokenAuthenticationFilter,将认证服务当做一个资源服务,解析请求头中的token
		// 资源服务器配置,处理使用access_token访问用户信息端点和客户端注册端点
		http.oauth2ResourceServer((resourceServer) -> resourceServer
				// 可自定义JWT设置
				.jwt(Customizer.withDefaults())
				// 权限不足时的异常处理
				.accessDeniedHandler(SecurityOAuth2Helpers::exceptionHandler)
				// 未携带token的异常处理
				.authenticationEntryPoint(SecurityOAuth2Helpers::exceptionHandler));

		http
				// 当未登录时访问认证端点时重定向至login页面
				.exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
						new LoginRedirectAuthenticationEntryPoint(LOGIN_URL),
						new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));

		// 在HttpServletRequests之间的SecurityContextHolder上设置SecurityContext的管理.当使用WebSecurityConfifigurerAdapter时,将自动运用
		// 使用redis存储、读取登录的认证信息,有BUG
		http.securityContext(context -> context.securityContextRepository(redisSecurityContextRepository));

		// 将安全标头添加到响应
		http.headers(header -> header.xssProtection(null));

		// session管理
		http.sessionManagement(session -> session.disable());

		// 配置基于x509的认证
		http.x509(Customizer.withDefaults());

		// 记住我
		http.rememberMe(Customizer.withDefaults());

		// 允许配置请求缓存
		http.requestCache(Customizer.withDefaults());

		// 配置基于容器的预认证,此时认证由Servlet容器管理
		http.jee(Customizer.withDefaults());

		// 将HttpServletRequest方法与在其上找到的值集成到SecurityContext中,当使用WebSecurityConfifigurerAdapter时,将自动应用
		http.servletApi(Customizer.withDefaults());

		// 允许配置匿名用户的表示方法.当与WebSecurityConfifigurerAdapter结合使用时,将自动应用.默认情况下,匿名用户将使用
		http.anonymous(Customizer.withDefaults());

		// 根据OAuth或OpenID Connect 1.0类型配置用户身份验证,主要用于客户端向第三方认证服务器认证
		http.oauth2Login(Customizer.withDefaults());

		// 配置通道安全.为了使该配置有用,必须提供至少一个到所需信道的映射
		http.requiresChannel(Customizer.withDefaults());

		// 配置 Http Basic 验证
		http.httpBasic(Customizer.withDefaults());

		// 在指定的Filter类的位置添加过滤器
		http.addFilterAt(null, null);

		return http.build();
	}

	/**
	 * 跨域过滤器配置
	 *
	 * @return CorsFilter
	 */
	@Bean
	CorsFilter corsFilter() {
		// 初始化cors配置对象
		CorsConfiguration configuration = new CorsConfiguration();
		// 设置跨域访问可以携带cookie
		configuration.setAllowCredentials(true);
		// 设置允许跨域的域名,如果允许携带cookie的话,路径就不能写*号, *表示所有的域名都可以跨域访问
		configuration.addAllowedOrigin("http://127.0.0.1:8080");
		// configuration.setAllowedOriginPatterns(Collections.singletonList(CorsConfiguration.ALL));
		// 允许所有的请求方法 ==> GET POST PUT Delete
		configuration.addAllowedMethod(CorsConfiguration.ALL);
		// 允许携带任何头信息
		configuration.addAllowedHeader(CorsConfiguration.ALL);
		// 初始化cors配置源对象
		UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
		// 给配置源对象设置过滤的参数
		// 参数一: 过滤的路径 == > 所有的路径都要求校验是否跨域
		// 参数二: 配置类
		configurationSource.registerCorsConfiguration("/**", configuration);
		// 返回配置好的过滤器
		return new CorsFilter(configurationSource);
	}

	/**
	 * 内存中注入UserDetailsService,主要进行用户身份验证,注意重复注入
	 * 
	 * UserDetailsService的实例,用于获取需要进行身份验证的用户信息,提供了与用户相关的数据,以便进行身份验证和授权的决策
	 * 
	 * @return UserDetailsService
	 */
	@Bean
	UserDetailsService userDetailsService() {
		UserDetails userDetails = User.builder()
				// 若需要加密,则使用加密
				.passwordEncoder(passwordEncoder()::encode)
				.username("username")
				// 加密后的密码
				.password("123456")
				// noop表示不加密
				.password("{noop}123456")
				.roles("USRE")
				.build();

		return new InMemoryUserDetailsManager(userDetails);
	}
}