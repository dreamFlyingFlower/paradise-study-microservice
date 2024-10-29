package com.wy.gray;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 自定义负载均衡器配置实现类
 *
 * @author 飞花梦影
 * @date 2024-05-29 16:43:16
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class VersionLoadBalancerConfiguration {

	@Bean
	ReactorLoadBalancer<ServiceInstance> versionGrayLoadBalancer(Environment environment,
			LoadBalancerClientFactory loadBalancerClientFactory) {
		String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
		return new VersionGrayLoadBalancer(
				loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name);
	}
}