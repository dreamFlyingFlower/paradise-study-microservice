package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * springcloud gateway网关,api管理,spring用来替代zuul,需要spring5.0,boot2.0以上
 * http://spring.io/guides/gs/gateway/
 * 
 * @apiNote zuul:全局路由,需要使用@EnableZuulProxy.注册到eureka的服务上之后可直接使用,如
 *          若访问http://192.168.1.170:8080/user/getById,可以改成http://zuulip:port/user所在的服务名/user/getById
 *          但是此种方法还是会暴露服务的名字,需要在application.yml中配置zuul.ignored.services属性
 * 
 * @author ParadiseWY
 * @date 2020-12-08 10:52:37
 * @git {@link https://github.com/mygodness100}
 */
@SpringBootApplication
@EnableZuulProxy
@EnableHystrix
public class ParadiseStudyMicroserviceFeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParadiseStudyMicroserviceFeignApplication.class, args);
	}
}