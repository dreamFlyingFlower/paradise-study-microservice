package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * SpringCloud Zuul网关,服务转发
 * 
 * {@link EnableZuulProxy}:开启Zuul网关代理.当不配置zuul.prefix时,默认可通过网关ip:port/被调用服务名/接口url访问原接口
 * 
 * Zuul在大文件上传转发时,需要是在URL之前加上zuul,这样会绕过Spring的拦截机制,如server/upload->zuul/server/upload
 * 
 * @author 飞花梦影
 * @date 2020-12-03 17:19:13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableEurekaClient
@EnableZuulProxy
@SpringBootApplication
public class ZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulApplication.class, args);
	}
}