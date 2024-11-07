package com.wy.config;

import java.text.SimpleDateFormat;
import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.jackson2.SecurityJackson2Modules;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dream.flying.flower.ConstDate;
import lombok.RequiredArgsConstructor;

/**
 * Redis序列化,不能直接使用Redis包中的RedisConfig,因为无法序列化SpringSecurity相关类
 * 
 * @author 飞花梦影
 * @date 2024-11-01 23:31:35
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(RedisOperations.class)
public class RedisConfig {

	private final Jackson2ObjectMapperBuilder builder;

	@Bean
	@ConditionalOnMissingBean
	RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = jackson2JsonRedisSerializer();

		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(factory);
		redisTemplate.setKeySerializer(RedisSerializer.string());
		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

		redisTemplate.setHashKeySerializer(RedisSerializer.string());
		redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
		return redisTemplate;
	}

	private Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
		// ObjectMapper objectMapper = new ObjectMapper();

		// 创建ObjectMapper并添加默认配置
		ObjectMapper objectMapper = builder.createXmlMapper(false).build();
		// 序列化所有字段
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		// 防止对象中还有对象,出现ClassCastException
		objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
				ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
		// objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
		// ObjectMapper.DefaultTyping.NON_FINAL);
		// 对象的所有字段全部列入,还是其他的选项,可以忽略null等
		objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
		// 取消默认的时间转换为timeStamp格式
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		// 设置Date类型的序列化及反序列化格式
		objectMapper.setDateFormat(new SimpleDateFormat(ConstDate.DATETIME));
		// 忽略空Bean转json的错误
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		// 忽略未知属性,防止json字符串中存在,java对象中不存在对应属性的情况出现错误
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.registerModule(new Jdk8Module());
		objectMapper.registerModule(new JavaTimeModule());
		// 如果不加该Module,会出现使用Redis反序列化Authentication失败的问题.Redis不含该Module,只能重写RedisConfig
		objectMapper.registerModule(new CoreJackson2Module());
		// 加载所有与SpringSecurity相关的Module
		objectMapper.registerModules(SecurityJackson2Modules.getModules(RedisConfig.class.getClassLoader()));

		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
				new Jackson2JsonRedisSerializer<>(Object.class);
		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
		return jackson2JsonRedisSerializer;
	}

	@Bean
	@ConditionalOnMissingBean
	CacheManager cacheManager(RedisConnectionFactory factory) {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = jackson2JsonRedisSerializer();

		// 配置序列化,解决乱码的问题,过期时间600秒
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofSeconds(600))
				.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
				.serializeValuesWith(
						RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
				.disableCachingNullValues();
		return RedisCacheManager.builder(factory).cacheDefaults(config).build();
	}
}