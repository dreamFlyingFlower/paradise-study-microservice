package com.wy.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 使用Ribbon首先要配置RestTemplate,可以使RestTemplate拥有负载均衡的能力
 * 
 * @author 飞花梦影
 * @date 2021-09-21 15:10:20
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class RestTemplateConfig {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}