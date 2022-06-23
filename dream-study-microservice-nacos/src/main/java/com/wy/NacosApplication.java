package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import com.alibaba.cloud.sentinel.rest.SentinelClientHttpResponse;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * Nacos:服务发现于注册,配置中心
 * 
 * Sentinel:
 * 
 * <pre>
 * {@link SentinelResource}:对单个方法进行降级限流配置
 * {@link SentinelResource#blockHandler()}:限流时调用的方法.若配置了blockHandlerClass(),则blockHandler()必须是blockHandlerClass中的静态方法,
 * 		且该静态方法的参数以及返回值同{@link ClientHttpRequestInterceptor#intercept}
 * {@link SentinelResource#fallback()}:降级时调用的方法,使用同blockHandler()
 * {@link SentinelRestTemplate}:配置在RestTemplate上,可以统一配置降级限流策略,不需要每个方法都加{@link SentinelResource}
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2021-10-02 17:16:33
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NacosApplication {

	public static void main(String[] args) {
		SpringApplication.run(NacosApplication.class, args);
	}

	@SentinelRestTemplate(fallbackClass = ExceptionUtils.class, fallback = "handlerFallback")
	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	static class ExceptionUtils {

		/**
		 * 限流方法
		 * 
		 * @param request
		 * @param body
		 * @param execution
		 * @param blockException
		 * @return
		 */
		public static SentinelClientHttpResponse handlerBlock(HttpRequest request, byte[] body,
				ClientHttpRequestExecution execution, BlockException blockException) {
			return new SentinelClientHttpResponse(blockException.getMessage());
		}

		/**
		 * 降级方法
		 * 
		 * @param request
		 * @param body
		 * @param execution
		 * @param blockException
		 * @return
		 */
		public static SentinelClientHttpResponse handlerFallback(HttpRequest request, byte[] body,
				ClientHttpRequestExecution execution, BlockException blockException) {
			return new SentinelClientHttpResponse(blockException.getMessage());
		}
	}
}