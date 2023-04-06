package com.wy.oauth.memory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.wy.oauth.OAuth2Config;
import com.wy.oauth.memory.config.SecurityMemoryConfig;
import com.wy.properties.OAuth2MemoryProperties;

/**
 * 内存模式用户名密码认证服务器
 * 
 * 获取token,固定接口/oauth/token,在浏览器访问
 * http://ip:55100/oauthServer/oauth/token?client_id=client_id&client_secret=guest&grant_type=password&username=guest&password=123456
 * 
 * 请求参数:
 * 
 * <pre>
 * client_id:第三方客户端client_id,
 * client_secret:第三方客户端密码,如果服务器没有做特殊处理,该值不能加密或编码
 * grant_type:第三方客户端访问OAuth2认证服务器的方式
 * username:登录SpringSecurity服务的用户名和密码,实际情况下不应该有该参数.正常情况下应该先登录到本系统获得认证的token,
 * 		之后请求头中携带认证的token才能继续访问OAuth2认证服务器.或者SpringSecurity对所有的第三方请求都无需认证,则可不带该参数
 * password:同username,如果Security没有做任何密码的其他操作,传参时不能加密,要原文传输
 * </pre>
 * 
 * 返回值:
 * 
 * <pre>
 * access_token:OAuth2认证服务器返回的token,以后访问所有请求都要携带该token,否则无法访问
 * token_tpye:令牌类型
 * refresh_token:access_token到期时获取下一次access_token时的刷新token
 * expires_in:access_token过期时间
 * scope:权限域
 * </pre>
 * 
 * 检查token是否失效,固定接口/oauth/check_token,在浏览器访问
 * http://ip:55100/oauthServer/oauth/check_token?token=
 * 
 * 重新获取token,仍然使用/oauth/token,单是grant_type换成refresh_token,同时带上第一次获取到的refresh_token,用户名和密码也不需要
 * http://ip:55100/oauthServer/oauth/token?client_id=client_id&client_secret=guest&grant_type=refresh_token&refresh_token=
 * 
 * 内存模式授权码认证服务器
 * 
 * <pre>
 * 第一次先获得code: http://ip:port/oauth/authorize?response_type=code&state=123456&client_id=client_id&scope=all&redirect_uri=http://otherappurl
 * 返回http://otherappurl?code=ycjU3F&state=123456可以拿到ycjU3F这个code
 * 
 * response_type:请求模式,授权码认证
 * state:状态,非必须
 * client_id:第三方客户端client_id
 * scope:权限域
 * redirect_uri:获得code的请求地址
 * 
 * 第二次获得token:http://ip:port/oauth/token?client_id=client_id&client_secret=guest&grant_type=authorization_code&code=ycjU3F&redirect_uri=http://otherappurl
 * 
 * grant_type:授权码认证
 * code:从第一部获得的code
 * </pre>
 *
 * @author 飞花梦影
 * @date 2023-04-03 21:52:24
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
// @Configuration
// @EnableAuthorizationServer
public class MemoryAuthorizationServer extends AuthorizationServerConfigurerAdapter {

	/**
	 * 不同的版本可能不一样,高版本一般不要.在{@link SecurityMemoryConfig#authenticationManagerBean}中设置
	 */
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private OAuth2MemoryProperties oAuth2MemoryProperties;

	/**
	 * 在 {@link OAuth2Config#passwordEncoder}中设置
	 */
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthorizationCodeServices memoryAuthorizationCodeServices;

	@Autowired
	private TokenStore tokenStore;

	@Autowired
	@Qualifier("memoryAuthorizationServerTokenServices")
	private AuthorizationServerTokenServices memoryAuthorizationServerTokenServices;

	/**
	 * 实际生产应该从数据库查询加载到内存中,内存中没有再从数据库查询,数据据没有则说明没有注册
	 * 
	 * guest:Bcrpt加密->$2a$10$ahYHTCNIo7RxlMObpPspHOboa1EKKCNreHhBSyGx71.003149BPey
	 * 123456:Bcrpt加密->$2a$10$dEwuUZzYSIJOx7.Lg6VuSuCNF2DGDOg30BV9bHqtHGIRuRZ38XoTi
	 * password:Bcrpt加密->$2a$10$owjsydvplVmh0wI6f.xOM.4TKBc/CoKYTvX.HmnS6Yeu7qlyukAPO
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// 从内存中读取配置
		clients.inMemory()
				// client的id和密码
				.withClient(oAuth2MemoryProperties.getClientIdGuest())
				.secret(passwordEncoder.encode(oAuth2MemoryProperties.getClientSecretGuest()))
				// token有效期
				.accessTokenValiditySeconds(100)
				// 该client可访问的资源服务器ID,每个资源服务器都可以自定义,可不写
				.resourceIds("oauth-resource")
				// 认证模式
				.authorizedGrantTypes(oAuth2MemoryProperties.getGrantTypes())
				// 授权的范围,每个resource会设置自己的范围
				.scopes(oAuth2MemoryProperties.getScopes());
	}

	/**
	 * 配置Token相关
	 * 
	 * @param endpoints
	 * @throws Exception
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		System.out.println(tokenStore);
		endpoints
				// 认证管理器
				.authenticationManager(authenticationManager)
				// 授权码存储
				.authorizationCodeServices(memoryAuthorizationCodeServices)
				// token生成服务
				.tokenServices(memoryAuthorizationServerTokenServices)
				// 以内存的方式存储token
				// .tokenStore(new InMemoryTokenStore())
				// 以JWT的方式存储token
				.tokenStore(tokenStore)
				// 允许获得token的请求方式
				.allowedTokenEndpointRequestMethods(HttpMethod.POST, HttpMethod.GET, HttpMethod.PATCH);
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