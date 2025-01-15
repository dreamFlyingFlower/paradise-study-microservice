package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OpenTelemetry:功能和Zipkin+Sleuth相同,链路追踪.未完成
 * 
 * @author 飞花梦影
 * @date 2020-12-08 10:52:37
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}