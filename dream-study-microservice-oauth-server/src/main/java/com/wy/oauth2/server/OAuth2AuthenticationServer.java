package com.wy.oauth2.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.wy.properties.ConfigProperties;

/**
 * OAuth2认证服务器
 *
 * @author 飞花梦影
 * @date 2021-07-02 15:10:26
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthenticationServer extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenStore redisTokenStore;

	@Autowired
	private TokenStore jwtTokenStore;

	@Autowired
	private JwtAccessTokenConverter jwtAccessTokenConverter;

	@Autowired
	private UserApprovalHandler userApprovalHandler;

	@Autowired
	private ConfigProperties config;

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient(config.getOauth2().getClientIdGuest())
				.secret(config.getOauth2().getClientSecretGuest())
				.authorizedGrantTypes(config.getOauth2().getAuthorizationModes())
				.scopes(config.getOauth2().getScopes());
		// .redirectUris("http://localhost:8080/authorized");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager).accessTokenConverter(jwtAccessTokenConverter)
				.userApprovalHandler(userApprovalHandler);
		if (null == jwtTokenStore) {
			endpoints.tokenStore(redisTokenStore);
		} else {
			endpoints.tokenStore(jwtTokenStore);
		}
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		// 获得签名的signkey,需要身份验证才行,默认是denyAll(),这是SpringSecurity的权限表达式
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}
}