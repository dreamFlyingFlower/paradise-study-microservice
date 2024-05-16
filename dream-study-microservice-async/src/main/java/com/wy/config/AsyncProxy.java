package com.wy.config;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.dream.digest.DigestHelper;
import com.wy.dto.ProxyMethodDTO;

/**
 * 代理类
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:55:26
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
public class AsyncProxy {

	/**
	 * 代理类方法
	 */
	private static final Map<String, ProxyMethodDTO> PROXY_METHOD_MAP = new ConcurrentHashMap<>();

	/**
	 * 设置代理方法
	 * 
	 * @param key
	 * @param proxyMethodDto
	 */
	public void setProxyMethod(String key, ProxyMethodDTO proxyMethodDto) {
		AsyncProxy.PROXY_METHOD_MAP.put(key, proxyMethodDto);
	}

	/**
	 * 获取代理方法
	 * 
	 * @param key
	 * @return
	 */
	public ProxyMethodDTO getProxyMethod(String key) {
		return AsyncProxy.PROXY_METHOD_MAP.get(key);
	}

	/**
	 * 获取异步方法唯一标识
	 *
	 * @param bean
	 * @param method
	 * @return
	 */
	public String getAsyncMethodKey(Object bean, Method method) {
		if (method.toString().contains(bean.getClass().getName())) {
			// 异步执行注解在当前类方法上面
			return DigestHelper.MD5Hex(method.toString());
		} else {
			// 异步执行注解在基类方法上面
			return DigestHelper.MD5Hex(bean.getClass().getSimpleName() + "#" + method);
		}
	}
}