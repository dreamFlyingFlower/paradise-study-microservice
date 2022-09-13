package com.wy.oauth2;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * OAuth2认证服务器,需要配合 {@link OAuth2Config}
 * 
 * {@link EnableAuthorizationServer}:添加该注解即可启动认证服务器,实现4种认证模式:授权码模式;密码模式;简化模式;客户端模式
 * 
 * 启动项目之后,可使用get请求默认访问/oauth/authorize?response_type=&client_id=&redirect_url=&scope,
 * 其中response_type为4中认证模式中的标识符,可从官网查看;client_id为分配给第三方服务的标识;
 * redirect_url为认证服务器返回给第三方服务器的授权码地址,授权码会拼接在该地址上;scope为权限,由服务提供商定义.
 * 访问该地址时会填一个用户名和密码,该用户名和密码就是相当于用户登录,同时需要给用户一个ROLE_USER的权限,否则登录失败
 * 
 * 获得授权码之后,再用post请求访问/oauth/token获得令牌信息,参数为grant_type,client_id,code,redirect_url,scope,见OAuth2官网
 * 
 * SpringOAuth获取令牌请求(/oauth/token)的核心流程:
 * 
 * <pre>
 * {@link AuthorizationEndpoint}:/oauth/authorize请求实现类,第三方请求登录认证
 * {@link TokenEndpoint}:/oauth/token请求的入口点,get和post请求都可以,必须已经请求了/oauth/authorize
 * {@link ClientDetailsService}:读取第三方应用信息
 * ->{@link InMemoryClientDetailsService}:默认实现类,读取第三方应用信息
 * ->{@link JdbcClientDetailsService}:自定义数据库实现类,从数据库读取第三方应用
 * {@link ClientDetails}:根据clientId读取相应的信息,类似于UserDetails,封装第三方应用信息
 * {@link TokenRequest}:封装请求中的其他信息,如用户名,密码等
 * {@link TokenGranter}:4种不同授权模式的实现,根据请求中的grant_type走不同的流程
 * ->{@link CompositeTokenGranter#grant}:集合了4种授权模式的类,并进行验证
 * ->{@link AuthorizationCodeTokenGranter#grant}:授权码模式具体实现类,由CompositeTokenGranter统一调用
 * -->{@link AuthorizationServerTokenServices}:生成OAuth2AccessToken接口
 * --->{@link DefaultTokenServices#createAccessToken()}:根据上一步得到的认证信息,生成OAuth2AccessToken
 * ---->{@link TokenStore}:令牌的存储
 * ---->{@link TokenEnhancer}:令牌增强器,当临牌生成之后,可以对令牌信息进行改造
 * {@link OAuth2AccessToken}:最终的令牌,scope等信息的集合
 * </pre>
 * 
 * SpringOAuth2相关类:
 * 
 * <pre>
 * {@link AuthorizationServerConfigurerAdapter}:继承该类可以对OAuth2登录做些自定义的配置
 * {@link AuthorizationServerEndpointsConfigurer}:SpringOAuth其他入口点配置,如TokenEndpoint
 * </pre>
 * 
 * @auther 飞花梦影
 * @date 2019-09-26 22:42:52
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthenticationServer extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;

	/** {@link OAuth2Config#authorizationCodeServices()} */
	@Autowired
	private AuthorizationCodeServices authorizationCodeServices;

	/** {@link OAuth2Config#authorizationServerTokenServices()} */
	@Autowired
	private AuthorizationServerTokenServices authorizationServerTokenServices;

	@Autowired
	private ClientDetailsService clientDetailsService;

	@Autowired
	private UserDetailsService userDetailsService;

	/** {@link TokenStoreConfig#redisTokenStore()} */
	@Autowired
	private RedisTokenStore redisTokenStore;

	/** {@link TokenStoreConfig#jwtAccessTokenConverter()} */
	@Autowired
	private JwtAccessTokenConverter jwtAccessTokenConverter;

	/** {@link TokenStoreConfig#jwtTokenEnhancer()} */
	@Autowired
	private TokenEnhancer jwtTokenEnhancer;

	@Autowired
	private UserApprovalHandler userApprovalHandler;

	/**
	 * 客户端相关配置,如有那些客户端会访问服务器,认证服务器会给那些客户端发令牌等信息.
	 * 重写该方法后,写在配置文件中的security.oauth2.client.client-id和client-secret都将无效
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// 从内存中读取信息
		// clients.inMemory()
		// // 配置clientid
		// .withClient("test_config")
		// // 配置密钥
		// .secret("test_secret")
		// // 设置过期时间
		// .accessTokenValiditySeconds(7200)
		// // 支持的授权模式
		// .authorizedGrantTypes("refresh_token", "authorization_code")
		// // 客户端请求权限.如果客户端不传scope,则直接给服务配置的权限;如果传了,则必须在配置的权限集合内
		// .scopes("all")
		// // 使用and()可以添加多个客户端授权
		// .and().withClient("test_config1");

		// 从数据库中读取信息
		clients.withClientDetails(clientDetailsService);
	}

	/**
	 * TokenEndpoint等入口点参数配置
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		// endpoints.tokenServices(tokenService)

		endpoints.authenticationManager(authenticationManager)
		        // 若无refresh_token会有UserDetailsService is required错误
		        .authorizationCodeServices(authorizationCodeServices)
		        .pathMapping("/oauth/confirm_access", "/confirm_access").pathMapping("/oauth/error", "/oauth_error")
		        // 只允许POST方式访问
		        .allowedTokenEndpointRequestMethods(HttpMethod.POST)
		        // 使用默认的tokenService,默认实现 DefaultTokenServices
		        // .tokenServices(defaulAuthorizationServerTokenServices)
		        // 使用自定义的tokenService,改造后的 DefaultTokenServices
		        .tokenServices(authorizationServerTokenServices)
		        // 使用redis存储第三方客户端相关信息
		        .tokenStore(redisTokenStore).userDetailsService(userDetailsService)
		        .userApprovalHandler(userApprovalHandler)
		        // 自定义异常
		        .exceptionTranslator(new OAuth2WebResponseExceptionTranslator());
		// 不使用authorizationServerTokenServices,直接使用JWT处理token
		if (null != jwtAccessTokenConverter && null != jwtTokenEnhancer) {
			TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
			ArrayList<TokenEnhancer> enhancers = new ArrayList<>();
			enhancers.add(jwtTokenEnhancer);
			enhancers.add(jwtAccessTokenConverter);
			tokenEnhancerChain.setTokenEnhancers(enhancers);
			endpoints.tokenEnhancer(tokenEnhancerChain).accessTokenConverter(jwtAccessTokenConverter);
		}
	}

	/**
	 * 令牌访问端点安全策略
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()").checkTokenAccess("permitAll()")
		        // 获得签名的signkey,需要身份验证才行,默认是denyAll(),这是SpringSecurity的权限表达式
		        .tokenKeyAccess("isAuthenticated()")
		        // 允许表单认证
		        .allowFormAuthenticationForClients();
	}
}