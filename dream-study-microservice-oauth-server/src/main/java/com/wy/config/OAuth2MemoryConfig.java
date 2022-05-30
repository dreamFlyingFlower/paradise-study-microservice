package com.wy.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.InMemoryApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.wy.util.JwtUtil;

/**
 * OAuth2相关配置
 *
 * @author 飞花梦影
 * @date 2021-07-02 16:51:40
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
public class OAuth2MemoryConfig {

	@Autowired
	private ClientDetailsService clientDetailsService;

	/**
	 * redisTokenStore存储令牌
	 * 
	 * @return TokenStore
	 */
	// @Bean
	// @ConditionalOnProperty(prefix = "config.oauth2", name = "storeType",
	// havingValue = "redis")
	// public TokenStore redisTokenStore(RedisConnectionFactory
	// redisConnectionFactory) {
	// return new RedisTokenStore(redisConnectionFactory);
	// }

	/**
	 * 使用JWT存储令牌
	 * 
	 * @return
	 */
	@Bean
	public TokenStore jwtTokenStore() {
		JwtTokenStore jwtTokenStore = new JwtTokenStore(jwtAccessTokenConverter());
		jwtTokenStore.setApprovalStore(inMemoryApprovalStore());
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
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		// 生成JWT令牌
		// 第一种方式
		// JwtAccessTokenConverter jwtAccessTokenConverter = new
		// JwtAccessTokenConverter();
		// KeyPair keyPair = new KeyStoreKeyFactory(new
		// ClassPathResource("keystore.jks"), "foobar".toCharArray())
		// .getKeyPair("test");
		// jwtAccessTokenConverter.setKeyPair(keyPair);
		// 第二种方式
		// 测试用,资源服务使用相同的字符达到一个对称加密的效果,生产时候使用RSA非对称加密方式
		final RsaSigner signer = new RsaSigner(JwtUtil.getSignerKey());

		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter() {

			private JsonParser objectMapper = JsonParserFactory.create();

			@Override
			protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

				// String userName = authentication.getUserAuthentication().getName();
				// // 与登录时候放进去的UserDetail实现类一直查看link{SecurityConfiguration}
				// User user = (User) authentication.getUserAuthentication().getPrincipal();
				// /** 自定义一些token属性 ***/
				// final Map<String, Object> additionalInformation = new HashMap<>();
				// additionalInformation.put("userName", userName);
				// additionalInformation.put("roles", user.getAuthorities());
				// ((DefaultOAuth2AccessToken)
				// accessToken).setAdditionalInformation(additionalInformation);
				// OAuth2AccessToken enhancedToken = super.enhance(accessToken, authentication);
				// return enhancedToken;

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

	@Bean
	public ApprovalStore inMemoryApprovalStore() {
		return new InMemoryApprovalStore();
	}

	/**
	 * 将授权码存储在内存
	 */
	@Bean
	public AuthorizationCodeServices authorizationCodeServices() {
		// 使用内存方式存储授权码
		return new InMemoryAuthorizationCodeServices();
	}

	/**
	 * 生成JWT令牌加密
	 * 
	 * @return JWK令牌
	 */
	@Bean
	public JWKSet jwkSet() {
		RSAKey.Builder builder = new RSAKey.Builder(JwtUtil.getVerifierKey()).keyUse(KeyUse.SIGNATURE)
		        .algorithm(JWSAlgorithm.RS256).keyID(JwtUtil.VERIFIER_KEY_ID);
		return new JWKSet(builder.build());
	}

	/**
	 * 令牌服务
	 */
	@Bean
	public AuthorizationServerTokenServices memoryAthorizationServerTokenServices() {
		DefaultTokenServices service = new DefaultTokenServices();
		// 是否刷新令牌
		service.setSupportRefreshToken(true);
		// 令牌存储策略
		service.setTokenStore(jwtTokenStore());
		// 令牌默认有效期2小时
		service.setAccessTokenValiditySeconds(7200);
		// 刷新令牌默认有效期3天
		service.setRefreshTokenValiditySeconds(259200);
		return service;
	}

	@Bean
	public UserApprovalHandler userApprovalHandler() {
		ApprovalStoreUserApprovalHandler userApprovalHandler = new ApprovalStoreUserApprovalHandler();
		userApprovalHandler.setApprovalStore(inMemoryApprovalStore());
		userApprovalHandler.setClientDetailsService(clientDetailsService);
		userApprovalHandler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
		return userApprovalHandler;
	}
}