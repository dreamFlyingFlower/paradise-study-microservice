package com.wy.config;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authorization.AuthorityAuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;

import dream.flying.flower.collection.CollectionHelper;

/**
 * 客户端Security配置
 * 
 * @auther 飞花梦影
 * @date 2021-07-03 11:00:36
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class SecurtiyConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(request -> request.requestMatchers("/test/**").access((auth, t) -> {
			Authentication authentication = auth.get();

			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			if (CollectionHelper.isEmpty(authorities)) {
				return null;
			}
			GrantedAuthority grantedAuthority = authorities.stream()
					.filter(authority -> authority.getAuthority().equalsIgnoreCase("SCOPE_message.read"))
					.findFirst()
					.orElse(null);

			if (null == grantedAuthority) {
				return new AuthorityAuthorizationDecision(false,
						authorities.stream().map(authority -> authority).collect(Collectors.toList()));
			} else {
				return new AuthorityAuthorizationDecision(true,
						authorities.stream().map(authority -> authority).collect(Collectors.toList()));
			}
		})
				// 其他请求全部都需要校验
				.anyRequest()
				.authenticated())
				.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()));

		return http.build();
	}
}