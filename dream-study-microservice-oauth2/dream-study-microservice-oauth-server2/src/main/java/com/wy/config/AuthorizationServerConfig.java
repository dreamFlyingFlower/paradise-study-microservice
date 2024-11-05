package com.wy.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

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
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import com.wy.context.RedisSecurityContextRepository;
import com.wy.grant.SmsAuthenticationConverter;
import com.wy.grant.SmsAuthenticationProvider;
import com.wy.service.UserService;

import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.framework.security.constant.ConstAuthorization;
import dream.flying.flower.framework.security.entrypoint.LoginRedirectAuthenticationEntryPoint;
import dream.flying.flower.framework.security.handler.CustomizerAuthenticationFailureHandler;
import dream.flying.flower.framework.security.handler.CustomizerAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * SpringSecurity5.8.14认证服务器配置
 * 
 * <pre>
 * {@link EnableWebSecurity}:加载{@link WebSecurityConfiguration},配置安全认证策略,加载{@link AuthenticationConfiguration},配置认证信息
 * {@link EnableMethodSecurity}:开启全局方法校验
 * ->{@link EnableMethodSecurity#jsr250Enabled()}:为true启用JSR250注解支持,如{@link RolesAllowed},{@link PermitAll}和{@link DenyAll}等注解
 * ->{@link EnableMethodSecurity#securedEnabled()}:为true启用{@link Secured}注解支持
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

	private final UserService userService;

	/**
	 * 配置认证相关的端点过滤器链,用于处理与协议端点相关的请求和响应
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

		// 自定义用户映射器,该方式会改变客户端调用/userinfo接口的数据
		Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper = (context) -> {
			OidcUserInfoAuthenticationToken authentication = context.getAuthentication();
			JwtAuthenticationToken principal = (JwtAuthenticationToken) authentication.getPrincipal();
			return new OidcUserInfo(JsonHelpers.parseMap(userService.loadUserByUsername(principal.getName())));
		};

		// 获得第一步应用的OAuth2AuthorizationServerConfigurer
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
				// 开启OpenID Connect 1.0协议相关端点
				.oidc(Customizer.withDefaults())
				// 开启OpenID Connect 1.0协议相关端点,并使用自定义的UserInfo映射器
				.oidc((oidc) -> {
					oidc.userInfoEndpoint((userInfo) -> {
						userInfo.userInfoMapper(userInfoMapper);
						userInfo.userInfoResponseHandler(new CustomizerAuthenticationSuccessHandler());
					});
				})
				.authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
						// 设置自定义用户确认授权页
						.consentPage(CUSTOM_CONSENT_PAGE_URI))
				// 设置客户端授权中失败的handler处理
				.clientAuthentication(auth -> auth.errorResponseHandler(new CustomizerAuthenticationFailureHandler()))
				// token 相关配置,如/oauth2/token接口
				.tokenEndpoint(token -> token.errorResponseHandler(new CustomizerAuthenticationFailureHandler()))

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
						// 从浏览器发出的请求肯定会带accept:text/html请求头,根据mediaType,可以用来判断请求是否来自浏览器,只有浏览器请求重定向到登录页面,其他异常返回json
						new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));

		// 使用JWT处理令牌用于用户信息和/或客户端注册,同时将认证服务器做为一个资源服务器
		http.oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()));

		// 添加自定义短信认证登录转换器
		SmsAuthenticationConverter converter = new SmsAuthenticationConverter();
		// 添加自定义短信认证登录认证提供
		SmsAuthenticationProvider provider = new SmsAuthenticationProvider();
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
				// 让认证服务器元数据中有自定义的认证方式,这样访问/.well-known/oauth-authorization-server时返回的元数据中有自定义的grant_type
				.authorizationServerMetadataEndpoint(metadata -> metadata.authorizationServerMetadataCustomizer(
						customizer -> customizer.grantType(ConstAuthorization.GRANT_TYPE_SMS_CODE)))
				// 添加自定义grant_type-短信认证登录
				.tokenEndpoint(tokenEndpoint -> tokenEndpoint.accessTokenRequestConverter(converter)
						.authenticationProvider(provider)
						// 自定义access_token响应,削减access_token的长度
						.accessTokenResponseHandler(null));

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
	 * 若客户端和资源服务器请求认证服务器携带的token中的iss不对,同样会认证失败
	 *
	 * @return AuthorizationServerSettings
	 */
	@Bean
	AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder()
				// 设置token签发的完整地址(http(s)://{ip}:{port}/context-path,如果需要通过ip访问就写ip,如果是域名就填域名
				.issuer("http://127.0.0.1:17127")
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
	 * 自定义jwt,将权限信息放至jwt中.联合身份认证自定义token处理,当使用openId Connect登录时将用户信息写入idToken中
	 * 
	 * @return OAuth2TokenCustomizer的实例
	 */
	@Bean
	OAuth2TokenCustomizer<JwtEncodingContext> oAuth2TokenCustomizer() {
		return new FederatedIdentityIdTokenCustomizer();
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
			// 根据token类型添加信息
			OAuth2TokenType tokenType = context.getTokenType();
			System.out.println(context.getTokenType());
			if (tokenType.getValue().equals("id_token")) {
				context.getClaims().claim("Test", "Test Id Token");
			}
			if (tokenType.getValue().equals("access_token")) {
				context.getClaims().claim("Test", "Test Access Token");
				Set<String> authorities = context.getPrincipal()
						.getAuthorities()
						.stream()
						.map(GrantedAuthority::getAuthority)
						.collect(Collectors.toSet());
				context.getClaims().claim("authorities", authorities).claim("user", context.getPrincipal().getName());
			}

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
				// 放入其它自定内容.如角色、头像...
			}
		};
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
}