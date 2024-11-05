package com.wy.config;

import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * 资源服务器配置,配合application-oauth.yml
 * 
 * {@link TokenRelayGatewayFilterFactory}:配置文件中令牌中继TokenRelay,只需要添加一个filter:TokenRelay=;
 * 当网关设置spring.security.oauth2.client.*属性时,会自动创建一个TokenRelayGatewayFilterFactory过滤器,它会从认证信息中获取access_token,并放入下游请求的请求头中
 * 
 * 文档:https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#the-tokenrelay-gatewayfilter-factory
 *
 * @author 飞花梦影
 * @date 2024-10-29 17:49:11
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {

	/**
	 * 配置认证相关的过滤器链
	 *
	 * @param http Spring Security的核心配置类
	 * @return 过滤器链
	 */
	@Bean
	SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
		// 全部请求都需要认证
		http.authorizeExchange(authorize -> authorize.anyExchange().authenticated());
		// 开启OAuth2登录
		http.oauth2Login(Customizer.withDefaults());

		// 设置当前服务为资源服务,解析请求头中的token
		http.oauth2ResourceServer((resourceServer) -> resourceServer
				// 使用jwt
				.jwt(Customizer.withDefaults())
		// 请求未携带Token处理
		// authenticationEntryPoint(this::authenticationEntryPoint)
		// 权限不足处理
		// .accessDeniedHandler(this::accessDeniedHandler)
		// Token解析失败处理
		// .authenticationFailureHandler(this::failureHandler)
		);
		// 禁用csrf与cors
		http.csrf(csrf -> csrf.disable());
		http.cors(cors -> cors.disable());

		return http.build();
	}
}