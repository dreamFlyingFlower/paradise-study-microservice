//package com.wy.oauth.memory;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
//import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//
//import com.wy.properties.OAuth2MemoryProperties;
//
//import lombok.AllArgsConstructor;
//
///**
// * 内存模式用户名密码认证服务器
// * 
// * @author 飞花梦影
// * @date 2023-04-03 21:52:24
// * @git {@link https://gitee.com/dreamFlyingFlower}
// */
//// @Configuration
//// @EnableAuthorizationServer
//@AllArgsConstructor
//public class MemoryAuthorizationServer extends AuthorizationServerConfigurerAdapter {
//
//	private final AuthenticationManager authenticationManager;
//
//	private final OAuth2MemoryProperties oauth2MemoryProperties;
//
//	private final PasswordEncoder passwordEncoder;
//
//	private final AuthorizationCodeServices memoryAuthorizationCodeServices;
//
//	private final TokenStore tokenStore;
//
//	@Qualifier("memoryAuthorizationServerTokenServices")
//	private final AuthorizationServerTokenServices memoryAuthorizationServerTokenServices;
//
//	/**
//	 * 实际生产应该从数据库查询加载到内存中,内存中没有再从数据库查询,数据据没有则说明没有注册
//	 * 
//	 * guest:Bcrpt加密->$2a$10$ahYHTCNIo7RxlMObpPspHOboa1EKKCNreHhBSyGx71.003149BPey
//	 * 123456:Bcrpt加密->$2a$10$dEwuUZzYSIJOx7.Lg6VuSuCNF2DGDOg30BV9bHqtHGIRuRZ38XoTi
//	 * password:Bcrpt加密->$2a$10$owjsydvplVmh0wI6f.xOM.4TKBc/CoKYTvX.HmnS6Yeu7qlyukAPO
//	 */
//	@Override
//	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//		// 从内存中读取配置
//		clients.inMemory()
//				// client的id和密码
//				.withClient(oauth2MemoryProperties.getClientIdGuest())
//				.secret(passwordEncoder.encode(oauth2MemoryProperties.getClientSecretGuest()))
//				// token有效期
//				.accessTokenValiditySeconds(100)
//				// 该client可访问的资源服务器ID,每个资源服务器都可以自定义,可不写
//				.resourceIds("oauth-resource")
//				// 认证模式
//				.authorizedGrantTypes(oauth2MemoryProperties.getGrantTypes())
//				// 授权的范围,每个resource会设置自己的范围
//				.scopes(oauth2MemoryProperties.getScopes());
//	}
//
//	/**
//	 * 配置Token相关
//	 * 
//	 * @param endpoints
//	 * @throws Exception
//	 */
//	@Override
//	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//		endpoints
//				// 认证管理器
//				.authenticationManager(authenticationManager)
//				// 授权码存储
//				.authorizationCodeServices(memoryAuthorizationCodeServices)
//				// token生成服务
//				.tokenServices(memoryAuthorizationServerTokenServices)
//				// 以内存的方式存储token
//				// .tokenStore(new InMemoryTokenStore())
//				// 以JWT的方式存储token
//				.tokenStore(tokenStore)
//				// 允许获得token的请求方式
//				.allowedTokenEndpointRequestMethods(HttpMethod.POST, HttpMethod.GET);
//	}
//
//	/**
//	 * 配置令牌端点(Token Endpoint)的安全约束
//	 * 
//	 * <pre>
//	 * /oauth/authorize:授权端点
//	 * /oauth/token:令牌端点
//	 * /oauth/confirm_access:用户确认授权提交端点
//	 * /oauth/error:授权服务错误信息端点
//	 * /oauth/check_token:用于资源服务访问的令牌解析端点
//	 * /oauth/token_key:提供公有密匙的端点,如果使用JWT令牌的话
//	 * </pre>
//	 */
//	@Override
//	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
//		oauthServer
//				// 开启端口/oauth/token_key的访问权限(允许所有),可获得公钥signKey,默认是denyAll(),是SpringSecurity的权限表达式
//				.tokenKeyAccess("permitAll()")
//				// 开启端口/oauth/check_token的访问权限(允许所有)
//				.checkTokenAccess("permitAll()")
//				// 值允许已经认证了的用户访问
//				// .checkTokenAccess("isAuthenticated()")
//				// 允许客户端进行表单认证
//				.allowFormAuthenticationForClients();
//	}
//}