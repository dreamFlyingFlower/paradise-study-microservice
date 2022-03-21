package com.wy.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.wy.model.User;

/**
 * Cache测试类
 * 
 * @author ParadiseWY
 * @date 2020-12-07 14:43:36
 * @git {@link https://github.com/mygodness100}
 */
@Service
@CacheConfig
public class CacheService {

	/**
	 * 将方法的结果进行缓存,若再次调用,将不再从数据库取值
	 * 
	 * @param userId 缓存的key值
	 * @return 结果
	 */
	// @Cacheable(value = "cacheTest", key = "#userId")
	// 使用自定义的keygenerator,key和keygenerator只能用一个
	// @Cacheable(value = "cacheTest", keyGenerator = "customizeKeyGenerator")
	// 当userId大于0的时候才缓存,可以用and或者or连接过个判断
	// @Cacheable(value = "cacheTest", key = "#userId", condition = "#userId > 0")
	// 可添加常量.如key="'"+ee+"'+#userId"
	// #result可以拿到方法的结果,同时可以对结果进行操作判断
	@Cacheable(value = "cacheTest", key = "#userId", unless = "#result == null")
	public User getCache(Integer userId) {
		return User.builder().userId(1).build();
	}

	/**
	 * 先调用方法,之后再将结果缓存,注意更新的key是否和查询的key相同,若不同就会产生一个新的缓存
	 * 
	 * @param user 缓存参数
	 * @return 是否更新成功
	 */
	@CachePut(value = "cacheTest", key = "#user.userId")
	public int editCache(User user) {
		return 1;
	}

	/**
	 * 清除缓存
	 * 
	 * @param userId 需要清除缓存的key
	 * @return
	 */
	// 删除单个符合key的缓存
	// @CacheEvict(value = "cacheTest", key = "#userId")
	// 删除所有缓存,此时key值无效
	// @CacheEvict(value = "cacheTest", key = "#userId",allEntries = true)
	@CacheEvict(value = "cacheTest", key = "#userId", beforeInvocation = false)
	public User deleteCache(Integer userId) {
		return null;
	}

	@Caching(cacheable = @Cacheable, evict = @CacheEvict, put = @CachePut)
	public User allCache(Integer userId) {
		return new User();
	}
}