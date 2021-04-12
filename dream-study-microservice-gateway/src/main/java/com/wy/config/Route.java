package com.wy.config;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

@Configuration
public class Route {

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes().route("path_route", r -> r.path("get").uri("http://httpbin.org"))
				.route("host_route", r -> r.host("*.myhost.org").uri("http://httpbin.org"))
				.route("read_body_pred", r -> r.host("*.readbody.org").and()
						.readBody(String.class, p -> p.trim().equalsIgnoreCase("hi"))
						.filters(f -> f.prefixPath("/httpbin").addResponseHeader("X-TestHeader",
								"read_body"))
						.uri("http://www.httpbin.org:8080"))
				.route("rewrite_route",
						r -> r.host("*.rewrite.org")
								.filters(f -> f.rewritePath("/foo/(?<segment>.*)", "/${segment}"))
								.uri("http://httpbin.org"))
				.route("rewrite_request_obj",
						r -> r.host("*.rewriterequestobj.ort").filters(f -> f.prefixPath("/httpbin")
								.addResponseHeader("X-TestHeader", "rewrite_request")
								.modifyRequestBody(String.class, User.class, (exchange, s) -> {
									return Mono.just(new User());
								})).uri("http://httpbin.org"))
				.route("rewrite_request_upper", r -> r.host("*.rewriterequestupper.org")
						.filters(f -> f.prefixPath("/httpbin")
								.addResponseHeader("X-TestHeader", "rewrite_request_upper")
								.modifyRequestBody(String.class, String.class, (exchange, s) -> {
									return Mono.just(s.toUpperCase() + s.toUpperCase());
								}))
						.uri("http://httpbin.org"))
				.route("rewrite_response_upper", r -> r.host("*.rewriteresponseupper.org")
						.filters(f -> f.prefixPath("/httpbin")
								.addResponseHeader("X-TestHeader", "rewrite_response_upper")
								.modifyResponseBody(String.class, String.class, (exchange, s) -> {
									return Mono.just(s.toUpperCase());
								}))
						.uri("http://httpbin.org"))
				.route("rewrite_response_obj",
						r -> r.host("*.rewriteresponseobj.org")
								.filters(f -> f.prefixPath("/httpbin")
										.addResponseHeader("X-TestHeader", "rewrite_response_obj")
										.modifyResponseBody(Map.class, String.class,
												MediaType.TEXT_PLAIN_VALUE, (exchange, map) -> {
													Object data = map.get("data");
													return Mono.just(data.toString());
												})
										.setResponseHeader("Content-Type",
												MediaType.TEXT_PLAIN_VALUE))
								.uri("http://httpbin.org"))
				.route("hystrix_route",
						r -> r.host("*.hystrix.org")
								.filters(f -> f.hystrix(c -> c.setName("slowcmd")))
								.uri("http://httpbin.org"))
				.route("hystrix_fallback_route",
						r -> r.host("*.hystrixfallback.org")
								.filters(f -> f.hystrix(c -> c.setName("slowcmd")
										.setFallbackUri("forward:/hystrixfallback")))
								.uri("http://httpbin.org"))
				.route("limit_route",
						r -> r.host("*.limited.org").and().path("/anything/**")
								.filters(f -> f.requestRateLimiter(
										c -> c.setRateLimiter(redisRateLimiter())))
								.uri("http://httpbin.org"))
				.route(r -> r.path("/image/webp")
						.filters(f -> f.prefixPath("/httpbin").addResponseHeader("X-AnotherHeader",
								"baz"))
						.uri("http://httpbin.org"))
				.route(r -> r.order(-1).host("**.throttle.org").and().path("/get").filters(f -> f
						.prefixPath("/httpbin")
						.filter(new ThrottleGatewayFilter().setCapacity(1).setRefillTokens(1)
								.setRefillPeriod(10).setRefillUnit(TimeUnit.SECONDS)))
						.uri("http://httpbin.org"))
				.route("websocket_route", r -> r.path("/echo").uri("ws://localhost:9000")).build();
	}

	@Bean
	RedisRateLimiter redisRateLimiter() {
		return new RedisRateLimiter(1, 2);
	}

	@Bean
	SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
		return http.httpBasic().and().csrf().disable().authorizeExchange()
				.pathMatchers("/anything/**").authenticated().anyExchange().permitAll().and()
				.build();
	}
}