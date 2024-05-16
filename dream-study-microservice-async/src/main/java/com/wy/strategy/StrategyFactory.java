package com.wy.strategy;

import java.util.List;
import java.util.Map;

import org.springframework.aop.support.AopUtils;

import com.dream.collection.CollectionHelper;
import com.dream.collection.MapHelper;
import com.dream.lang.StrHelper;
import com.wy.strategy.context.StrategyContext;

import dream.framework.web.helper.SpringContextHelpers;
import lombok.extern.slf4j.Slf4j;

/**
 * 策略工厂类
 *
 * @author 飞花梦影
 * @date 2024-05-16 14:02:47
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
public class StrategyFactory {

	/**
	 * 执行策略
	 * 
	 * @param type
	 * @param clazz
	 * @return
	 */
	public static <O extends StrategyContext, T extends StrategyService<O>> T doStrategy(String type, Class<T> clazz) {
		if (StrHelper.isEmpty(type) || null == clazz) {
			return null;
		}

		Map<String, T> beanMap = SpringContextHelpers.getBeansOfType(clazz);
		if (MapHelper.isEmpty(beanMap)) {
			log.error("策略实现类不存在,type = {},clazz = {}", type, clazz.getName());
			return null;
		}
		try {
			T defaultStrategy = null;
			for (Map.Entry<String, T> entry : beanMap.entrySet()) {
				// 默认策略
				if (null == defaultStrategy) {
					Class<?> targetClass = AopUtils.getTargetClass(entry.getValue());
					DefaultStrategy annotation = targetClass.getAnnotation(DefaultStrategy.class);
					if (null != annotation) {
						defaultStrategy = entry.getValue();
					}
				}
				// 策略类型列表
				List<String> types = entry.getValue().listType();
				if (CollectionHelper.isNotEmpty(types) && types.contains(type)) {
					return entry.getValue();
				}
			}
			if (null != defaultStrategy) {
				return defaultStrategy;
			}
			log.error("策略类型不存在,type = {},clazz = {}", type, clazz.getName());
		} catch (Exception e) {
			log.error("获取策略实现类失败,type = {},clazz = {}", type, clazz.getName(), e);
		}
		return null;
	}
}