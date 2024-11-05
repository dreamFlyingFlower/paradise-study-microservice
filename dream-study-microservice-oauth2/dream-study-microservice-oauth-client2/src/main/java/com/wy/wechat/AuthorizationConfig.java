package com.wy.wechat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.wy.context.RedisSecurityContextRepository;

import dream.flying.flower.framework.security.entrypoint.LoginAuthenticationEntryPoint;
import dream.flying.flower.framework.security.entrypoint.LoginRedirectAuthenticationEntryPoint;
import dream.flying.flower.framework.security.handler.CustomizerAccessDeniedHandler;
import dream.flying.flower.framework.security.handler.CustomizerAuthenticationFailureHandler;
import dream.flying.flower.framework.security.handler.CustomizerAuthenticationSuccessHandler;

/**
 * 配置认证相关信息,需要在客户端和资源服务器都配置
 *
 * @author 飞花梦影
 * @date 2024-11-04 09:42:58
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class AuthorizationConfig {

	private RedisSecurityContextRepository redisSecurityContextRepository;

	/**
	 * 登录地址,前后端分离就填写完整的url路径,不分离填写相对路径
	 */
	private final String LOGIN_URL = "http://127.0.0.1:5173";

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
	 * 配置认证相关的过滤器链(资源服务,客户端配置)
	 *
	 * @param http spring security核心配置类
	 * @return 过滤器链
	 * @throws Exception 抛出
	 */
	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
			ClientRegistrationRepository clientRegistrationRepository) throws Exception {
		// 添加跨域过滤器
		http.addFilter(corsFilter());
		// 禁用 csrf 与 cors
		http.csrf(AbstractHttpConfigurer::disable);
		http.cors(AbstractHttpConfigurer::disable);
		http.authorizeHttpRequests((authorize) -> authorize
				// 放行静态资源
				.requestMatchers("/assets/**", "/webjars/**", "/login", "/getCaptcha", "/getSmsCaptcha", "/error")
				.permitAll()
				.anyRequest()
				.authenticated())
				// 指定登录页面
				.formLogin(formLogin -> {
					formLogin.loginPage("/login");
					if (UrlUtils.isAbsoluteUrl(LOGIN_URL)) {
						// 绝对路径代表是前后端分离,登录成功和失败改为写回json,不重定向了
						formLogin.successHandler(new CustomizerAuthenticationSuccessHandler());
						formLogin.failureHandler(new CustomizerAuthenticationFailureHandler());
					}
				});
		// 添加BearerTokenAuthenticationFilter,将认证服务当做一个资源服务,解析请求头中的token
		http.oauth2ResourceServer((resourceServer) -> resourceServer.jwt(Customizer.withDefaults())
				.accessDeniedHandler(new CustomizerAccessDeniedHandler())
				.authenticationEntryPoint(new LoginAuthenticationEntryPoint(LOGIN_URL)));
		// 兼容前后端分离与不分离配置
		if (UrlUtils.isAbsoluteUrl(LOGIN_URL)) {
			http
					// 当未登录时访问认证端点时重定向至login页面
					.exceptionHandling((exceptions) -> exceptions.defaultAuthenticationEntryPointFor(
							new LoginRedirectAuthenticationEntryPoint(LOGIN_URL),
							new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));
		}
		// 联合身份认证
		http.oauth2Login(oauth2Login -> oauth2Login.loginPage(LOGIN_URL)
				.authorizationEndpoint(authorization -> authorization
						.authorizationRequestResolver(this.authorizationRequestResolver(clientRegistrationRepository)))
				.tokenEndpoint(token -> token.accessTokenResponseClient(this.accessTokenResponseClient())));

		// 使用redis存储、读取登录的认证信息
		http.securityContext(context -> context.securityContextRepository(redisSecurityContextRepository));

		return http.build();
	}

	/**
	 * AuthorizationRequest 自定义配置
	 *
	 * @param clientRegistrationRepository yml配置中客户端信息存储类
	 * @return OAuth2AuthorizationRequestResolver
	 */
	private OAuth2AuthorizationRequestResolver
			authorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
		DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver =
				new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");

		// 兼容微信登录授权申请
		authorizationRequestResolver.setAuthorizationRequestCustomizer(new WechatAuthorizationRequestConsumer());

		return authorizationRequestResolver;
	}

	/**
	 * 适配微信登录适配,添加自定义请求token入参处理
	 *
	 * @return OAuth2AccessTokenResponseClient accessToken响应信息处理
	 */
	private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
		DefaultAuthorizationCodeTokenResponseClient tokenResponseClient =
				new DefaultAuthorizationCodeTokenResponseClient();
		tokenResponseClient.setRequestEntityConverter(new WechatCodeGrantRequestEntityConverter());
		// 自定义 RestTemplate,适配微信登录获取token
		OAuth2AccessTokenResponseHttpMessageConverter messageConverter =
				new OAuth2AccessTokenResponseHttpMessageConverter();
		List<MediaType> mediaTypes = new ArrayList<>(messageConverter.getSupportedMediaTypes());
		// 微信获取token时响应的类型为“text/plain”,这里特殊处理一下
		mediaTypes.add(MediaType.TEXT_PLAIN);
		messageConverter.setAccessTokenResponseConverter(new WechatMapAccessTokenResponseConverter());
		messageConverter.setSupportedMediaTypes(mediaTypes);

		// 初始化RestTemplate
		RestTemplate restTemplate = new RestTemplate(Arrays.asList(new FormHttpMessageConverter(), messageConverter));

		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
		tokenResponseClient.setRestOperations(restTemplate);
		return tokenResponseClient;
	}
}