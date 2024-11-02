package com.wy.provider.device;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

/**
 * 设备授权码认证服务器配置
 * 
 * 设备码流程一般使用在不便输入的设备上,设备提供一个链接给用户验证,用户在其它设备的浏览器中认证
 * 
 * <pre>
 * 1.用户请求/oauth2/device_authorization接口,获取user_code、设备码和给用户在浏览器访问的地址
 * 2.用户在浏览器打开地址,输入user_code,如果用户尚未登录则需要进行登录
 * 3.输入user_code之后如果该客户端当前用户尚未授权则重定向至授权确认页面
 * 4.授权完成后设备通过设备码换取token,设备一般是在给出用户验证地址后轮训携带设备码访问/oauth2/token接口,如果用户尚未验证时访问则会响应authorization_pending
 * 
 * POST(/oauth2/device_authorization):/oauth2/device_authorization?client_id=&scope
 * 响应:
 * 		user_code: 用户在浏览器打开验证地址时输入的内容
 * 		device_code: 设备码,用该值换取token
 * 		verification_uri_complete: 用户在浏览器打开的验证地址,页面会自动获取参数并提交表单
 * 		verification_uri: 验证地址,需要用户输入user_code
 * 		expires_in: 过期时间,单位秒
 * 
 * GET(verification_uri)/GET(verification_uri_complete):在页面访问其中一个地址,如果未登录,需要先登录
 * 
 * GET(activate):请求该接口,重定向到指定页面,输入user_code并提交
 * 
 * GET(/oauth2/consent):重定向到授权页面
 * 
 * GET(/success):授权成功后跳转到成功页面
 * 
 * POST(/oauth2/token):设备发起请求用设备码换取token
 * 请求参数:
 * 		client_id: 客户端id
 * 		device_code: 请求/oauth2/device_authorization接口返回的设备码(device_code)
 * 		grant_type: urn:ietf:params:oauth:grant-type:device_code(固定的,参照{@link AuthorizationGrantType#DEVICE_CODE}
 * </pre>
 *
 * @author 飞花梦影
 * @date 2024-11-02 10:34:18
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class AuthorizationServerConfig {

	private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";

	/**
	 * 配置端点的过滤器链
	 *
	 * @param http spring security核心配置类
	 * @return 过滤器链
	 * @throws Exception 抛出
	 */
	@Bean
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
			RegisteredClientRepository registeredClientRepository,
			AuthorizationServerSettings authorizationServerSettings) throws Exception {
		// 配置默认的设置,忽略认证端点的csrf校验
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

		// 新建设备码converter和provider
		DeviceClientAuthenticationConverter deviceClientAuthenticationConverter =
				new DeviceClientAuthenticationConverter(authorizationServerSettings.getDeviceAuthorizationEndpoint());
		DeviceClientAuthenticationProvider deviceClientAuthenticationProvider =
				new DeviceClientAuthenticationProvider(registeredClientRepository);

		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
				// 开启OpenID Connect 1.0协议相关端点
				.oidc(Customizer.withDefaults())
				// 设置自定义用户确认授权页
				.authorizationEndpoint(
						authorizationEndpoint -> authorizationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI))
				// 设置设备码用户验证url(自定义用户验证页)
				.deviceAuthorizationEndpoint(
						deviceAuthorizationEndpoint -> deviceAuthorizationEndpoint.verificationUri("/activate"))
				// 设置验证设备码用户确认页面
				.deviceVerificationEndpoint(
						deviceVerificationEndpoint -> deviceVerificationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI))
				.clientAuthentication(clientAuthentication ->
				// 客户端认证添加设备码的converter和provider
				clientAuthentication.authenticationConverter(deviceClientAuthenticationConverter)
						.authenticationProvider(deviceClientAuthenticationProvider));
		http
				// 当未登录时访问认证端点时重定向至login页面
				.exceptionHandling((exceptions) -> exceptions.defaultAuthenticationEntryPointFor(
						new LoginUrlAuthenticationEntryPoint("/login"),
						new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
				// 处理使用access token访问用户信息端点和客户端注册端点
				.oauth2ResourceServer((resourceServer) -> resourceServer.jwt(Customizer.withDefaults()));

		return http.build();
	}

	/**
	 * 配置认证相关的过滤器链
	 *
	 * @param http spring security核心配置类
	 * @return 过滤器链
	 * @throws Exception 抛出
	 */
	@Bean
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authorize) -> authorize
				// 放行静态资源
				.requestMatchers("/assets/**", "/webjars/**", "/login")
				.permitAll()
				.anyRequest()
				.authenticated())
				// 指定登录页面
				.formLogin(formLogin -> formLogin.loginPage("/login"));
		// 添加BearerTokenAuthenticationFilter,将认证服务当做一个资源服务,解析请求头中的token
		http.oauth2ResourceServer((resourceServer) -> resourceServer.jwt(Customizer.withDefaults()));

		return http.build();
	}

	/**
	 * 配置密码解析器,使用BCrypt的方式对密码进行加密和验证
	 *
	 * @return BCryptPasswordEncoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 配置客户端Repository
	 *
	 * @param jdbcTemplate db 数据源信息
	 * @param passwordEncoder 密码解析器
	 * @return 基于数据库的repository
	 */
	@Bean
	public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate,
			PasswordEncoder passwordEncoder) {
		RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
				// 客户端id
				.clientId("messaging-client")
				// 客户端秘钥,使用密码解析器加密
				.clientSecret(passwordEncoder.encode("123456"))
				// 客户端认证方式,基于请求头的认证
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				// 配置资源服务器使用该客户端获取授权时支持的方式
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				// 授权码模式回调地址,oauth2.1已改为精准匹配,不能只设置域名,并且屏蔽了localhost,本机使用127.0.0.1访问
				.redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
				.redirectUri("https://www.baidu.com")
				// 该客户端的授权范围,OPENID与PROFILE是IdToken的scope,获取授权时请求OPENID的scope时认证服务会返回IdToken
				.scope(OidcScopes.OPENID)
				.scope(OidcScopes.PROFILE)
				// 自定scope
				.scope("message.read")
				.scope("message.write")
				// 客户端设置,设置用户需要确认授权
				.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
				.build();

		// 基于db存储客户端,还有一个基于内存的实现 InMemoryRegisteredClientRepository
		JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);

		// 初始化客户端
		RegisteredClient repositoryByClientId =
				registeredClientRepository.findByClientId(registeredClient.getClientId());
		if (repositoryByClientId == null) {
			registeredClientRepository.save(registeredClient);
		}
		// 设备码授权客户端
		RegisteredClient deviceClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("device-message-client")
				// 公共客户端
				.clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
				// 设备码授权
				.authorizationGrantType(AuthorizationGrantType.DEVICE_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				// 自定scope
				.scope("message.read")
				.scope("message.write")
				.build();
		RegisteredClient byClientId = registeredClientRepository.findByClientId(deviceClient.getClientId());
		if (byClientId == null) {
			registeredClientRepository.save(deviceClient);
		}

		// PKCE客户端
		RegisteredClient pkceClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("pkce-message-client")
				// 公共客户端
				.clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
				// 授权码模式,因为是扩展授权码流程,所以流程还是授权码的流程,改变的只是参数
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				// 授权码模式回调地址,oauth2.1已改为精准匹配,不能只设置域名,并且屏蔽了localhost,本机使用127.0.0.1访问
				.redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
				.clientSettings(ClientSettings.builder().requireProofKey(Boolean.TRUE).build())
				// 自定scope
				.scope("message.read")
				.scope("message.write")
				.build();
		RegisteredClient findPkceClient = registeredClientRepository.findByClientId(pkceClient.getClientId());
		if (findPkceClient == null) {
			registeredClientRepository.save(pkceClient);
		}
		return registeredClientRepository;
	}

	/**
	 * 配置基于db的oauth2的授权管理服务
	 *
	 * @param jdbcTemplate db数据源信息
	 * @param registeredClientRepository 上边注入的客户端repository
	 * @return JdbcOAuth2AuthorizationService
	 */
	@Bean
	public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate,
			RegisteredClientRepository registeredClientRepository) {
		// 基于db的oauth2认证服务,还有一个基于内存的服务实现InMemoryOAuth2AuthorizationService
		return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
	}

	/**
	 * 配置基于db的授权确认管理服务
	 *
	 * @param jdbcTemplate db数据源信息
	 * @param registeredClientRepository 客户端repository
	 * @return JdbcOAuth2AuthorizationConsentService
	 */
	@Bean
	public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate,
			RegisteredClientRepository registeredClientRepository) {
		// 基于db的授权确认管理服务,还有一个基于内存的服务实现InMemoryOAuth2AuthorizationConsentService
		return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
	}

	/**
	 * 配置jwk源,使用非对称加密,公开用于检索匹配指定选择器的JWK的方法
	 *
	 * @return JWKSource
	 */
	@Bean
	public JWKSource<SecurityContext> jwkSource() {
		KeyPair keyPair = generateRsaKey();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		RSAKey rsaKey =
				new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return new ImmutableJWKSet<>(jwkSet);
	}

	/**
	 * 生成rsa密钥对,提供给jwk
	 *
	 * @return 密钥对
	 */
	private static KeyPair generateRsaKey() {
		KeyPair keyPair;
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return keyPair;
	}

	/**
	 * 配置jwt解析器
	 *
	 * @param jwkSource jwk源
	 * @return JwtDecoder
	 */
	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	/**
	 * 添加认证服务器配置,设置jwt签发者、默认端点请求地址等
	 *
	 * @return AuthorizationServerSettings
	 */
	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}

	/**
	 * 先暂时配置一个基于内存的用户,框架在用户认证时会默认调用
	 * {@link UserDetailsService#loadUserByUsername(String)} 方法根据
	 * 账号查询用户信息,一般是重写该方法实现自己的逻辑
	 *
	 * @param passwordEncoder 密码解析器
	 * @return UserDetailsService
	 */
	@Bean
	public UserDetailsService users(PasswordEncoder passwordEncoder) {
		UserDetails user = User.withUsername("admin")
				.password(passwordEncoder.encode("123456"))
				.roles("admin", "normal", "unAuthentication")
				.authorities("app", "web", "/test2", "/test3")
				.build();
		return new InMemoryUserDetailsManager(user);
	}
}