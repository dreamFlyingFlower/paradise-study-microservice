package com.wy.configs;

import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Contract;
import feign.Logger;

/**
 * 在FeignClient注解中有个configuration属性,该属性会自定义被注解修改的接口的上下文默认配置,不能在启动时被扫描
 * 
 * Feign上下文的默认配置是{@link FeignClientConfiguration}
 * 
 * @author 飞花梦影
 * @date 2021-09-21 16:45:03
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class FeignConfig {

	/**
	 * 自定义Feign上下文中的Contract,此时FeignClient修饰的接口的方法需要使用RequestLine注解
	 * 
	 * @return 返回Contract.该配置最好不要重写,不能使用原生的SpringMVC注解.若要写,则最好单独配置
	 */
	@Bean
	public Contract feignContract() {
		return new feign.Contract.Default();
	}

	/**
	 * 自定义Feign上下文中的日志
	 * 
	 * @return
	 */
	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}
}