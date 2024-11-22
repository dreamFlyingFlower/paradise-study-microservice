package com.wy.core;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import com.wy.helpers.SecurityOAuth2Helpers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义解析资源服务器中的Bearer,根据客户端设置不同解析JWT或opaque
 * 
 * 如果使用opaque,需要进行额外配置spring.security.oauth2.resourceserver.opaquetoken.introspection-uri,client-id,client-secret
 *
 * @author 飞花梦影
 * @date 2024-11-20 23:12:45
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class MultitenancyAuthenticationManagerResolver implements AuthenticationManagerResolver<HttpServletRequest> {

	private final JwtDecoder jwtDecoder;

	private final OpaqueTokenIntrospector opaqueTokenIntrospector;

	private final RegisteredClientRepository registeredClientRepository;

	@Override
	public AuthenticationManager resolve(HttpServletRequest request) {
		AuthenticationManager jwt = new ProviderManager(new JwtAuthenticationProvider(jwtDecoder));
		AuthenticationManager opaqueToken =
				new ProviderManager(new OpaqueTokenAuthenticationProvider(opaqueTokenIntrospector));
		return useJwt(request) ? jwt : opaqueToken;
	}

	protected boolean useJwt(HttpServletRequest request) {
		String clientId = SecurityOAuth2Helpers.getClientId(request);
		if (StringUtils.isBlank(clientId)) {
			log.info("当前认证类型无需携带clientId");
			return true;
		}
		RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientId);
		if (null == registeredClient) {
			log.info("{}未在认证服务器中注册,请先注册!", clientId);
			return true;
		}

		OAuth2TokenFormat accessTokenFormat = registeredClient.getTokenSettings().getAccessTokenFormat();
		return OAuth2TokenFormat.SELF_CONTAINED.getValue().equals(accessTokenFormat.getValue());
	}
}