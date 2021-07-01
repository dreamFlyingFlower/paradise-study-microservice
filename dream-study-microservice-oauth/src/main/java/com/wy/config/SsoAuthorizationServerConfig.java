package com.wy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * SSO单点登录OAuth2服务实现
 *
 * @author 飞花梦影
 * @date 2021-07-01 12:04:23
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
@EnableAuthorizationServer
public class SsoAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private JwtAccessTokenConverter jwtAccessTokenConverter;

	@Autowired
	private JwtTokenStore jwtTokenStore;

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		// 获得签名的signkey,需要身份验证才行,默认是denyAll(),这是SpringSecurity的权限表达式
		security.tokenKeyAccess("isAuthenticated()");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				// 配置clientid
				.withClient("test_client")
				// 配置密钥
				.secret("test_secret")
				// 设置过期时间
				.accessTokenValiditySeconds(300)
				// 支持的授权模式
				.authorizedGrantTypes("refresh_token", "authorization_code")
				// 客户端请求权限.如果客户端不传scope,则直接给服务配置的权限;如果传了,则必须在配置的权限集合内
				.scopes("all")
				// 使用and()可以添加多个客户端授权
				.and().withClient("test_client1").secret("test_secret1").accessTokenValiditySeconds(300)
				.authorizedGrantTypes("refresh_token", "authorization_code");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(jwtTokenStore).accessTokenConverter(jwtAccessTokenConverter);
	}
}