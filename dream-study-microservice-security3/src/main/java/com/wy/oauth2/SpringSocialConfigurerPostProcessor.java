package com.wy.oauth2;

import java.util.Objects;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.wy.config.SocialConfig;
import com.wy.social.qq.QqSocialSecurityConfigurer;

/**
 * 在{@link SocialConfig#userSocialConfigurer()}中设置跳到的登录页面是Web页面才有的,APP需要另行设置,
 * 此处需要对SpringSocialConfigurer做处理,让SpringSocialConfigurer实例化之后重新设置跳转条件
 * 
 * FIXME 该类需要改造为接口,以便实现不同方式的跳转.此处即便完成也只是对app和web的跳转做了设置,以后还更新更改
 *
 * @author 飞花梦影
 * @date 2021-06-30 19:36:51
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class SpringSocialConfigurerPostProcessor implements BeanPostProcessor {

	/**
	 * 在对象实例化之前的操作
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
	}

	/**
	 * 在对象实例化之后的操作,该类主要是不同项目中有多个跳转时才有用,Web是一个项目,APP是一个项目时才需要配置
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (Objects.equals(beanName, "userSocialConfigurer")) {
			QqSocialSecurityConfigurer configure = (QqSocialSecurityConfigurer) bean;
			configure.signupUrl("/social/app/signUp");
			return configure;
		}
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}
}