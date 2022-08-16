package com.wy.fallback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.alibaba.fastjson.JSON;
import com.wy.service.impl.FeignServiceImpl;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 该类需要实现所有方法,并加入到spring的环境中,可直接写匿名内部内,也可以实现该类,最好是实现该类,可复用父类
 * 
 * @author paradiseWy
 */
@Slf4j
@SuppressWarnings("unchecked")
public abstract class FeignFallback<T extends FeignServiceImpl> implements FallbackFactory<T> {

	private Class<T> clazz;

	public FeignFallback() {
		ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
		Type type = parameterizedType.getActualTypeArguments()[0];
		clazz = (Class<T>) type;
	}

	@Override
	public T create(Throwable cause) {
		log.error(JSON.toJSONString(cause));
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}