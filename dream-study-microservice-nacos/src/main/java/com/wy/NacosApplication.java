package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Nacos:服务发现于注册,配置中心
 * 
 * @author 飞花梦影
 * @date 2021-10-02 17:16:33
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NacosApplication {

	public static void main(String[] args) {
		SpringApplication.run(NacosApplication.class, args);
	}
}