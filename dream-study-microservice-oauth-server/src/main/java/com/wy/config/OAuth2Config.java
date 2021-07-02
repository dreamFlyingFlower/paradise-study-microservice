package com.wy.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.InMemoryApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * OAuth2相关配置
 *
 * @author 飞花梦影
 * @date 2021-07-02 16:51:40
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
public class OAuth2Config {

	/**
	 * redisTokenStore存储令牌
	 * 
	 * @return TokenStore
	 */
	@Bean
	@ConditionalOnProperty(prefix = "config.oauth2", name = "storeType", havingValue = "redis")
	public TokenStore redisTokenStore(RedisConnectionFactory redisConnectionFactory) {
		return new RedisTokenStore(redisConnectionFactory);
	}

	/**
	 * 使用JWT存储令牌
	 * 
	 * @return
	 */
	@Bean
	public TokenStore tokenStore() {
		JwtTokenStore tokenStore = new JwtTokenStore(jwtAccessTokenConverter());
		tokenStore.setApprovalStore(inMemoryApprovalStore());
		return tokenStore;
	}

	/**
	 * 使用JWT存储令牌
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnProperty(prefix = "config.oauth2", name = "storeType", havingValue = "jwt", matchIfMissing = true)
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		// 生成JWT令牌的第一种方式
		// KeyPair keyPair = new KeyStoreKeyFactory(new
		// ClassPathResource("keystore.jks"), "foobar".toCharArray())
		// .getKeyPair("test");
		// converter.setKeyPair(keyPair);
		// 生成JWT令牌的第二种方式
		// final RsaSigner signer = new RsaSigner(JwtUtil.getSignerKey());
		//
		// JwtAccessTokenConverter converter = new JwtAccessTokenConverter() {
		// private JsonParser objectMapper = JsonParserFactory.create();
		//
		// @Override
		// protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication
		// authentication) {
		// String content;
		// try {
		// content =
		// this.objectMapper.formatMap(getAccessTokenConverter().convertAccessToken(accessToken,
		// authentication));
		// } catch (Exception ex) {
		// throw new IllegalStateException("Cannot convert access token to JSON", ex);
		// }
		// Map<String, String> headers = new HashMap<>();
		// headers.put("kid", JwtUtil.VERIFIER_KEY_ID);
		// String token = JwtHelper.encode(content, signer, headers).getEncoded();
		// return token;
		// }
		// };
		// converter.setSigner(signer);
		// converter.setVerifier(new RsaVerifier(JwtUtil.getVerifierKey()));
		// return converter;
		jwtAccessTokenConverter().setSigningKey("test");
		return converter;
	}

	// 第二种方式生成JWT令牌需要的方法
	// @Bean
	// public JWKSet jwkSet() {
	// RSAKey.Builder builder = new RSAKey.Builder(JwtUtil.getVerifierKey())
	// .keyUse(KeyUse.SIGNATURE)
	// .algorithm(JWSAlgorithm.RS256)
	// .keyID(JwtUtil.VERIFIER_KEY_ID);
	// return new JWKSet(builder.build());
	// }

	@Bean
	public ApprovalStore inMemoryApprovalStore() {
		return new InMemoryApprovalStore();
	}

	@Bean
	public UserApprovalHandler userApprovalHandler(ClientDetailsService clientDetailsService) {
		ApprovalStoreUserApprovalHandler userApprovalHandler = new ApprovalStoreUserApprovalHandler();
		userApprovalHandler.setApprovalStore(inMemoryApprovalStore());
		userApprovalHandler.setClientDetailsService(clientDetailsService);
		userApprovalHandler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
		return userApprovalHandler;
	}
}