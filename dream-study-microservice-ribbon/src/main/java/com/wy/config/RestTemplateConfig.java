package com.wy.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
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

	/**
	 * 若其他微服务中使用了SpringSecurity认证,RestTemplate需要在Http头信息配置实现安全认证
	 *
	 * @return 请求头
	 */
	@Bean
	public HttpHeaders getHeaders() {
		// 定义HTTP的头信息
		HttpHeaders headers = new HttpHeaders();
		// 认证的原始信息,由用户名和密码拼接而成
		String auth = "cat:123456";
		// 进行加密处理
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		String authHeader = "Basic " + new String(encodedAuth);
		headers.set("Authorization", authHeader);
		return headers;
	}
}