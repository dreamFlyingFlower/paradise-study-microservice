package com.wy.util;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

/**
 * Spring上下文,可在非Spring环境中获得Spring组件
 * 
 * @author ParadiseWY
 * @date 2019-06-26 22:15:43
 * @git {@link https://github.com/mygodness100}
 */
@Configuration
public class SpringContextUtils implements InitializingBean, ApplicationContextAware {

	private static ApplicationContext applicationContext;

	// 该类为spring特有类,可以拿到反射中方法的形参名.jdk1.8之前是拿不到的.jdk8如何拿要百度咯
	private static final ParameterNameDiscoverer disCoverer;

	static {
		disCoverer = new LocalVariableTableParameterNameDiscoverer();
	}

	public ApplicationContext getContext() {
		return applicationContext;
	}

	public ParameterNameDiscoverer getDisCoverer() {
		return disCoverer;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextUtils.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	/**
	 * 根据bean的class来查找对象
	 * 
	 * @param c 需要查找的类字节码
	 * @return 对象
	 */
	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}

	/**
	 * 根据bean的beanName来查找对象
	 * 
	 * @param beanName spring组件中定义的value,若是不写,默认为类名首字母小写
	 * @return 对象,需强转
	 */
	public static Object getBean(String beanName) {
		return applicationContext.getBean(beanName);
	}

	/**
	 * 根据bean的class来查找所有的对象(包括子类)
	 * 
	 * @param c 父类字节码
	 * @return 所有子类
	 */
	public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
		return applicationContext.getBeansOfType(clazz);
	}
}