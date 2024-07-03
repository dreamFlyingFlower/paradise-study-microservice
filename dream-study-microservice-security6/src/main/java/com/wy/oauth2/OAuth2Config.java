package com.wy.oauth2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
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
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * OAuth2认证服务器相关配置
 *
 * @author 飞花梦影
 * @date 2022-09-13 16:26:49
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
public class OAuth2Config {

	/**
	 * {@link TokenStoreConfig#jwtAccessTokenConverter()}
	 */
	@Autowired
	private JwtAccessTokenConverter jwtAccessTokenConverter;

	/**
	 * {@link TokenStoreConfig#jwtTokenEnhancer()}
	 */
	@Autowired
	private TokenEnhancer jwtTokenEnhancer;

	/**
	 * {@link TokenStoreConfig#redisTokenStore()},{@link TokenStoreConfig#jwtTokenStore()}
	 */
	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ClientDetailsService clientDetailsService;

	// @Autowired
	// private OauthService oauthService;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	ClientDetailsService clientDetailsService(DataSource dataSource) {
		// 使用默认的数据库处理数据
		JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
		clientDetailsService.setPasswordEncoder(passwordEncoder);

		// 使用默认的内存处理数据
		// InMemoryClientDetailsService memoryClientDetailsService = new
		// InMemoryClientDetailsService();

		// 使用自定义的ClientDetailsService
		// SelfJdbcClientDetailsService clientDetailsService = new
		// SelfJdbcClientDetailsService(dataSource);
		// clientDetailsService.setPasswordEncoder(passwordEncoder());
		return clientDetailsService;
	}

	/**
	 * 使用内存或数据库处理验证码
	 * 
	 * @param dataSource 数据库配置
	 * @return AuthorizationCodeServices
	 */
	@Bean
	AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
		// return new InMemoryAuthorizationCodeServices();
		return new JdbcAuthorizationCodeServices(dataSource);
	}

	@Bean
	UserApprovalHandler userApprovalHandler(DataSource dataSource) {
		ApprovalStoreUserApprovalHandler userApprovalHandler = new ApprovalStoreUserApprovalHandler();
		userApprovalHandler.setApprovalStore(new JdbcApprovalStore(dataSource));
		// 通过token统计结果
		// TokenStoreUserApprovalHandler userApprovalHandler = new
		// TokenStoreUserApprovalHandler();
		// userApprovalHandler.setTokenStore(tokenStore);

		// 通过自定义处理方法处理token
		// SelfUserApprovalHandler userApprovalHandler = new
		// SelfUserApprovalHandler();
		// userApprovalHandler.setOauthService(oauthService);
		// userApprovalHandler.setTokenStore(tokenStore);

		userApprovalHandler.setClientDetailsService(clientDetailsService);
		userApprovalHandler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
		return userApprovalHandler;
	}

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
	 * 自定义token处理方法
	 * 
	 * @param clientDetailsService 客户端信息
	 * @return AuthorizationServerTokenServices
	 */
	@Bean
	AuthorizationServerTokenServices authorizationServerTokenServices(ClientDetailsService clientDetailsService) {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setClientDetailsService(clientDetailsService);
		defaultTokenServices.setSupportRefreshToken(true);
		defaultTokenServices.setTokenStore(tokenStore);

		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		if (null != jwtAccessTokenConverter && null != jwtTokenEnhancer) {
			ArrayList<TokenEnhancer> enhancers = new ArrayList<>();
			enhancers.add(jwtTokenEnhancer);
			enhancers.add(jwtAccessTokenConverter);
			tokenEnhancerChain.setTokenEnhancers(enhancers);
		} else {
			tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter));
			defaultTokenServices.setTokenEnhancer(tokenEnhancerChain);
		}

		defaultTokenServices.setAccessTokenValiditySeconds(7200); // 令牌默认有效期2小时
		defaultTokenServices.setRefreshTokenValiditySeconds(259200); // 刷新令牌默认有效期3天
		return defaultTokenServices;
	}
}