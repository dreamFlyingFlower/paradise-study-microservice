package com.wy.oauth.memory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 * OAuth2 Memory相关配置
 *
 * @author 飞花梦影
 * @date 2021-07-02 16:51:40
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Deprecated
@Configuration
public class OAuth2MemoryConfig {

	/**
	 * 将授权码存储在内存
	 * 
	 * @return AuthorizationCodeServices
	 */
	@Bean
	AuthorizationCodeServices memoryAuthorizationCodeServices() {
		// 使用内存方式存储授权码
		return new InMemoryAuthorizationCodeServices();
	}

	/**
	 * 暂时保存到内存中
	 * 
	 * @return TokenStore
	 */
	@Bean
	TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}

	/**
	 * 令牌服务
	 * 
	 * @return AuthorizationServerTokenServices
	 */
	@Bean
	AuthorizationServerTokenServices memoryAuthorizationServerTokenServices() {
		// 使用默认的token服务
		DefaultTokenServices service = new DefaultTokenServices();
		// 是否刷新令牌
		service.setSupportRefreshToken(true);
		// 令牌存储策略,需要和AuthorizationServerEndpointsConfigurer配置的tokenStore方式相同
		// 使用内存方式
		service.setTokenStore(tokenStore());
		// 令牌默认有效期2小时
		service.setAccessTokenValiditySeconds(7200);
		// 刷新令牌默认有效期3天
		service.setRefreshTokenValiditySeconds(259200);
		return service;
	}
}