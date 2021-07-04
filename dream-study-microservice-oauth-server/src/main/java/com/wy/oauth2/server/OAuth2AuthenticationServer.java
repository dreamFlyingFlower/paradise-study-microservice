package com.wy.oauth2.server;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.InMemoryApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.wy.properties.ConfigProperties;
import com.wy.util.JwtUtil;

/**
 * OAuth2认证服务器
 *
 * @author 飞花梦影
 * @date 2021-07-02 15:10:26
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthenticationServer extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;

//	@Autowired
//	private TokenStore redisTokenStore;

//	@Autowired
//	private TokenStore jwtTokenStore;

//	@Autowired
//	private JwtAccessTokenConverter jwtAccessTokenConverter;

//	@Autowired
//	private UserApprovalHandler userApprovalHandler;

	@Autowired
	private ConfigProperties config;

	@Autowired
	private ClientDetailsService clientDetailsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient(config.getOauth2().getClientIdGuest())
				.secret(passwordEncoder.encode(config.getOauth2().getClientSecretGuest()))
				.authorizedGrantTypes(config.getOauth2().getGrantTypes()).scopes(config.getOauth2().getScopes())
				.redirectUris("http://localhost:55300/oauthClient/oauth/authorized").and().withClient("user1")
				.secret("password").authorizedGrantTypes(config.getOauth2().getGrantTypes())
				.redirectUris("http://localhost:55300/oauthClient/oauth/authorized");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager).accessTokenConverter(accessTokenConverter())
				.userApprovalHandler(userApprovalHandler());
		// if (null == jwtTokenStore) {
		// endpoints.tokenStore(redisTokenStore);
		// } else {
		endpoints.tokenStore(jwtTokenStore());
		// }
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		// 获得签名的signkey,需要身份验证才行,默认是denyAll(),这是SpringSecurity的权限表达式
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}

	@Bean
	public TokenStore jwtTokenStore() {
		JwtTokenStore jwtTokenStore = new JwtTokenStore(accessTokenConverter());
		jwtTokenStore.setApprovalStore(approvalStore());
		return jwtTokenStore;
	}

	/**
	 * 使用JWT存储令牌
	 * 
	 * @return
	 */
	@Bean
	// @ConditionalOnProperty(prefix = "config.oauth2", name = "storeType",
	// havingValue = "jwt", matchIfMissing = true)
	public JwtAccessTokenConverter accessTokenConverter() {
		// 生成JWT令牌
		// 第一种方式
		// JwtAccessTokenConverter jwtAccessTokenConverter = new
		// JwtAccessTokenConverter();
		// KeyPair keyPair = new KeyStoreKeyFactory(new
		// ClassPathResource("keystore.jks"), "foobar".toCharArray())
		// .getKeyPair("test");
		// jwtAccessTokenConverter.setKeyPair(keyPair);
		// 第二种方式
		final RsaSigner signer = new RsaSigner(JwtUtil.getSignerKey());

		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter() {

			private JsonParser objectMapper = JsonParserFactory.create();

			@Override
			protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
				String content;
				try {
					content = this.objectMapper
							.formatMap(getAccessTokenConverter().convertAccessToken(accessToken, authentication));
				} catch (Exception ex) {
					throw new IllegalStateException("Cannot convert access token to JSON", ex);
				}
				Map<String, String> headers = new HashMap<>();
				headers.put("kid", JwtUtil.VERIFIER_KEY_ID);
				String token = JwtHelper.encode(content, signer, headers).getEncoded();
				return token;
			}
		};
		jwtAccessTokenConverter.setSigner(signer);
		jwtAccessTokenConverter.setVerifier(new RsaVerifier(JwtUtil.getVerifierKey()));
		// 第三种方式
		// JwtAccessTokenConverter jwtAccessTokenConverter = new
		// JwtAccessTokenConverter();
		// jwtAccessTokenConverter.setSigningKey("test");
		return jwtAccessTokenConverter;
	}

	/**
	 * 第二种方式生成JWT令牌需要的方法
	 * 
	 * @return JWK令牌
	 */
	@Bean
	public JWKSet jwkSet() {
		RSAKey.Builder builder = new RSAKey.Builder(JwtUtil.getVerifierKey()).keyUse(KeyUse.SIGNATURE)
				.algorithm(JWSAlgorithm.RS256).keyID(JwtUtil.VERIFIER_KEY_ID);
		return new JWKSet(builder.build());
	}

	@Bean
	public ApprovalStore approvalStore() {
		return new InMemoryApprovalStore();
	}

	@Bean
	public UserApprovalHandler userApprovalHandler() {
		ApprovalStoreUserApprovalHandler userApprovalHandler = new ApprovalStoreUserApprovalHandler();
		userApprovalHandler.setApprovalStore(approvalStore());
		userApprovalHandler.setClientDetailsService(clientDetailsService);
		userApprovalHandler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
		return userApprovalHandler;
	}
}