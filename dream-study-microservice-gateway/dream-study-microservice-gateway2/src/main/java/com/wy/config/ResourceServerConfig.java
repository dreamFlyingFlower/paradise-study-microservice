package com.wy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

/**
 * 网关资源服务器
 * 
 * 开启方法级别鉴权的注解变了,webflux的注解和webmvc的注解不一样,并且过滤器链也换成SecurityWebFilterChain了,jwt自定义解析器的方式也不一致
 * 
 * EnableReactiveMethodSecurity注解文档:https://docs.spring.io/spring-security/reference/reactive/authorization/method.html#jc-enable-reactive-method-security
 * JWT解析适配器文档:https://docs.spring.io/spring-security/reference/reactive/oauth2/resource-server/jwt.html#webflux-oauth2resourceserver-jwt-authorization-extraction
 *
 * @author 飞花梦影
 * @date 2024-11-04 10:43:05
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class ResourceServerConfig {

	/**
	 * 配置认证相关的过滤器链
	 *
	 * @param http Spring Security的核心配置类
	 * @return 过滤器链
	 */
	@Bean
	public SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
		// 禁用csrf与cors
		http.csrf(ServerHttpSecurity.CsrfSpec::disable);
		http.cors(ServerHttpSecurity.CorsSpec::disable);

		// 开启全局验证
		http.authorizeExchange((authorize) -> authorize
				// 全部需要认证
				.anyExchange()
				.authenticated());

		// 开启OAuth2登录
		http.oauth2Login(Customizer.withDefaults());

		// 设置当前服务为资源服务,解析请求头中的token
		http.oauth2ResourceServer((resourceServer) -> resourceServer
				// 使用jwt
				.jwt(jwt -> jwt
						// 请求中携带token访问时会触发该解析器适配器
						.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
		/*
		 * // xhr请求未携带Token处理 .authenticationEntryPoint(this::authenticationEntryPoint)
		 * // 权限不足处理 .accessDeniedHandler(this::accessDeniedHandler) // Token解析失败处理
		 * .authenticationFailureHandler(this::failureHandler)
		 */
		);

		return http.build();
	}

	/**
	 * 自定义jwt解析器,设置解析出来的权限信息的前缀与在jwt中的key
	 *
	 * @return jwt解析器适配器 ReactiveJwtAuthenticationConverterAdapter
	 */
	public Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		// 设置解析权限信息的前缀,设置为空是去掉前缀
		grantedAuthoritiesConverter.setAuthorityPrefix("");
		// 设置权限信息在jwt claims中的key
		grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
	}
}