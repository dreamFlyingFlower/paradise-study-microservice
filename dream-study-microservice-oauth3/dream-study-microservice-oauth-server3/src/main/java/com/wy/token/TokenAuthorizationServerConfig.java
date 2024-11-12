package com.wy.token;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import com.wy.config.AuthorizationServerConfig;
import com.wy.oidc.OidcUserInfoService;

import dream.flying.flower.framework.security.constant.ConstSecurity;
import lombok.RequiredArgsConstructor;

/**
 * SpringSecurity6认证服务器Token配置,只展示差异配置,主要配置见{@link AuthorizationServerConfig}
 * 
 * @author 飞花梦影
 * @date 2024-09-18 22:02:11
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class TokenAuthorizationServerConfig {

	/**
	 * 第一种:自定义JWT和IdToken.当前传入参数OidcUserInfoService,自由度更高
	 * 
	 * @param userInfoService OidcUserInfoService
	 * @return OAuth2TokenCustomizer的实例
	 */
	@Bean
	OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(OidcUserInfoService userInfoService) {
		return (context) -> {
			if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
				OidcUserInfo userInfo = userInfoService.loadUser(context.getPrincipal().getName());
				context.getClaims().claims(claims -> claims.putAll(userInfo.getClaims()));
			}
		};
	}

	/**
	 * 第二种:同上.自定义JWT和IdToken,自定义的数据可以从Authentication中获取.2种只需注入一个
	 * 
	 * {@link SecurityContextHolder#getContext()}->{@link SecurityContext#getAuthentication()}
	 * 
	 * @return OAuth2TokenCustomizer的实例
	 */
	@Bean
	OAuth2TokenCustomizer<JwtEncodingContext> oauth2TokenCustomizer() {
		return new CustomizerTokenCustomizer();
	}

	/**
	 * Opaque方式向token中自定义存数据,自定义的数据可以从Authentication中获取,解决的是token存储信息过多的问题
	 * 
	 * Opaque需要客户端设置TokenSettings.builder().accessTokenFormat(OAuth2TokenFormat.REFERENCE),默认为OAuth2TokenFormat.SELF_CONTAINED
	 * 
	 * 同时认证服务器需要配置httpSecurity.oauth2ResourceServer(resourceServer->resourceServer.opaqueToken(Customizer.withDefaults()));,默认为jwt
	 * 
	 * 和上面2种的区别在于存储数据量的多少
	 * 
	 * {@link SecurityContextHolder#getContext()}->{@link SecurityContext#getAuthentication()}
	 * 
	 * @return OAuth2TokenCustomizer
	 */
	@Bean
	OAuth2TokenCustomizer<OAuth2TokenClaimsContext> tokenCustomizer() {
		return context -> {
			OAuth2TokenClaimsSet.Builder claims = context.getClaims();
			// 将权限信息或其他信息放入jwt的claims中,可以从context中拿到client_id
			claims.claim(ConstSecurity.AUTHORITIES_KEY, "自定义参数");
		};
	}
}