package com.wy.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wy.utils.StrUtils;

/**
 * 使用redis缓存,防止缓存击穿
 * 
 * @author ParadiseWY
 * @date 2019-06-23 10:42:48
 * @git {@link https://github.com/mygodness100}
 */
@Component
public class CacheHandlerService {

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	public <T> T getCahce(String key, long expire, TimeUnit unit, TypeReference<T> clazz,
			CacheHandler<T> cacheHandler) {
		String result = String.valueOf(redisTemplate.opsForValue().get(key));
		if (StrUtils.isNotBlank(result)) {
			return JSON.parseObject(result, clazz);
		} else {
			synchronized (this) {
				result = String.valueOf(redisTemplate.opsForValue().get(key));
				if (StrUtils.isNotBlank(result)) {
					return JSON.parseObject(result, clazz);
				} else {
					T t = cacheHandler.handlerCache();
					redisTemplate.opsForValue().set(key, JSON.toJSONString(t), expire, unit);
					return t;
				}
			}
		}
	}
}