package com.wy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.wy.oauth2.SelfAccessTokenConverter;
import com.wy.util.JwtUtil;

/**
 * OAuth2相关配置
 *
 * @author 飞花梦影
 * @date 2021-07-02 16:51:40
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Deprecated
@Configuration
public class OAuth2Config {

	@Autowired
	private SelfAccessTokenConverter selfAccessTokenConverter;

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
	 * 使用JWT存储令牌access_token
	 * 
	 * @return TokenStore
	 */
	@Bean
	TokenStore tokenStore() {
		JwtTokenStore jwtTokenStore = new JwtTokenStore(jwtAccessTokenConverter());
		// 使用内存存储令牌
		// jwtTokenStore.setApprovalStore(inMemoryApprovalStore());
		return jwtTokenStore;
	}

	/**
	 * JWT令牌解析器
	 * 
	 * @return JwtAccessTokenConverter
	 */
	@Bean
	// @ConditionalOnProperty(prefix = "config.oauth2", name = "storeType",
	// havingValue = "jwt", matchIfMissing = true)
	JwtAccessTokenConverter jwtAccessTokenConverter() {
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
		// final RsaSigner signer = new RsaSigner(JwtUtil.getSignerKey());
		//
		// JwtAccessTokenConverter jwtAccessTokenConverter = new
		// JwtAccessTokenConverter() {
		//
		// private JsonParser objectMapper = JsonParserFactory.create();
		//
		// @Override
		// protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication
		// authentication) {
		//
		// // String userName = authentication.getUserAuthentication().getName();
		// // // 与登录时候放进去的UserDetail实现类一致
		// // User user = (User) authentication.getUserAuthentication().getPrincipal();
		// // /** 自定义一些token属性 */
		// // final Map<String, Object> additionalInformation = new HashMap<>();
		// // additionalInformation.put("userName", userName);
		// // additionalInformation.put("roles", user.getAuthorities());
		// // ((DefaultOAuth2AccessToken)
		// // accessToken).setAdditionalInformation(additionalInformation);
		// // OAuth2AccessToken enhancedToken = super.enhance(accessToken,
		// authentication);
		// // return enhancedToken;
		//
		// String content;
		// try {
		// content = this.objectMapper
		// .formatMap(getAccessTokenConverter().convertAccessToken(accessToken,
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
		// jwtAccessTokenConverter.setSigner(signer);
		// jwtAccessTokenConverter.setVerifier(new
		// RsaVerifier(JwtUtil.getVerifierKey()));

		// 第三种方式
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
		// 设置签名密钥,需要和认证服务器的一直
		jwtAccessTokenConverter.setSigningKey("test");
		// 设置验证时使用的密钥,和签名密钥保持一致
		jwtAccessTokenConverter.setVerifier(new MacSigner("test"));
		// 解析从认证服务器返回的额外信息并存入到本服务器中的认证信息中
		jwtAccessTokenConverter.setAccessTokenConverter(selfAccessTokenConverter);
		return jwtAccessTokenConverter;
	}

	/**
	 * 第二种方式生成JWT令牌需要的方法
	 * 
	 * @return JWK令牌
	 */
	@Bean
	JWKSet jwkSet() {
		RSAKey.Builder builder = new RSAKey.Builder(JwtUtil.getVerifierKey()).keyUse(KeyUse.SIGNATURE)
				.algorithm(JWSAlgorithm.RS256).keyID(JwtUtil.VERIFIER_KEY_ID);
		return new JWKSet(builder.build());
	}
}