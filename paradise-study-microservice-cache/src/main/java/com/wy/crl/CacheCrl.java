package com.wy.crl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wy.model.User;
import com.wy.result.Result;
import com.wy.service.CacheService;

/**
 * 缓存测试API
 * 
 * @author ParadiseWY
 * @date 2020-12-06 00:05:33
 * @git {@link https://github.com/mygodness100}
 */
@RestController
@RequestMapping("cache")
public class CacheCrl {

	@Autowired
	private CacheService cacheService;

	@GetMapping("getCache/{userId}")
	public Result<?> getCache(@PathVariable Integer userId) {
		return Result.ok(cacheService.getCache(userId));
	}

	@GetMapping("editCache")
	public Result<?> editCache(User user) {
		return Result.ok(cacheService.editCache(user));
	}

	@GetMapping("deleteCache/{userId}")
	public Result<?> deleteCache(@PathVariable Integer userId) {
		return Result.ok(cacheService.deleteCache(userId));
	}

	@GetMapping("allCache")
	public Result<?> allCache(Integer userId) {
		return Result.ok(cacheService.allCache(userId));
	}
}