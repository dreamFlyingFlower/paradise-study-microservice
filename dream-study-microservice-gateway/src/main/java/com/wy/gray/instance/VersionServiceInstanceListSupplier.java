package com.wy.gray.instance;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.loadbalancer.core.DelegatingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;

import com.dream.lang.StrHelper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * 灰度发布,自定义服务实例筛选逻辑
 * 
 * 在网关启动类使用注解@LoadBalancerClient指定哪些服务使用自定义负载均衡算法
 * 
 * <pre>
 * 1.@LoadBalancerClient(value = "serviceName", configuration = VersionServiceInstanceListSupplierConfiguration.class),对于指定serviceName启用自定义负载均衡算法
 * 2.@LoadBalancerClients(defaultConfiguration = VersionServiceInstanceListSupplierConfiguration.class)为所有服务启用自定义负载均衡算法
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2024-05-29 16:55:14
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
public class VersionServiceInstanceListSupplier extends DelegatingServiceInstanceListSupplier {

	public VersionServiceInstanceListSupplier(ServiceInstanceListSupplier delegate) {
		super(delegate);
	}

	@Override
	public Flux<List<ServiceInstance>> get() {
		return delegate.get();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Flux<List<ServiceInstance>> get(Request request) {
		return delegate.get(request).map(instances -> filteredByVersion(instances, getVersion(request.getContext())));
	}

	/**
	 * filter instance by requestVersion
	 * 
	 * @author javadaily
	 */
	private List<ServiceInstance> filteredByVersion(List<ServiceInstance> instances, String requestVersion) {
		log.info("request version is {}", requestVersion);
		if (StrHelper.isBlank(requestVersion)) {
			return instances;
		}

		List<ServiceInstance> filteredInstances = instances.stream()
				.filter(instance -> requestVersion.equalsIgnoreCase(instance.getMetadata().getOrDefault("version", "")))
				.collect(Collectors.toList());

		if (filteredInstances.size() > 0) {
			return filteredInstances;
		}

		return instances;
	}

	private String getVersion(Object requestContext) {
		if (requestContext == null) {
			return null;
		}
		String version = null;
		if (requestContext instanceof RequestDataContext) {
			version = getVersionFromHeader((RequestDataContext) requestContext);
		}
		return version;
	}

	/**
	 * get version from header
	 * 
	 * @author javadaily
	 */
	private String getVersionFromHeader(RequestDataContext context) {
		if (context.getClientRequest() != null) {
			HttpHeaders headers = context.getClientRequest().getHeaders();
			if (headers != null) {
				// could extract to the properties
				return headers.getFirst("version");
			}
		}
		return null;
	}
}