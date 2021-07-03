package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import zipkin2.server.internal.EnableZipkinServer;

/**
 * 通用信息
 * 
 * sharding数据库分库分表技术:https://blog.csdn.net/shijiemozujiejie/article/details/80786231
 * 
 * sleuth:链路追踪,主要用来测试系统中各部分的性能以及改善系统
 * 
 * zipkin:是slenth的web端,可在网页查看sleuth的链路,需要配置sleuth的服务器,需开启@EnableZipkinServer注解
 *
 * @author ParadiseWY
 * @date 2020-12-05 23:58:47
 * @git {@link https://github.com/mygodness100}
 */
@SpringBootApplication
@EnableZipkinServer
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}