package com.wy.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import feign.Contract;
import feign.Feign;

/**
 * 当hystrix全局使用的使用,若是想单独禁用某个feign不使用hystrix,需要单独指定一定configuration,
 * 同时configuraion中需要配置如下
 * 
 * @author 飞花梦影
 * @date 2019-08-22 21:54:22
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class HystrixForbiddenConfig {

	@Bean
	public Contract feignContract() {
		return new feign.Contract.Default();
	}

	/**
	 * 当正常情况时,返回的应该是HystrixFeign.builder(),表示使用hystrix
	 * 
	 * @return 当使用本config的时候,表示某个feign禁用hystrix
	 */
	@Bean
	@Scope("prototype")
	public Feign.Builder feignBuilder() {
		return Feign.builder();
	}
}