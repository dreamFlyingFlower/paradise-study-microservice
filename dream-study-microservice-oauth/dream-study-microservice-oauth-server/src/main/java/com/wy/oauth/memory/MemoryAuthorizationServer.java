package com.wy.oauth.memory;

import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.wy.properties.OAuth2MemoryProperties;

import lombok.AllArgsConstructor;

/**
 * {@link Deprecated}:在SpringSecurity5.7以上版本中,该认证方式被废弃
 * 
 * OAuth2使用内存进行认证服务
 * 
 * @author 飞花梦影
 * @date 2023-04-03 21:52:24
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Deprecated
@Configuration
@EnableAuthorizationServer
@AllArgsConstructor
public class MemoryAuthorizationServer extends AuthorizationServerConfigurerAdapter {

	private final AuthenticationManager authenticationManager;

	private final OAuth2MemoryProperties oauth2MemoryProperties;

	private final PasswordEncoder passwordEncoder;

	private final AuthorizationCodeServices memoryAuthorizationCodeServices;

	private final TokenStore tokenStore;

	@Qualifier("memoryAuthorizationServerTokenServices")
	private final AuthorizationServerTokenServices memoryAuthorizationServerTokenServices;

	// private final PasswordEncoder passwordEncoder;

	/**
	 * 客户端管理令牌:令牌生成和存储
	 * 
	 * 实际生产应该从数据库查询加载到内存中,内存中没有再从数据库查询,数据据没有则说明没有注册
	 * 
	 * 重写该方法后,如果使用内存方式存储客户端信息,则在配置文件中的security.oauth2.client.client-id和client-secret都将无效
	 * 
	 * guest:Bcrpt加密->$2a$10$ahYHTCNIo7RxlMObpPspHOboa1EKKCNreHhBSyGx71.003149BPey
	 * 123456:Bcrpt加密->$2a$10$dEwuUZzYSIJOx7.Lg6VuSuCNF2DGDOg30BV9bHqtHGIRuRZ38XoTi
	 * password:Bcrpt加密->$2a$10$owjsydvplVmh0wI6f.xOM.4TKBc/CoKYTvX.HmnS6Yeu7qlyukAPO
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// 配置存储到内存的客户端信息
		clients.inMemory()
				// clientId:客户端ID,必须
				.withClient(oauth2MemoryProperties.getClientIdGuest())
				// secret:(可信客户端需要)客户机密码,没有可不填
				.secret(passwordEncoder.encode(oauth2MemoryProperties.getClientSecretGuest()))
				// authorizedGrantTypes:授予客户端使用授权的类型,默认值为空
				.authorizedGrantTypes(oauth2MemoryProperties.getGrantTypes())
				// token有效期
				.accessTokenValiditySeconds(60 * 60 * 2)
				// scope:客户受限的范围.如果范围未定义或为空(默认),客户端不受范围限制.read write all或其他
				.scopes(oauth2MemoryProperties.getScopes())
				// 可访问的资源服务器编号,每个资源服务器都可以自定义,可不写
				.resourceIds("oauth-resource")
				// authorities:授予客户的授权机构(普通的Spring Security权威机构)
				.authorities("authority1", "authority2")
				// 执行认证操作的时候会跳转到一个授权页面
				.autoApprove(true)
				// 重定向地址
				.redirectUris("http://")
		// 该方法可以添加多个客户端信息
		// .and().withClient("test1")
		;
	}

	/**
	 * 配置客户端详细信息,客户端标识,客户端秘钥,资源列表等,TokenEndpoint等入口点参数配置
	 * 
	 * <pre>
	 * {@link AuthorizationEndpoint}:可以通过以下方式配置支持的授权类型AuthorizationServerEndpointsConfigurer.
	 * 默认情况下,所有授权类型均受支持,除了密码.以下属性会影响授权类型
	 * {@link AuthenticationManager}:通过注入密码授权被打开AuthenticationManager
	 * {@link UserDetailsService}:如果注入UserDetailsService,则刷新令牌授权将包含对用户详细信息的检查,以确保该帐户仍然活动
	 * {@link AuthorizationCodeServices}:定义AuthorizationCodeServices授权代码授权的授权代码服务
	 * {@link TokenGranter}:TokenGranter完全控制授予和忽略上述其他属性
	 * 在XML授予类型中包含作为子元素authorization-server
	 * 
	 * /oauth/authorize:可以从该请求中获取所有数据,然后根据需要进行渲染,然后所有用户需要执行的操作都是回复有关批准或拒绝授权的信息
	 * 请求参数直接传递给UserApprovalHandler,AuthorizationEndpoint
	 * </pre>
	 * 
	 * @param endpoints
	 * @throws Exception
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
				// 认证管理器
				.authenticationManager(authenticationManager)
				// 授权码存储,若无refresh_token会有UserDetailsService is required错误
				.authorizationCodeServices(memoryAuthorizationCodeServices)
				// token服务
				.tokenServices(memoryAuthorizationServerTokenServices)
				// 以内存的方式存储token
				.tokenStore(tokenStore)
				// 允许获得token的请求方式
				.allowedTokenEndpointRequestMethods(HttpMethod.POST, HttpMethod.GET)
		// 调整相关固定API重定向地址:第一个参数为固定API地址,第二个参数为重定向地址,当前只有/oauth/confirm_access和/oauth/error可配置
		// .pathMapping("/oauth/confirm_access", "/confirm_access")
		// .pathMapping("/oauth/error", "/oauth_error")
		;
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
				// 允许已经认证了的用户访问
				// .tokenKeyAccess("isAuthenticated()")
				// 拒绝所有
				// .tokenKeyAccess("denyAll()")
				// 开启端口/oauth/check_token的访问权限(允许所有)
				.checkTokenAccess("permitAll()")
				// 值允许已经认证了的用户访问
				// .checkTokenAccess("isAuthenticated()")
				// 密码解析
				// .passwordEncoder(passwordEncoder)
				// 允许客户端进行表单认证
				.allowFormAuthenticationForClients();
		// oauthServer.tokenKeyAccess("isAnonymous() ||
		// hasAuthority('ROLE_TRUSTED_CLIENT')");
		// oauthServer.checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
	}
}