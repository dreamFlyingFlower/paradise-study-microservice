package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import zipkin2.server.internal.EnableZipkinServer;

/**
 * SpringCloud Zipkin链路追踪,每个需要追踪的微服务都需要加入zipkin.但是实际中,可以直接使用zipkin的jar包启动
 * 
 * {@link EnableZipkinServer}:开启Zipkin链接追踪,内置使用了Undertow服务器,启动之后Web访问ip:port/zipkin
 * 
 * FIXME Zipkin的数据无法持久化,需要借助ElasticSearch进行持久化
 * 
 * @author 飞花梦影
 * @date 2020-12-03 17:19:13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableZipkinServer
@EnableEurekaClient
@SpringBootApplication
public class SleuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(SleuthApplication.class, args);
	}
}