package com.wy.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

// import com.nimbusds.jose.JWSAlgorithm;
// import com.nimbusds.jose.jwk.JWKSet;
// import com.nimbusds.jose.jwk.KeyUse;
// import com.nimbusds.jose.jwk.RSAKey;
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

	// @Autowired
	// private TokenStore redisTokenStore;

	@Autowired
	private TokenStore jwtTokenStore;

	@Autowired
	private JwtAccessTokenConverter jwtAccessTokenConverter;

	@Autowired
	private UserApprovalHandler userApprovalHandler;

	@Autowired
	private ConfigProperties config;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * 实际生产应该从数据库查询加载到内存中,内存中没有再从数据库查询,数据据没有则说明没有注册
	 */
	// guest:加密->$2a$10$dXULkWNhddYNVH9yQkzwQeJinGE0e22iL4CSEdkm7sRPwa.A27iEi
	// 123456:加密->$2a$10$lg5hcqs13V3c6FVjr1/mjO31clz7fkjlIKnppDhNDdxJVaWxh/xB6
	// password:加密->$2a$10$owjsydvplVmh0wI6f.xOM.4TKBc/CoKYTvX.HmnS6Yeu7qlyukAPO
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				// client的id和密码
				.withClient(config.getOauth2().getClientIdGuest())
				.secret(passwordEncoder.encode(config.getOauth2().getClientSecretGuest()))
				// token有效期
				.accessTokenValiditySeconds(100)
				// 该client可访问的资源服务器ID,每个资源服务器都有一个ID
				.resourceIds("resourceId")
				// 认证模式
				.authorizedGrantTypes(config.getOauth2().getGrantTypes())
				// 授权的范围,每个resource会设置自己的范围
				.scopes(config.getOauth2().getScopes())
				// authorization_code认证模式传递code给client的uri,该uri由client指定
				.redirectUris("http://localhost:55300/oauthClient/oauth/authorized").and().withClient("user1")
				.secret("password").authorizedGrantTypes(config.getOauth2().getGrantTypes())
				.redirectUris("http://localhost:55300/oauthClient/oauth/authorized");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager).accessTokenConverter(jwtAccessTokenConverter)
				.userDetailsService(userDetailsService).userApprovalHandler(userApprovalHandler);
		// if (null == jwtTokenStore) {
		// endpoints.tokenStore(redisTokenStore);
		// } else {
		endpoints.tokenStore(jwtTokenStore);
		// }
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		// 获得签名的signkey,需要身份验证才行,默认是denyAll(),这是SpringSecurity的权限表达式
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()")
				.allowFormAuthenticationForClients();
	}
}