package com.wy.config;

import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.dream.helper.ArrayHelper;
import com.wy.annotation.AsyncExecute;
import com.wy.dto.ProxyMethodDTO;

import dream.framework.web.helper.SpringContextHelpers;

/**
 * 异步执行初始化
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:54:17
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
@Order(value = -1)
@ConditionalOnProperty(prefix = "dream.async", value = "enabled", havingValue = "true")
public class AsyncInitBean implements BeanPostProcessor {

	@Autowired
	private AsyncProxy asyncProxy;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
		if (ArrayHelper.isEmpty(methods)) {
			return bean;
		}
		for (Method method : methods) {
			AsyncExecute asyncExec = AnnotationUtils.findAnnotation(method, AsyncExecute.class);
			if (null == asyncExec) {
				continue;
			}
			ProxyMethodDTO proxyMethodDto = new ProxyMethodDTO();
			proxyMethodDto.setBean(SpringContextHelpers.getBean(beanName));
			proxyMethodDto.setMethod(method);
			// 生成方法唯一标识
			String key = asyncProxy.getAsyncMethodKey(bean, method);
			asyncProxy.setProxyMethod(key, proxyMethodDto);
		}
		return bean;
	}
}