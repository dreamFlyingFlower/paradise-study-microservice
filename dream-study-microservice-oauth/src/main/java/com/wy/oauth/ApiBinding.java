package com.wy.oauth;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

/**
 * 向资源服务器发送请求获得用户的信息
 *
 * @author 飞花梦影
 * @date 2021-07-01 20:11:52
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public abstract class ApiBinding {

	protected RestTemplate restTemplate;

	public ApiBinding(String accessToken) {
		this.restTemplate = new RestTemplate();
		if (accessToken != null) {
			this.restTemplate.getInterceptors().add(getBearerTokenInterceptor(accessToken));
		} else {
			this.restTemplate.getInterceptors().add(getNoTokenInterceptor());
		}
	}

	/**
	 * 拦截每个到资源服务器的请求,若请求中含有accessToken则执行请求
	 * 
	 * @param accessToken accessToken
	 * @return ClientHttpRequestInterceptor
	 */
	private ClientHttpRequestInterceptor getBearerTokenInterceptor(String accessToken) {
		return new ClientHttpRequestInterceptor() {

			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution)
					throws IOException {
				request.getHeaders().add("Authorization", "Bearer " + accessToken);
				return execution.execute(request, bytes);
			}
		};
	}

	/**
	 * 拦截每个到资源服务器的请求,若请求中不含有accessToken则抛异常
	 * 
	 * @return ClientHttpRequestInterceptor
	 */
	private ClientHttpRequestInterceptor getNoTokenInterceptor() {
		return new ClientHttpRequestInterceptor() {

			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution)
					throws IOException {
				throw new IllegalStateException("Can't access the API without an access token");
			}
		};
	}
}