package com.wy.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import com.wy.context.RedisSecurityContextRepository;
import com.wy.provider.sms.SmsGrantAuthenticationConverter;
import com.wy.provider.sms.SmsGrantAuthenticationProvider;

import dream.flying.flower.framework.security.constant.ConstAuthorization;
import dream.flying.flower.framework.security.entrypoint.LoginRedirectAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * SpringSecurity5.8.14认证服务器配置
 * 
 * <pre>
 * {@link EnableWebSecurity}:加载WebSecurityConfiguration,配置安全认证策略,加载AuthenticationConfiguration, 配置了认证信息
 * {@link EnableMethodSecurity}:开启全局方法认证,启用JSR250注解支持,启用注解 {@link Secured}支持
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2024-09-18 22:02:11
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class AuthorizationServerConfig {

	/**
	 * 登录地址,前后端分离就填写完整的url路径,不分离填写相对路径
	 */
	private final String LOGIN_URL = "http://127.0.0.1:5173";

	private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";

	private final RedisSecurityContextRepository redisSecurityContextRepository;

	/**
	 * 配置认证相关的端点过滤器链,用于处理与协议端点相关的请求和响应.
	 * 
	 * 负责处理OAuth2和OpenID Connect的协议细节,例如授权请求、令牌颁发和验证等
	 *
	 * @param http security核心配置类
	 * @return 过滤器链
	 * @throws Exception
	 */
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
			RegisteredClientRepository registeredClientRepository,
			AuthorizationServerSettings authorizationServerSettings) throws Exception {
		// 默认配置,忽略认证端点的csrf校验.如果要整合OAuth2,则需要当前方式注入相关对象
		// 将OAuth2AuthorizationServerConfigurer配置应用到HttpSecurity中
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

		// 新建设备码converter和provider
		// DeviceClientAuthenticationConverter deviceClientAuthenticationConverter =
		// new
		// DeviceClientAuthenticationConverter(authorizationServerSettings.getDeviceAuthorizationEndpoint());
		// DeviceClientAuthenticationProvider deviceClientAuthenticationProvider =
		// new DeviceClientAuthenticationProvider(registeredClientRepository);

		// 使用redis存储、读取登录的认证信息
		http.securityContext(context -> context.securityContextRepository(redisSecurityContextRepository));

		// 获得第一步应用的OAuth2AuthorizationServerConfigurer
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
				// 开启OpenID Connect 1.0协议相关端点
				.oidc(Customizer.withDefaults())
				// 设置自定义用户确认授权页
				.authorizationEndpoint(
						authorizationEndpoint -> authorizationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI))
		// 设置设备码用户验证url(自定义用户验证页)
		// .deviceAuthorizationEndpoint(
		// deviceAuthorizationEndpoint ->
		// deviceAuthorizationEndpoint.verificationUri("/activate"))
		// 设置验证设备码用户确认页面
		// .deviceVerificationEndpoint(
		// deviceVerificationEndpoint ->
		// deviceVerificationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI))
		// .clientAuthentication(clientAuthentication ->
		// // 客户端认证添加设备码的converter和provider
		// clientAuthentication.authenticationConverter(deviceClientAuthenticationConverter)
		// .authenticationProvider(deviceClientAuthenticationProvider))
		;
		http
				// 当未登录时访问认证端点时重定向至login页面
				// .exceptionHandling((exceptions) -> exceptions
				// .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
				// 当未登录时访问认证端点时重定向至login页面,同时指定请求类型
				.exceptionHandling((exceptions) -> exceptions.defaultAuthenticationEntryPointFor(
						new LoginRedirectAuthenticationEntryPoint(LOGIN_URL),
						new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));

		// 使用JWT处理令牌用于用户信息和/或客户端注册
		http.oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()));

		// 自定义短信认证登录转换器
		SmsGrantAuthenticationConverter converter = new SmsGrantAuthenticationConverter();
		// 自定义短信认证登录认证提供
		SmsGrantAuthenticationProvider provider = new SmsGrantAuthenticationProvider();
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
				// 让认证服务器元数据中有自定义的认证方式
				.authorizationServerMetadataEndpoint(metadata -> metadata.authorizationServerMetadataCustomizer(
						customizer -> customizer.grantType(ConstAuthorization.GRANT_TYPE_SMS_CODE)))
				// 添加自定义grant_type——短信认证登录
				.tokenEndpoint(tokenEndpoint -> tokenEndpoint.accessTokenRequestConverter(converter)
						.authenticationProvider(provider));

		DefaultSecurityFilterChain build = http.build();

		// 从框架中获取provider中所需的bean
		OAuth2TokenGenerator<?> tokenGenerator = http.getSharedObject(OAuth2TokenGenerator.class);
		AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
		OAuth2AuthorizationService authorizationService = http.getSharedObject(OAuth2AuthorizationService.class);
		// 以上三个bean在build()方法之后调用是因为调用build方法时框架会尝试获取这些类,
		// 如果获取不到则初始化一个实例放入SharedObject中,所以要在build方法调用之后获取
		// 在通过set方法设置进provider中,但是如果在build方法之后调用authenticationProvider(provider)
		// 框架会提示unsupported_grant_type,因为已经初始化完了,在添加就不会生效了
		provider.setTokenGenerator(tokenGenerator);
		provider.setAuthorizationService(authorizationService);
		provider.setAuthenticationManager(authenticationManager);

		return build;
	}

	/**
	 * 自定义jwt解析器,设置解析出来的权限信息的前缀与在jwt中的key
	 *
	 * @return jwt解析器 JwtAuthenticationConverter
	 */
	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		// 设置解析权限(scope)信息的前缀,设置为空是去掉前缀,如果不去掉,则scope参数从client传递时需带上SCOPE_
		grantedAuthoritiesConverter.setAuthorityPrefix("");
		// 设置权限信息在jwt claims中的key
		grantedAuthoritiesConverter.setAuthoritiesClaimName(ConstAuthorization.AUTHORITIES_KEY);

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	/**
	 * 将AuthenticationManager注入ioc中,其它需要使用地方可以直接从ioc中获取
	 *
	 * @param authenticationConfiguration 导出认证配置
	 * @return AuthenticationManager 认证管理器
	 */
	@Bean
	@SneakyThrows
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
		return authenticationConfiguration.getAuthenticationManager();
	}

	/**
	 * 配置基于db的oauth2的授权管理服务,对应表oauth2_authorization
	 *
	 * @param jdbcTemplate db数据源信息
	 * @param registeredClientRepository 上边注入的客户端repository
	 * @return JdbcOAuth2AuthorizationService
	 */
	@Bean
	OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate,
			RegisteredClientRepository registeredClientRepository) {
		// 基于db的oauth2认证服务,基于内存的服务实现InMemoryOAuth2AuthorizationService
		return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
	}

	/**
	 * 配置基于db的授权确认管理服务,对应表oauth2_authorization_consent
	 *
	 * @param jdbcTemplate db数据源信息
	 * @param registeredClientRepository 客户端repository
	 * @return JdbcOAuth2AuthorizationConsentService
	 */
	@Bean
	OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate,
			RegisteredClientRepository registeredClientRepository) {
		// 基于db的授权确认管理服务,基于内存的服务实现InMemoryOAuth2AuthorizationConsentService
		return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
	}

	/**
	 * 添加认证服务器配置,设置jwt签发者、默认端点请求地址等
	 * 
	 * 配置Authorization Server的一些全局设置,例如令牌的有效期、刷新令牌的策略和认证页面的URL等,提供了对授权服务器行为的细粒度控制
	 *
	 * @return AuthorizationServerSettings
	 */
	@Bean
	AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder()
				/*
				 * 设置token签发地址(http(s)://{ip}:{port}/context-path,
				 * http(s)://domain.com/context-path)
				 * 如果需要通过ip访问这里就是ip,如果是有域名映射就填域名,通过什么方式访问该服务这里就填什么
				 */
				// .issuer("http://127.0.0.1:8080")
				.build();
	}

	/**
	 * Opaque方式向token中自定义存数据,自定义的数据可以从Authentication中获取
	 * 
	 * {@link SecurityContextHolder#getContext()}->{@link SecurityContext#getAuthentication()}
	 * 
	 * @return OAuth2TokenCustomizer
	 */
	@Bean
	OAuth2TokenCustomizer<OAuth2TokenClaimsContext> tokenCustomizer() {
		return context -> {

			OAuth2TokenClaimsSet.Builder claims = context.getClaims();
			// 将权限信息或其他信息放入jwt的claims中,可以从context中拿到client_id
			claims.claim(ConstAuthorization.AUTHORITIES_KEY, "自定义参数");
		};
	}

	/**
	 * JWT方式向token中自定义存数据,自定义的数据可以从Authentication中获取
	 * 
	 * {@link SecurityContextHolder#getContext()}->{@link SecurityContext#getAuthentication()}
	 * 
	 * @return OAuth2TokenCustomizer
	 */
	@Bean
	OAuth2TokenCustomizer<JwtEncodingContext> oauth2TokenCustomizer() {
		return context -> {
			// 检查登录用户信息是不是UserDetails,排除掉没有用户参与的流程
			if (context.getPrincipal().getPrincipal() instanceof UserDetails) {
				UserDetails user = (UserDetails) context.getPrincipal().getPrincipal();
				// 获取申请的scopes
				Set<String> scopes = context.getAuthorizedScopes();
				// 获取用户的权限
				Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
				// 提取权限并转为字符串
				Set<String> authoritySet = Optional.ofNullable(authorities)
						.orElse(Collections.emptyList())
						.stream()
						// 获取权限字符串
						.map(GrantedAuthority::getAuthority)
						// 去重
						.collect(Collectors.toSet());

				// 合并scope与用户信息
				authoritySet.addAll(scopes);

				JwtClaimsSet.Builder claims = context.getClaims();
				// 将权限信息放入jwt的claims中,也可以生成一个以指定字符分割的字符串放入
				claims.claim(ConstAuthorization.AUTHORITIES_KEY, authoritySet);
				// 放入其它自定内容
				// 角色、头像...
			}
		};
	}
}