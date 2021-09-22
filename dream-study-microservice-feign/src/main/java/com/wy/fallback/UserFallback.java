package com.wy.fallback;

import java.util.List;

import org.springframework.stereotype.Component;

import com.wy.feign.FeignUserService;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * Hystrix的工厂回退机制,需要实现FallbackFactory,传入Feign接口作为参数,并实现里面的降级方法
 * 
 * @author 飞花梦影
 * @date 2021-09-22 23:39:26
 * @git {@link https://github.com/dreamFlyingFlower}
 */
// public class UserFallback extends FeignFallback<UserServiceImpl> { // 未完成
@Component
@Slf4j
public class UserFallback implements FallbackFactory<FeignUserService> {

	@Override
	public FeignUserService create(Throwable cause) {
		log.error("fallback; reason was: {}", cause.getMessage());
		return new FeignUserServiceFactory() {

			@Override
			public Object removes(List<String> ids) {
				return null;
			}

			@Override
			public Object remove(String id) {
				return null;
			}

			@Override
			public Object getList(Object page) {
				return null;
			}

			@Override
			public Object getByParams(String username, Integer age) {
				return null;
			}

			@Override
			public Object getById(String id) {
				return null;
			}

			@Override
			public Object edit(Object entity) {
				return null;
			}

			@Override
			public Object create(Object entity) {
				return null;
			}

			@Override
			public Object checkUnique(String username) {
				return null;
			}
		};
	}
}