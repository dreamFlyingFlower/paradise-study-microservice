package com.wy.gray.instance;

import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 替换默认服务实例筛选逻辑
 *
 * @author 飞花梦影
 * @date 2024-05-29 16:57:18
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class VersionServiceInstanceListSupplierConfiguration {

	@Bean
	ServiceInstanceListSupplier serviceInstanceListSupplier(ConfigurableApplicationContext context) {
		ServiceInstanceListSupplier delegate =
				ServiceInstanceListSupplier.builder().withDiscoveryClient().withCaching().build(context);
		return new VersionServiceInstanceListSupplier(delegate);
	}
}