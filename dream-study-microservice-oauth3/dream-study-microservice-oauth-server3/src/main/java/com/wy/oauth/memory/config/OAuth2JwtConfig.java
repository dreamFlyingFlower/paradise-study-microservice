//package com.wy.oauth.memory.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.jwt.crypto.sign.MacSigner;
//import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
//import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
//import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
//import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
//
///**
// * OAuth2 JWT相关配置
// *
// * @author 飞花梦影
// * @date 2021-07-02 16:51:40
// * @git {@link https://github.com/dreamFlyingFlower }
// */
//@SuppressWarnings("deprecation")
//@Configuration
//public class OAuth2JwtConfig {
//
//	/**
//	 * 将授权码存储在内存
//	 * 
//	 * @return AuthorizationCodeServices
//	 */
//	@Bean
//	AuthorizationCodeServices memoryAuthorizationCodeServices() {
//		// 使用内存方式存储授权码
//		return new InMemoryAuthorizationCodeServices();
//	}
//
//	/**
//	 * 令牌服务
//	 * 
//	 * @return AuthorizationServerTokenServices
//	 */
//	@Bean
//	AuthorizationServerTokenServices memoryAuthorizationServerTokenServices() {
//		// 使用默认的token服务
//		DefaultTokenServices service = new DefaultTokenServices();
//		// 是否刷新令牌
//		service.setSupportRefreshToken(true);
//		// 令牌存储策略,需要和AuthorizationServerEndpointsConfigurer配置的tokenStore方式相同
//		// 使用JWT方式
//		service.setTokenStore(tokenStore());
//		// 针对JWT令牌的添加
//		service.setTokenEnhancer(jwtAccessTokenConverter());
//		// 令牌默认有效期2小时
//		service.setAccessTokenValiditySeconds(7200);
//		// 刷新令牌默认有效期3天
//		service.setRefreshTokenValiditySeconds(259200);
//		return service;
//	}
//
//	/**
//	 * 使用JWT存储access_token
//	 * 
//	 * @return TokenStore
//	 */
//	@Bean
//	TokenStore tokenStore() {
//		return new JwtTokenStore(jwtAccessTokenConverter());
//	}
//
//	/**
//	 * JWT令牌解析器
//	 * 
//	 * @return JwtAccessTokenConverter
//	 */
//	@Bean
//	// @ConditionalOnProperty(prefix = "config.oauth2", name = "storeType",
//	// havingValue = "jwt", matchIfMissing = true)
//	JwtAccessTokenConverter jwtAccessTokenConverter() {
//		// 生成JWT令牌
//		// 第一种方式
//		// JwtAccessTokenConverter jwtAccessTokenConverter = new
//		// JwtAccessTokenConverter();
//		// KeyPair keyPair = new KeyStoreKeyFactory(new
//		// ClassPathResource("keystore.jks"), "foobar".toCharArray())
//		// .getKeyPair("test");
//		// jwtAccessTokenConverter.setKeyPair(keyPair);
//
//		// 第二种方式
//		// 测试用,资源服务使用相同的字符达到一个对称加密的效果,生产时候使用RSA非对称加密方式
//		// final RsaSigner signer = new RsaSigner(JwtUtil.getSignerKey());
//		//
//		// JwtAccessTokenConverter jwtAccessTokenConverter = new
//		// JwtAccessTokenConverter() {
//		//
//		// private JsonParser objectMapper = JsonParserFactory.create();
//		//
//		// @Override
//		// protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication
//		// authentication) {
//		//
//		// // String userName = authentication.getUserAuthentication().getName();
//		// // // 与登录时候放进去的UserDetail实现类一致
//		// // User user = (User) authentication.getUserAuthentication().getPrincipal();
//		// // /** 自定义一些token属性 */
//		// // final Map<String, Object> additionalInformation = new HashMap<>();
//		// // additionalInformation.put("userName", userName);
//		// // additionalInformation.put("roles", user.getAuthorities());
//		// // ((DefaultOAuth2AccessToken)
//		// // accessToken).setAdditionalInformation(additionalInformation);
//		// // OAuth2AccessToken enhancedToken = super.enhance(accessToken,
//		// authentication);
//		// // return enhancedToken;
//		//
//		// String content;
//		// try {
//		// content = this.objectMapper
//		// .formatMap(getAccessTokenConverter().convertAccessToken(accessToken,
//		// authentication));
//		// } catch (Exception ex) {
//		// throw new IllegalStateException("Cannot convert access token to JSON", ex);
//		// }
//		// Map<String, String> headers = new HashMap<>();
//		// headers.put("kid", JwtUtil.VERIFIER_KEY_ID);
//		// String token = JwtHelper.encode(content, signer, headers).getEncoded();
//		// return token;
//		// }
//		// };
//		// jwtAccessTokenConverter.setSigner(signer);
//		// jwtAccessTokenConverter.setVerifier(new
//		// RsaVerifier(JwtUtil.getVerifierKey()));
//
//		// 第三种方式
//		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
//		// 设置签名密钥
//		jwtAccessTokenConverter.setSigningKey("test");
//		// 设置验证时使用的密钥,和签名密钥保持一致
//		jwtAccessTokenConverter.setVerifier(new MacSigner("test"));
//		return jwtAccessTokenConverter;
//	}
//}