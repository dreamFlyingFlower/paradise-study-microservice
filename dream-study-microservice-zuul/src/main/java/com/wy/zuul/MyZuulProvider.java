package com.wy.zuul;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

/**
 * Zuul服务自身降级,必须实现{@link FallbackProvider}
 *
 * @author 飞花梦影
 * @date 2021-09-23 15:42:20
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Component
public class MyZuulProvider implements FallbackProvider {

	@Override
	public String getRoute() {
		// *表示所有服务都降低,若只降低指定服务,可写服务名,多个逗号隔开
		return "*";
	}

	@Override
	public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
		// 匿名内部类实现ClientHttpResponse
		return new ClientHttpResponse() {

			@Override
			public HttpHeaders getHeaders() {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				return headers;
			}

			@Override
			public InputStream getBody() throws IOException {
				// 响应体
				System.out.println(MyZuulProvider.this.getRoute());
				return new ByteArrayInputStream("服务正在维护,请稍后再试.".getBytes());
			}

			@Override
			public HttpStatus getStatusCode() throws IOException {
				return HttpStatus.BAD_REQUEST;
			}

			@Override
			public int getRawStatusCode() throws IOException {
				return HttpStatus.BAD_REQUEST.value();
			}

			@Override
			public String getStatusText() throws IOException {
				return HttpStatus.BAD_REQUEST.getReasonPhrase();
			}

			@Override
			public void close() {
				// 关闭了response流,可以做一些清理工作
			}
		};
	}
}