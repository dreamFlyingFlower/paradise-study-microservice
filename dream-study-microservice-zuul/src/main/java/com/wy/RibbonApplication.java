package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

import com.wy.config.RibbonConfig;

/**
 * SpringCloud Ribbon服务调度中心,客户端负载均衡
 * 
 * 自定义Ribbon的负载均衡模式:
 * 
 * <pre>
 * {@link RibbonClient}:使用该注解,修改默认的负载均衡方式.默认是轮询方式{@link RibbonAutoConfiguration}
 * {@link RibbonClient#name()}:指定自定义负载均衡的服务名
 * {@link RibbonClient#configuration()}:指定自定义轮询的类,该类不能被扫描,否则所有的服务都将使用该负载均衡方式
 * 
 * 使用配置文件指定聚在均衡方式,该方式优先级比注解方式的优先级高
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2020-12-03 17:19:13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableEurekaServer
@RibbonClient(name = "dream-study-microservice-service", configuration = RibbonConfig.class)
@SpringBootApplication(exclude = RibbonConfig.class)
public class RibbonApplication {

	public static void main(String[] args) {
		SpringApplication.run(RibbonApplication.class, args);
	}
}