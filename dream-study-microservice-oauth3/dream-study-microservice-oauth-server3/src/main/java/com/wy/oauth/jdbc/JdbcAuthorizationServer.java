//package com.wy.oauth.jdbc;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.ClientDetails;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.CompositeTokenGranter;
//import org.springframework.security.oauth2.provider.TokenGranter;
//import org.springframework.security.oauth2.provider.TokenRequest;
//import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
//import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
//import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
//import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
//import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
//import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
//import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
//import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
//import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
//import org.springframework.security.oauth2.provider.token.TokenEnhancer;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//
//import com.wy.oauth.jdbc.config.OAuth2JdbcConfig;
//
//import lombok.AllArgsConstructor;
//
///**
// * OAuth2使用数据库进行认证服务
// * 
// * @author 飞花梦影
// * @date 2021-07-02 15:10:26
// * @git {@link https://github.com/dreamFlyingFlower }
// */
//@Configuration
//@EnableAuthorizationServer
//@AllArgsConstructor
//public class JdbcAuthorizationServer extends AuthorizationServerConfigurerAdapter {
//
//	/**
//	 * 授权管理器
//	 */
//	private AuthenticationManager authenticationManager;
//
//	/**
//	 * 此处为{@link JdbcTokenStore},见{@link OAuth2JdbcConfig}
//	 */
//	private TokenStore tokenStore;
//
//	/**
//	 * 此处为{@link JdbcClientDetailsService},见{@link OAuth2JdbcConfig}
//	 */
//	private JdbcClientDetailsService jdbcClientDetailsService;
//
//	private AuthorizationCodeServices jdbcAuthorizationCodeServices;
//
//	private AuthorizationServerTokenServices jdbcAuthorizationServerTokenServices;
//
//	// private JwtAccessTokenConverter jwtAccessTokenConverter;
//	//
//	// private UserApprovalHandler userApprovalHandler;
//	//
//	// /** 用户认证业务 */
//	// private UserDetailsService userDetailsService;
//
//	/**
//	 * 管理令牌:令牌生成和存储
//	 * 
//	 * 将ClientDetailsServiceConfigurer(从回调AuthorizationServerConfigurer)可以用来在内存或JDBC实现客户的细节服务来定义的
//	 * 
//	 * <pre>
//	 * 客户端的重要属性是:
//	 * clientId:客户端ID,必须
//	 * secret:(可信客户端需要)客户机密码,没有可不填
//	 * scope:客户受限的范围.如果范围未定义或为空(默认),客户端不受范围限制.read write all或其他
//	 * authorizedGrantTypes:授予客户端使用授权的类型,默认值为空
//	 * authorities:授予客户的授权机构(普通的Spring Security权威机构)
//	 * 客户端的详细信息可以通过直接访问底层数据库{@link JdbcClientDetailsService}来更新运行的应用程序
//	 * </pre>
//	 */
//	@Override
//	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//		clients.withClientDetails(jdbcClientDetailsService);
//	}
//
//	/**
//	 * 配置客户端详细信息,客户端标识,客户端秘钥,资源列表等等
//	 * 
//	 * <pre>
//	 * {@link AuthorizationEndpoint}:可以通过以下方式配置支持的授权类型AuthorizationServerEndpointsConfigurer.
//	 * 默认情况下,所有授权类型均受支持,除了密码.以下属性会影响授权类型
//	 * {@link AuthenticationManager}:通过注入密码授权被打开AuthenticationManager
//	 * {@link UserDetailsService}:如果注入UserDetailsService,则刷新令牌授权将包含对用户详细信息的检查,以确保该帐户仍然活动
//	 * {@link AuthorizationCodeServices}:定义AuthorizationCodeServices授权代码授权的授权代码服务
//	 * {@link TokenGranter}:TokenGranter完全控制授予和忽略上述其他属性
//	 * 在XML授予类型中包含作为子元素authorization-server
//	 * 
//	 * /oauth/authorize:可以从该请求中获取所有数据,然后根据需要进行渲染,然后所有用户需要执行的操作都是回复有关批准或拒绝授权的信息
//	 * 请求参数直接传递给UserApprovalHandler,AuthorizationEndpoint
//	 * </pre>
//	 * 
//	 * @param endpoints
//	 * @throws Exception
//	 */
//	@Override
//	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//		endpoints
//				// 认证管理器
//				.authenticationManager(authenticationManager)
//				// .accessTokenConverter(jwtAccessTokenConverter)
//				// .userDetailsService(userDetailsService).userApprovalHandler(userApprovalHandler)
//				// 授权码存储
//				.authorizationCodeServices(jdbcAuthorizationCodeServices)
//				// token服务
//				.tokenServices(jdbcAuthorizationServerTokenServices)
//				// 从数据库查看来源数据
//				.tokenStore(tokenStore)
//				// 支持的请求类型
//				.allowedTokenEndpointRequestMethods(HttpMethod.POST, HttpMethod.GET);
//		// if (null == jwtTokenStore) {
//		// endpoints.tokenStore(redisTokenStore);
//		// } else {
//		// 从jwt查看来源数据
//		// endpoints.tokenStore(jwtTokenStore);
//		// }
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
//				// 允许以表单的方式将token传递到服务
//				.allowFormAuthenticationForClients();
//		// oauthServer.tokenKeyAccess("isAnonymous() ||
//		// hasAuthority('ROLE_TRUSTED_CLIENT')");
//		// oauthServer.checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
//	}
//}