package com.wy.config;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import dream.flying.flower.ConstDigest;
import dream.flying.flower.digest.RsaHelper;
import dream.flying.flower.framework.security.constant.ConstOAuthGrantType;
import lombok.RequiredArgsConstructor;

/**
 * SpringSecurity6客户端认证配置
 * 
 * 项目启动时,会将不存在于oauth2_registered_client表中的客户端数据写入到该表中
 * 
 * @author 飞花梦影
 * @date 2024-09-18 22:02:11
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class AuthorizationClientConfig {

	/**
	 * 配置已经注册的客户端认证Repository,也可以直接使用配置文件,数据库对应表oauth2_registered_client
	 * 
	 * 用于管理OAuth2和OpenID Connect客户端的注册信息,包括客户端ID、密钥、授权类型和重定向URI等,负责验证客户端的身份并维护客户端的配置
	 * 
	 * 如果认证服务器开启了oidc,可调用该URL查询认证服务器信息:http://127.0.0.1:17127/.well-known/openid-configuration
	 * 
	 * 获取授权码:http://localhost:17127/oauth2/authorize?response_type=code&client_id=oidc-client&scope=profile&redirect_uri=http://www.baidu.com
	 * 
	 * @param jdbcTemplate db数据源信息
	 * @param passwordEncoder 密码解析器
	 * @return 基于数据库的repository
	 */
	@Bean
	RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
		RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
				// 客户端id
				.clientId("messaging-client")
				// 客户端秘钥,使用密码解析器加密
				.clientSecret(passwordEncoder.encode("123456"))
				// {noop}开头,表示密码以明文存储
				.clientSecret("{noop}123456")
				// 客户端认证方式:基于POST请求,从请求头获取参数的认证方式,处理类为 ClientSecretBasicAuthenticationConverter
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				// 客户端认证方式:基于POST请求,从请求参数中获取相关参数,处理类为 ClientSecretPostAuthenticationConverter
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
				// 配置资源服务器使用该客户端获取授权时支持的方式
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				// 客户端添加自定义认证
				.authorizationGrantType(ConstOAuthGrantType.SMS_CODE)
				// 授权码模式回调地址,oauth2.1已改为精准匹配,不能只设置域名,并且屏蔽了localhost,本机使用127.0.0.1访问
				.redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
				.redirectUri("https://www.baidu.com")
				// 该客户端的授权范围,OPENID与PROFILE是IdToken的scope,获取授权时请求OPENID的scope时认证服务会返回id_token
				.scope(OidcScopes.OPENID)
				.scope(OidcScopes.PROFILE)
				// 自定scope,客户端如果带了scope必须带上SCOPE_前缀,可以去掉,见AuthorizationServerConfig#jwtAuthenticationConverter()
				.scope("message.read")
				.scope("message.write")
				// 客户端设置
				.clientSettings(ClientSettings.builder()
						// 设置用户需要确认授权,若无感授权,改为false
						.requireAuthorizationConsent(true)
						// 当使用该客户端发起PKCE流程时必须设置为true
						.requireProofKey(false)
						// 设置客户端JWKS的URL
						.jwkSetUrl(null)
						// 设置token端点对验证方法为CLIENT_SECRET_JWT,PRIVATE_KEY_JWT的客户端进行身份验证使用的签名算法
						.tokenEndpointAuthenticationSigningAlgorithm(null)
						.build())
				// token配置
				.tokenSettings(TokenSettings.builder()
						// 授权码有效时长
						.authorizationCodeTimeToLive(Duration.ofSeconds(60))
						// access token 有效期
						.accessTokenTimeToLive(Duration.ofMinutes(60))
						// access_token格式,SELF_CONTAINED是jwt格式,REFERENCE是opaque格式,相当于token元数据的一个id,通过id找到对应数据(自省令牌时)
						// 不同格式需要http.oauth2ResourceServer()进行不同配置,见AuthorizationServerConfig关于oauth2ResourceServer的部分
						.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
						// refresh_token是否可以重用:true->可重用,refresh_token不变,可一直使用
						.reuseRefreshTokens(true)
						// refresh_token有效时长
						.refreshTokenTimeToLive(Duration.ofDays(7))
						// id_token的加密算法
						.idTokenSignatureAlgorithm(SignatureAlgorithm.RS256)
						// 设备码有效时长
						.deviceCodeTimeToLive(null)
						.build())
				.build();

		// 基于db存储客户端,基于内存的InMemoryRegisteredClientRepository(registeredClient);
		JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);

		// 初始化客户端:如果没有该客户端信息,则存入客户端
		RegisteredClient repositoryByClientId =
				registeredClientRepository.findByClientId(registeredClient.getClientId());
		if (repositoryByClientId == null) {
			registeredClientRepository.save(registeredClient);
		}

		// 设置多个客户端认证.设备码授权客户端,公共客户端
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

		// PKCE客户端(授权码扩展模式)
		RegisteredClient pkceClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("pkce-message-client")
				// 公共客户端
				.clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
				// 授权码模式,因为是扩展授权码流程,所以流程还是授权码的流程,改变的只是参数
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				// 授权码模式回调地址,oauth2.1已改为精准匹配,不能只设置域名,并且屏蔽了localhost,本机使用127.0.0.1访问
				.redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
				// 开启PKCE,需要先在网上生成一对类似密钥的CodeVerifier和CodeChallenge
				// CodeChallenge用于配置认证服务器的客户端配置,CodeVerifier用户客户端访问认证服务器获取token时携带,以便认证服务器进行校验
				// 客户端认证:/oauth2/authorize?response_type=code&client_id=pkce-message-client&redirect_uri=&scope=message.read&code_challenge=xxxx&code_challenge_method=S256
				// 客户端获取token:/oauth2/token?grant_type=&client_id=&redirect_uri=&code=&code_verifier=xxxxxxxx
				.clientSettings(ClientSettings.builder().requireProofKey(Boolean.TRUE).build())
				// 自定scope
				.scope("message.read")
				.scope("message.write")
				.build();
		RegisteredClient findPkceClient = registeredClientRepository.findByClientId(pkceClient.getClientId());
		if (findPkceClient == null) {
			registeredClientRepository.save(pkceClient);
		}

		// token交换客户端
		RegisteredClient tokenExchangeClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("guest-token-client")
				.clientSecret("{noop}123456")
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.TOKEN_EXCHANGE)
				.scope("message.read")
				.scope("message.write")
				.build();
		RegisteredClient tokenRegisteredClient =
				registeredClientRepository.findByClientId(tokenExchangeClient.getClientId());
		if (tokenRegisteredClient == null) {
			registeredClientRepository.save(tokenRegisteredClient);
		}

		// 长连接认证
		RegisteredClient mtlsDemoClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("guest-mtls-client")
				.clientAuthenticationMethod(ClientAuthenticationMethod.TLS_CLIENT_AUTH)
				.clientAuthenticationMethod(ClientAuthenticationMethod.SELF_SIGNED_TLS_CLIENT_AUTH)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.scope("message.read")
				.scope("message.write")
				.clientSettings(ClientSettings.builder()
						.x509CertificateSubjectDN("CN=demo-client-sample,OU=Spring Samples,O=Spring,C=US")
						.jwkSetUrl("http://127.0.0.1:8080/jwks")
						.build())
				.tokenSettings(TokenSettings.builder().x509CertificateBoundAccessTokens(true).build())
				.build();
		RegisteredClient mtlsRegisteredClient = registeredClientRepository.findByClientId(mtlsDemoClient.getClientId());
		if (mtlsRegisteredClient == null) {
			registeredClientRepository.save(mtlsRegisteredClient);
		}

		return registeredClientRepository;
	}

	/**
	 * 配置JWK源,使用非对称加密,为JWT(id_token)提供加密密钥,用于加密/解密或签名/验签.公开用于检索匹配指定选择器的JWK的方法,用于签名访问令牌
	 * 
	 * 用于生成和管理用于对访问令牌进行签名的JSON Web Key(JWK),提供了加密算法和密钥,以确保访问令牌的完整性和安全性
	 *
	 * @return JWKSource
	 */
	@Bean
	JWKSource<SecurityContext> jwkSource() {
		KeyPair keyPair = RsaHelper.generateKeyPair(ConstDigest.KEY_SIZE_2048);
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		RSAKey rsaKey =
				new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return new ImmutableJWKSet<>(jwkSet);
	}

	/**
	 * 用于解码已签名的访问令牌
	 * 
	 * 用于验证和解码已签名的访问令牌,以获取其中包含的授权信息和用户身份,负责验证访问令牌的有效性和真实性
	 *
	 * @param jwkSource jwk源
	 * @return JwtDecoder
	 */
	@Bean
	JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	/**
	 * 如果使用指定数据库,可以自动新增如下3张表
	 * 
	 * @return EmbeddedDatabase
	 */
	@Bean
	EmbeddedDatabase embeddedDatabase() {
		// @formatter:off
		return new EmbeddedDatabaseBuilder().generateUniqueName(true)
				.setType(EmbeddedDatabaseType.H2)
				// 自定义使用其他类型数据库
				.setDataSourceFactory(null)
				.setScriptEncoding("UTF-8")
				.addScript("org/springframework/security/oauth2/server/authorization/oauth2-authorization-schema.sql")
				.addScript(
						"org/springframework/security/oauth2/server/authorization/oauth2-authorization-consent-schema.sql")
				.addScript(
						"org/springframework/security/oauth2/server/authorization/client/oauth2-registered-client-schema.sql")
				.build();
		// @formatter:on
	}
}