package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Resilience4j:功能和Hystrix相同,主要用来熔断限流
 * 
 * @author 飞花梦影
 * @date 2020-12-08 10:52:37
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}