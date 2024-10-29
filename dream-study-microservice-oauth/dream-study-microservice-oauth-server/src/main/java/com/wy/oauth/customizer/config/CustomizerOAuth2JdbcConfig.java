package com.wy.oauth.customizer.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.wy.oauth.CustomizeTokenEnhancer;
import com.wy.oauth.customizer.CustomizerJdbcClientDetailsService;
import com.wy.util.JwtUtil;

/**
 * OAuth2使用数据库自定义相关配置
 *
 * @author 飞花梦影
 * @date 2021-07-02 16:51:40
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Deprecated
@Configuration
public class CustomizerOAuth2JdbcConfig {

	@Autowired
	private DataSource dataSource;

	// @Autowired
	// private JdbcAccessTokenConverter jdbcAccessTokenConverter;

	/**
	 * 授权码模式数据来源,将授权码存储在数据库
	 * 
	 * @return AuthorizationCodeServices
	 */
	@Bean
	AuthorizationCodeServices authorizationCodeServices() {
		// 设置授权码模式的授权码存取在数据库中
		return new JdbcAuthorizationCodeServices(dataSource);
	}

	/**
	 * 使用JWT存储令牌,无需建表
	 * 
	 * @return TokenStore
	 */
	@ConditionalOnProperty(prefix = "config.oauth2", name = "storeType", havingValue = "jwt", matchIfMissing = true)
	@Bean
	TokenStore tokenStore() {
		return new JwtTokenStore(jwtAccessTokenConverter());
	}

	/**
	 * 使用数据库存储令牌,需要建表oauth_acccess_token和oauth_refresh_token,该表结构见 JdbcTokenStore
	 *
	 * @return TokenStore
	 */
	@Bean
	@ConditionalOnProperty(prefix = "config.oauth2", name = "storeType", havingValue = "jdbc")
	public TokenStore jdbcTokenStore() {
		return new JdbcTokenStore(dataSource);
	}

	/**
	 * 使用redis存储令牌,无需建表
	 * 
	 * @return TokenStore
	 */
	@Bean
	@ConditionalOnProperty(prefix = "config.oauth2", name = "storeType", havingValue = "redis")
	public TokenStore redisTokenStore(RedisConnectionFactory redisConnectionFactory) {
		return new RedisTokenStore(redisConnectionFactory);
	}

	/**
	 * 注入数据库资源,需要新建oauth_client_details表,该表结构见 JdbcClientDetailsService
	 * 
	 * @return JdbcClientDetailsService
	 */
	@Bean
	JdbcClientDetailsService jdbcClientDetailsService() {
		return new CustomizerJdbcClientDetailsService(dataSource);
	}

	/**
	 * Token令牌服务
	 */
	@Bean
	@Primary
	AuthorizationServerTokenServices authorizationServerTokenServices() {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		// 客户端信息服务
		defaultTokenServices.setClientDetailsService(jdbcClientDetailsService());
		// 令牌存储策略
		defaultTokenServices.setTokenStore(tokenStore());
		// 是否支持刷新令牌
		defaultTokenServices.setSupportRefreshToken(true);
		// 令牌默认有效期2小时
		defaultTokenServices.setAccessTokenValiditySeconds(60 * 60 * 2);
		// 刷新令牌默认有效期30天
		defaultTokenServices.setRefreshTokenValiditySeconds(60 * 60 * 24 * 30);
		// 针对JWT令牌的添加
		defaultTokenServices.setTokenEnhancer(jwtAccessTokenConverter());

		// 加入JWT配置
		// TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		// tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtAccessTokenConverter));
		// service.setTokenEnhancer(tokenEnhancerChain);

		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		if (null != jwtAccessTokenConverter() && null != jwtTokenEnhancer()) {
			ArrayList<TokenEnhancer> enhancers = new ArrayList<>();
			enhancers.add(jwtAccessTokenConverter());
			enhancers.add(jwtTokenEnhancer());
			tokenEnhancerChain.setTokenEnhancers(enhancers);
		} else {
			tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter()));
			defaultTokenServices.setTokenEnhancer(tokenEnhancerChain);
		}
		return defaultTokenServices;
	}

	/**
	 * JWT令牌解析器
	 * 
	 * @return JwtAccessTokenConverter
	 */
	@Bean
	@ConditionalOnProperty(prefix = "config.oauth2", name = "storeType", havingValue = "jwt", matchIfMissing = true)
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
		// // // 与登录时候放进去的UserDetail实现类一直查看link{SecurityConfiguration}
		// // User user = (User) authentication.getUserAuthentication().getPrincipal();
		// // /** 自定义一些token属性 ***/
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
		// JwtAccessTokenConverter jwtAccessTokenConverter = new
		// JwtAccessTokenConverter();
		// 设置签名密钥
		// jwtAccessTokenConverter.setSigningKey("test");
		// 设置验证时使用的密钥,和签名密钥保持一致
		// jwtAccessTokenConverter.setVerifier(new MacSigner("test"));
		// 设置自定义JWT数据
		// jwtAccessTokenConverter.setAccessTokenConverter(jdbcAccessTokenConverter);

		// 第四种方式
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
		jwtAccessTokenConverter.setSigningKey("test");
		return jwtAccessTokenConverter;
	}

	/**
	 * 设置默认的Token生成方式
	 * 
	 * @return TokenEnhancer
	 */
	@Bean
	TokenEnhancer tokenEnhancer() {
		return new TokenEnhancer() {

			@Override
			public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
				DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
				Map<String, Object> additionalInformation = new LinkedHashMap<String, Object>();
				additionalInformation.put("code", 0);
				additionalInformation.put("msg", "success");
				token.setAdditionalInformation(additionalInformation);
				return accessToken;
			}
		};
	}

	/**
	 * 设置默认的token生成方式
	 * 
	 * @return TokenEnhancer
	 */
	@Bean
	@ConditionalOnMissingBean(name = "jwtTokenEnhancer")
	TokenEnhancer jwtTokenEnhancer() {
		return new CustomizeTokenEnhancer();
	}

	/**
	 * 生成JWT令牌加密
	 * 
	 * @return JWK令牌
	 */
	@Bean
	JWKSet jwkSet() {
		RSAKey.Builder builder = new RSAKey.Builder(JwtUtil.getVerifierKey()).keyUse(KeyUse.SIGNATURE)
				.algorithm(JWSAlgorithm.RS256)
				.keyID(JwtUtil.VERIFIER_KEY_ID);
		return new JWKSet(builder.build());
	}

	/**
	 * 授权信息保存策略
	 * 
	 * @return ApprovalStore
	 */
	@Bean
	ApprovalStore jdbcApprovalStore() {
		return new JdbcApprovalStore(dataSource);
	}

	@Bean
	UserApprovalHandler userApprovalHandler() {
		ApprovalStoreUserApprovalHandler userApprovalHandler = new ApprovalStoreUserApprovalHandler();
		userApprovalHandler.setApprovalStore(jdbcApprovalStore());
		userApprovalHandler.setClientDetailsService(jdbcClientDetailsService());
		userApprovalHandler.setRequestFactory(new DefaultOAuth2RequestFactory(jdbcClientDetailsService()));

		// 通过token统计结果
		// TokenStoreUserApprovalHandler userApprovalHandler = new
		// TokenStoreUserApprovalHandler();
		// userApprovalHandler.setTokenStore(tokenStore);

		// 通过自定义处理方法处理token
		// CustomizerUserApprovalHandler customizerUserApprovalHandler = new
		// CustomizerUserApprovalHandler();
		// customizerUserApprovalHandler.setOauthService(oauthService);
		// customizerUserApprovalHandler.setTokenStore(tokenStore);

		return userApprovalHandler;
	}
}