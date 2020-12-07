package com.wy.config;

/**
 * 应对redis缓存击穿,需要使用redis的service需要实现该接口或直接在匿名方法中调用
 * 
 * @author ParadiseWY
 * @date 2019-06-23 10:36:13
 * @git {@link https://github.com/mygodness100}
 */
@FunctionalInterface
public interface CacheHandler<T> {

	T handlerCache();
}