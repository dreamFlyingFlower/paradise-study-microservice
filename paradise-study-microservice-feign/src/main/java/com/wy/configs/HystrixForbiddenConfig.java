package com.wy.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import feign.Contract;
import feign.Feign;

/**
 * @apiNote 当hystrix全局使用的使用,若是想单独禁用某个feign不使用hystrix,需要单独指定一定configuration,
 *          同时configuraion中需要配置如下
 * @author ParadiseWY
 * @date 2019年8月22日 下午9:54:22
 */
@Configuration
public class HystrixForbiddenConfig {
	@Bean
	public Contract feignContract() {
		return new feign.Contract.Default();
	}

	/**
	 * @apiNote 当正常情况时,返回的应该是HystrixFeign.builder(),表示使用hystrix
	 * @return 当使用本config的时候,表示某个feign禁用hystrix
	 */
	@Bean
	@Scope("prototype")
	public Feign.Builder feignBuilder() {
		return Feign.builder();
	}
}