package com.wy.crl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

	@Autowired
	private HttpHeaders httpHeaders;

	@GetMapping("/movie/{id}")
	public User findById(@PathVariable Long id) {
		// 若请求为post,参数必须是MultiValueMap,且必须用exchange,参数放在HttpEntity中,否则对方接受不到参数
		// MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		// params.add("userName", "admin");
		// params.add("password", "123456");
		// return
		// restTemplate.exchange("http://dream-study-microservice-security/user/",
		// HttpMethod.POST,
		// new HttpEntity<MultiValueMap<String, String>>(params,httpHeaders),
		// User.class).getBody();
		// 若其他微服务中开启了SpringSecurity认证,需要在resttemplate中加入请求头
		return restTemplate.exchange("http://dream-study-microservice-security/user/", HttpMethod.GET,
				new HttpEntity<User>(httpHeaders), User.class).getBody();
	}

	/**
	 * 直接使用SpringCloud自带的loadBalancerClient进行负载均衡,在2020.0.x的SpringCloud版本中,Ribbon组件被移除
	 * 
	 * @return
	 */
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