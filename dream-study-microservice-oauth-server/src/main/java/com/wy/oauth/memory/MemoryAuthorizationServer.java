package com.wy.oauth.memory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.wy.config.SecurityConfig;
import com.wy.oauth.OAuth2Config;
import com.wy.properties.ConfigProperties;

/**
 * 内存模式认证服务器
 *
 * @author 飞花梦影
 * @date 2023-04-03 21:52:24
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Configuration
@EnableAuthorizationServer
public class MemoryAuthorizationServer extends AuthorizationServerConfigurerAdapter {

	/**
	 * 不同的版本可能不一样,高版本一般不要.在 {@link SecurityConfig#authenticationManagerBean}中设置
	 */
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtAccessTokenConverter jwtAccessTokenConverter;

	@Autowired
	private UserApprovalHandler userApprovalHandler;

	@Autowired
	private ConfigProperties config;

	@Autowired
	private UserDetailsService userDetailsService;

	/**
	 * 在 {@link OAuth2Config#passwordEncoder}中设置
	 */
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthorizationCodeServices memoryAuthorizationCodeServices;

	@Autowired
	private AuthorizationServerTokenServices memoryAuthorizationServerTokenServices;

	/**
	 * 实际生产应该从数据库查询加载到内存中,内存中没有再从数据库查询,数据据没有则说明没有注册
	 * 
	 * guest:Bcrpt加密->$2a$10$dXULkWNhddYNVH9yQkzwQeJinGE0e22iL4CSEdkm7sRPwa.A27iEi
	 * 123456:Bcrpt加密->$2a$10$lg5hcqs13V3c6FVjr1/mjO31clz7fkjlIKnppDhNDdxJVaWxh/xB6
	 * password:Bcrpt加密->$2a$10$owjsydvplVmh0wI6f.xOM.4TKBc/CoKYTvX.HmnS6Yeu7qlyukAPO
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// 从内存中读取配置
		clients.inMemory()
				// client的id和密码
				.withClient(config.getOauth2().getClientIdGuest())
				.secret(passwordEncoder.encode(config.getOauth2().getClientSecretGuest()))
				// token有效期
				.accessTokenValiditySeconds(100)
				// 该client可访问的资源服务器ID,每个资源服务器都可以自定义,可不写
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

	/**
	 * 配置Token相关
	 * 
	 * @param endpoints
	 * @throws Exception
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
				// 认证管理器
				.authenticationManager(authenticationManager).accessTokenConverter(jwtAccessTokenConverter)
				.userDetailsService(userDetailsService).userApprovalHandler(userApprovalHandler)
				// 授权码存储
				.authorizationCodeServices(memoryAuthorizationCodeServices)
				// token生成服务
				.tokenServices(memoryAuthorizationServerTokenServices)
				// 以内存的方式存储token
				.tokenStore(new InMemoryTokenStore())
				// 允许方法token的请求方式
				.allowedTokenEndpointRequestMethods(HttpMethod.POST, HttpMethod.GET, HttpMethod.PATCH);
		// if (null == jwtTokenStore) {
		// endpoints.tokenStore(redisTokenStore);
		// } else {
		// 从jwt查看来源数据
		// endpoints.tokenStore(jwtTokenStore);
		// }
	}

	/**
	 * 配置令牌端点(Token Endpoint)的安全约束
	 * 
	 * <pre>
	 * /oauth/authorize:授权端点
	 * /oauth/token:令牌端点
	 * /oauth/confirm_access:用户确认授权提交端点
	 * /oauth/error:授权服务错误信息端点
	 * /oauth/check_token:用于资源服务访问的令牌解析端点
	 * /oauth/token_key:提供公有密匙的端点,如果使用JWT令牌的话
	 * </pre>
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer
				// 开启端口/oauth/token_key的访问权限(允许所有),可获得公钥signKey,默认是denyAll(),是SpringSecurity的权限表达式
				.tokenKeyAccess("permitAll()")
				// 开启端口/oauth/check_token的访问权限(允许所有)
				.checkTokenAccess("permitAll()")
				// 值允许已经认证了的用户访问
				// .checkTokenAccess("isAuthenticated()")
				// 允许客户端进行表单认证
				.allowFormAuthenticationForClients();
	}
}