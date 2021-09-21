package com.wy.crl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Ribbon测试
 * 
 * @author 飞花梦影
 * @date 2021-09-21 15:14:46
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RestController
public class RibbonCrl {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private LoadBalancerClient loadBalancerClient;

	@GetMapping("/movie/{id}")
	public User findById(@PathVariable Long id) {
		return this.restTemplate.getForObject("http://dream-study-microservice-security/user/" + id, User.class);
	}

	@GetMapping("/test")
	public String test() {
		ServiceInstance serviceInstance = this.loadBalancerClient.choose("dream-study-microservice-security");
		System.out.println("111" + ":" + serviceInstance.getServiceId() + ":" + serviceInstance.getHost() + ":"
				+ serviceInstance.getPort());
		ServiceInstance serviceInstance2 = this.loadBalancerClient.choose("dream-study-microservice-security2");
		System.out.println("222" + ":" + serviceInstance2.getServiceId() + ":" + serviceInstance2.getHost() + ":"
				+ serviceInstance2.getPort());
		return "1";
	}
}