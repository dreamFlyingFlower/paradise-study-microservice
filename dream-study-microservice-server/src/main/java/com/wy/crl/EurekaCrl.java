package com.wy.crl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

/**
 * EurekaClient不能在PostConstruct注解和Scheduled注解标记的类中使用
 * 
 * @author 飞花梦影
 * @date 2021-09-21 18:03:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RestController
@RequestMapping("eureka")
public class EurekaCrl {

	@Autowired
	private EurekaClient eurekaClient;

	@Autowired
	private DiscoveryClient discoveryClient;

	@GetMapping("getInstanceInfo")
	public String getInstanceInfo() {
		// Spring获得服务名列表
		List<String> services = this.discoveryClient.getServices();
		for (String string : services) {
			System.out.println(string);
		}
		// Eureka获得微服务组件的实例信息:微服务名,是否使用安全认证
		InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("dream-study-microservice-service", false);
		return instanceInfo.getHostName();
	}
}