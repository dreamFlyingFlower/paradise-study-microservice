package com.wy.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * 使用JWT令牌代替SpringOAuth2生成的UUID令牌
 *
 * @author 飞花梦影
 * @date 2021-07-01 10:16:13
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@SuppressWarnings("deprecation")
@Configuration
public class TokenStoreConfig {

	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	/**
	 * redisTokenStore存储令牌
	 * 
	 * @return TokenStore
	 */
	@Bean
	@ConditionalOnProperty(prefix = "config.security.oauth2.token", name = "storeType", havingValue = "redis")
	public TokenStore redisTokenStore() {
		return new RedisTokenStore(redisConnectionFactory);
	}

	/**
	 * JwtTokenStore存取令牌
	 * 
	 * @return TokenStore
	 */
	@Bean
	@ConditionalOnProperty(prefix = "config.security.oauth2.token", name = "storeType", havingValue = "jwt",
			matchIfMissing = true)
	public TokenStore jwtTokenStore() {
		return new JwtTokenStore(jwtAccessTokenConverter());
	}

	/**
	 * 管理JWT的生成规则,管理等
	 * 
	 * @return JwtAccessTokenConverter
	 */
	@Bean
	@ConditionalOnProperty(prefix = "config.security.oauth2.token", name = "storeType", havingValue = "jwt",
			matchIfMissing = true)
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
		jwtAccessTokenConverter.setSigningKey("密钥,可配置");
		return jwtAccessTokenConverter;
	}

	/**
	 * 自定义Token生成器,若无扩展信息可不自定义
	 * 
	 * @return TokenEnhancer
	 */
	@Bean
	@ConditionalOnMissingBean(name = "jwtTokenEnhancer")
	public TokenEnhancer jwtTokenEnhancer() {
		return new CustomizeTokenEnhancer();
	}
}