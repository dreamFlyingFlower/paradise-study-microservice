package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * SpringCloud Stream,一个构建消息驱动微服务的框架
 * 
 * 整合消息中间件,降低微服务和消息中间件的耦合性,在不同消息中间件间切换.目前只支持rabbitmq 和 kafka
 * 
 * @author 飞花梦影
 * @date 2020-12-03 17:19:13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableEurekaClient
@SpringBootApplication
public class StreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(StreamApplication.class, args);
	}
}