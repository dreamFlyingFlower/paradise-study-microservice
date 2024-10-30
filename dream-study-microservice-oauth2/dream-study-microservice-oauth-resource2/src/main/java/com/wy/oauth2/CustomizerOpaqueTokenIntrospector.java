package com.wy.oauth2;

import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import com.wy.config.ResourceSecurtiyConfig;

/**
 * 重写OpaqueTokenIntrospector,获得当前访问资源服务器的客户端client_id和client_secret,让{@link ResourceSecurtiyConfig#opaqueTokenIntrospector()}使用
 * 
 * 参照{@link NimbusOpaqueTokenIntrospector}
 *
 * @author 飞花梦影
 * @date 2024-10-30 16:44:50
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class CustomizerOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

	private final String introspectionUri;

	public CustomizerOpaqueTokenIntrospector(String introspectionUri) {
		this.introspectionUri = introspectionUri;
	}

	@Override
	public OAuth2AuthenticatedPrincipal introspect(String token) {
		// TODO 改为从 ThreadLocal 中获取
		String clientId = "";
		String clientSecret = "";

		clientId = clientId == null ? "guest" : clientId;
		clientSecret = clientSecret == null ? "guest" : clientSecret;

		NimbusOpaqueTokenIntrospector delegate =
				new NimbusOpaqueTokenIntrospector(introspectionUri, clientId, clientSecret);

		return delegate.introspect(token);
	}
}