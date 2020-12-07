package com.wy.config;

import java.lang.reflect.Method;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 自定义缓存的相关属性
 * 
 * @author ParadiseWY
 * @date 2020-12-07 15:18:33
 * @git {@link https://github.com/mygodness100}
 */
@Configuration
public class CacheConfig {

	/**
	 * 自定义缓存key
	 * 
	 * @return 缓存key
	 */
	@Bean("customizeKeyGenerator")
	public KeyGenerator customizeKeyGenerator() {

		return new KeyGenerator() {

			@Override
			public Object generate(Object target, Method method, Object... params) {
				String key = method.getName();
				for (Object object : params) {
					key += object.toString();
				}
				return key;
			}
		};
	}

	/**
	 * 定义redis缓存中的redistemplate序列化
	 */
	@Bean
	@ConditionalOnMissingBean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		return RedisCacheManager.create(connectionFactory);
	}
}