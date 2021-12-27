package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

/**
 * springcloud gateway网关,api管理,spring用来替代zuul,需要spring5.0,boot2.0以上
 * http://spring.io/guides/gs/gateway/
 * 
 * @author 飞花梦影
 * @date 2020-12-08 10:52:37
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
@EnableHystrix
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
}