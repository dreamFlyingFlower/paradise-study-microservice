package com.wy.gray;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultRequestContext;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;

import com.dream.lang.StrHelper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 灰度发布,自定义负载均衡模式.通过给请求头添加Version 与 Service Instance 元数据属性进行对比
 * 
 * 在网关启动类使用注解@LoadBalancerClient指定哪些服务使用自定义负载均衡算法
 * 
 * <pre>
 * 1.@LoadBalancerClient(value = "serviceName", configuration = VersionLoadBalancerConfiguration.class),对于指定serviceName启用自定义负载均衡算法;
 * 2.@LoadBalancerClients(defaultConfiguration = VersionLoadBalancerConfiguration.class)为所有服务启用自定义负载均衡算法
 * </pre>
 *
 * @author 飞花梦影
 * @date 2024-05-29 16:38:32
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
public class VersionGrayLoadBalancer implements ReactorServiceInstanceLoadBalancer {

	private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

	private final String serviceId;

	private final AtomicInteger position;

	public VersionGrayLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
			String serviceId) {
		this(serviceInstanceListSupplierProvider, serviceId, new Random().nextInt(1000));
	}

	public VersionGrayLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
			String serviceId, int seedPosition) {
		this.serviceId = serviceId;
		this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
		this.position = new AtomicInteger(seedPosition);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Mono<Response<ServiceInstance>> choose(Request request) {
		ServiceInstanceListSupplier supplier =
				this.serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);

		return supplier.get(request).next().map(serviceInstances -> processInstanceResponse(serviceInstances, request));
	}

	private Response<ServiceInstance> processInstanceResponse(List<ServiceInstance> instances, Request<?> request) {
		if (instances.isEmpty()) {
			log.warn("No servers available for service: " + this.serviceId);
			return new EmptyResponse();
		} else {
			DefaultRequestContext requestContext = (DefaultRequestContext) request.getContext();
			RequestData clientRequest = (RequestData) requestContext.getClientRequest();
			HttpHeaders headers = clientRequest.getHeaders();

			String reqVersion = headers.getFirst("version");
			if (StrHelper.isBlank(reqVersion)) {
				return processRibbonInstanceResponse(instances);
			}

			// 获取请求头中的version属性,然后根据服务实例元数据中的version属性进行匹配,对于符合条件的实例参考Round-Robin-based实现方法
			log.info("request header version : {}", reqVersion);
			// filter service instances
			List<ServiceInstance> serviceInstances =
					instances.stream().filter(instance -> reqVersion.equals(instance.getMetadata().get("version")))
							.collect(Collectors.toList());

			if (serviceInstances.size() > 0) {
				return processRibbonInstanceResponse(serviceInstances);
			} else {
				return processRibbonInstanceResponse(instances);
			}
		}
	}

	/**
	 * 负载均衡器 参考 org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer#getInstanceResponse
	 */
	private Response<ServiceInstance> processRibbonInstanceResponse(List<ServiceInstance> instances) {
		int pos = Math.abs(this.position.incrementAndGet());
		ServiceInstance instance = instances.get(pos % instances.size());
		return new DefaultResponse(instance);
	}
}