package com.wy.config;

import java.time.Duration;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorityAuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;

import com.wy.oauth2.CustomizerBearerTokenResolver;
import com.wy.properties.ConfigProperties;

import dream.flying.flower.collection.CollectionHelper;
import dream.flying.flower.framework.security.constant.ConstAuthorization;
import lombok.AllArgsConstructor;

/**
 * Web资源服务器Security配置
 * 
 * @auther 飞花梦影
 * @date 2021-07-03 11:00:36
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
@AllArgsConstructor
public class WebResourceConfig {

	private final ConfigProperties configProperties;

	private final RestTemplateBuilder restTemplateBuilder;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(request -> request
				// 白名单中的请求直接允许,被放行的接口上不能有权限注解,否则失效
				.requestMatchers(configProperties.getSecurity().getPermitAllSources())
				.permitAll()
				.requestMatchers("/test/**")
				.permitAll()
				// 需要权限校验的URL
				.requestMatchers("/admin/**")
				// 权限校验:认证对象,当前request上下文
				.access((auth, context) -> {
					Authentication authentication = auth.get();
					if (authentication.isAuthenticated()) {
						return null;
					}

					HttpServletRequest httpServletRequest = context.getRequest();
					System.out.println(httpServletRequest.getRequestURI());

					Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
					if (CollectionHelper.isEmpty(authorities)) {
						return null;
					}
					GrantedAuthority grantedAuthority = authorities.stream()
							.filter(authority -> authority.getAuthority().equalsIgnoreCase("SCOPE_message.read"))
							.findFirst()
							.orElse(null);

					if (null == grantedAuthority) {
						// 权限是否通过:第一个参数表示是否通过,false不通过
						return new AuthorityAuthorizationDecision(false,
								authorities.stream().map(authority -> authority).collect(Collectors.toList()));
					} else {
						return new AuthorityAuthorizationDecision(true,
								authorities.stream().map(authority -> authority).collect(Collectors.toList()));
					}
				})
				.requestMatchers(HttpMethod.GET, "/**")
				.hasAnyRole("GUEST")
				.requestMatchers(HttpMethod.POST, "/**")
				.hasAnyAuthority("GUEST")
				// .antMatchers(HttpMethod.DELETE, "/**").access("#oauth2.hasScope('write')")
				// 其他请求全部都需要校验
				.anyRequest()
				.authenticated())
				// // 禁用session
				// .sessionManagement(session ->
				// session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// // 指定请求头参数
				// .headers(header -> header
				// // 指定请求头
				// .addHeaderWriter((request, response) -> {
				// // 允许跨域
				// response.addHeader("Access-Control-Allow-Origin", "*");
				// // 如果是跨域的预检请求,则原封不动向下传达请求头信息
				// if (request.getMethod().equals("OPTIONS")) {
				// response.setHeader("Access-Control-Allow-Methods",
				// request.getHeader("AccessControl-Request-Method"));
				// response.setHeader("Access-Control-Allow-Headers",
				// request.getHeader("AccessControl-Request-Headers"));
				// }
				// }))
				.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
						// 自定义取token的方式
						.bearerTokenResolver(new CustomizerBearerTokenResolver())
						// 认证服务器对客户端的配置tokenSettings.accessTokenFormat必须为OAuth2TokenFormat.SELF_CONTAINED
						.jwt(jwt -> jwt.decoder(jwtDecoder()))
						// opaque校验,认证服务器对客户端的配置tokenSettings.accessTokenFormat必须为OAuth2TokenFormat.REFERENCE
						.opaqueToken(opaqueToken -> opaqueToken.introspector(opaqueTokenIntrospector())));

		return http.build();
	}

	/**
	 * 自定义jwt解析器,设置解析出来的权限信息的前缀与在jwt中的key
	 *
	 * @return jwt解析器 JwtAuthenticationConverter
	 */
	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		// 设置解析权限(scope)信息的前缀,设置为空是去掉前缀,如果不去掉,则scope参数从client传递时需带上SCOPE_
		grantedAuthoritiesConverter.setAuthorityPrefix("");
		// 设置权限信息在jwt claims中的key
		grantedAuthoritiesConverter.setAuthoritiesClaimName(ConstAuthorization.AUTHORITIES_KEY);

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	public JwtDecoder jwtDecoder() {
		// 授权服务器 jwk 的信息
		NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri("http://127.0.0.1:17127/oauth2/jwks")
				// 设置获取 jwk 信息的超时时间
				.restOperations(restTemplateBuilder.setReadTimeout(Duration.ofSeconds(3))
						.setConnectTimeout(Duration.ofSeconds(3))
						.build())
				.build();
		// 对jwt进行校验
		decoder.setJwtValidator(JwtValidators.createDefault());
		return decoder;
	}

	public NimbusOpaqueTokenIntrospector opaqueTokenIntrospector() {
		// 向认证服务器请求解析token,此处的client_id和client_secret固定,若需要根据请求来,可参照CustomizerOpaqueTokenIntrospector
		return new NimbusOpaqueTokenIntrospector("http://localhost:17127/oauth2/introspect", "guest", "guest");
	}
}