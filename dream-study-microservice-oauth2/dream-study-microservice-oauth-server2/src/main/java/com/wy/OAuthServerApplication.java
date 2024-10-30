package com.wy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenIntrospection;
import org.springframework.security.oauth2.server.authorization.authentication.ClientSecretAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientCredentialsAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenIntrospectionAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenIntrospectionAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenRevocationAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.oidc.web.OidcProviderConfigurationEndpointFilter;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.web.NimbusJwkSetEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2AuthorizationEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2AuthorizationServerMetadataEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2ClientAuthenticationFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenIntrospectionEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenRevocationEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.authentication.ClientSecretBasicAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.ClientSecretPostAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.JwtClientAssertionAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.PublicClientAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.filter.CorsFilter;

import com.nimbusds.jose.jwk.source.JWKSource;

/**
 * SpringSecurity认证服务器,5.7以上版本已经抛弃了EnableAuthorizationServer等相关注解,直接使用拦截器SecurityFilterChain
 * 
 * Spring Authorization Server
 * 
 * <pre>
 * 是Spring Security OAuth的进化版本,引入了对OAuth 2.1和OpenID Connect 1.0规范的支持.
 * 基于Spring Security,为构建OpenID Connect 1.0身份提供者和OAuth2授权服务器产品提供了安全、轻量级和可定制的基础.
 * OAuth 2.1和OpenID Connect 1.0是用于身份验证和授权的行业标准协议,被广泛应用于各种应用程序和系统,以实现安全的用户身份验证和授权流程.
 * 
 * JWT和Opaque:Jwt是公开的,直接在线就可以解析看到里面的数据,但不能修改.Opaque一个不透明的token,在看起来就是一个字符串
 * </pre>
 * 
 * 程序启动后,有以下几个固定访问端点:
 * 
 * <pre>
 * 访问登录页面:GET:http://localhost:17127/oauth2/authorize?client_id=test-client&response_type=code&scope=user&redirect_uri=https://www.baidu.com
 * 登录成功跳转授权页面
 * 授权完成后跳转了认证成功的回调地址,上述地址为https://www.baidu.com
 * 
 * 获取到授权码就可以访问/oauth/token获取JWT token了
 * 请求访问令牌:POST:http://localhost:17127/oauth2/token?grant_type=code&client_id=test-client&client_secret=your_client_secret
 * 
 * 使用访问令牌访问受保护的资源:GET:http://localhost:17127/user?access_token=your_access_token
 * </pre>
 * 
 * 客户端向其他认证服务器进行认证:
 * 
 * <pre>
 * {@link RegisteredOAuth2AuthorizedClient}:注解,使用方式见该文件.直接使用客户端配置
 * {@link JdbcOAuth2AuthorizedClientService}:配合{@link JdbcRegisteredClientRepository}使用Jdbc存储客户端Token信息
 * </pre>
 * 
 * spring-security-oauth2-authorization-server的JAR包下有多个SQL文件
 * 
 * <pre>
 * oauth2-registered-client-schema.sql:已注册的客户端信息表,操作类为 JdbcRegisteredClientRepository
 * oauth2-authorization-consent-schema.sql:认证授权表,操作类为 JdbcOAuth2AuthorizationConsentService
 * oauth2-authorization-schema.sql:认证信息表,操作类为 JdbcOAuth2AuthorizationService
 * </pre>
 * 
 * spring-security-oauth2-client的JAR包下有多个SQL文件
 * 
 * <pre>
 * oauth2-client-schema.sql:认证服务器签发的配置文件,操作类为 JdbcOAuth2AuthorizedClientService
 * oauth2-client-schema-postgres.sql:认证服务器签发的配置文件
 * </pre>
 * 
 * 相关类
 * 
 * <pre>
 * {@link AuthorizationServerSettings#builder()}:认证服务器相关设置,例如访问令牌的有效期,刷新令牌的策略和认证页面的URL等.提供了对授权服务器行为的细粒度控制.
 * 		设置固定访问端点,每个端点都有拦截器进行拦截.	包括/oauth2/authorize,/oauth2/token,/oauth2/jwks,/oauth2/revoke,/oauth2/introspect,/connect/register,/userinfo
 * {@link AuthorizationGrantType}:支持的授权模式
 * {@link SecurityContext}:SpringSecurity上下文,保存登录相关信息
 * ->{@link SecurityContextImpl}:SecurityContext默认实现类
 * {@link ExceptionTranslationFilter}:在认证或授权过程中,捕获出现的{@link AuthenticationException}(认证异常)和{@link AccessDeniedException}(授权异常)异常.
 * 		自定义对AuthenticationException,AccessDeniedException异常的处理,需要自定义AuthenticationEntryPoint,AccessDeniedException的实现类,然后将自定义的异常实现类设置到配置中去.
 * 		通过accessDeniedHandler(AccessDeniedHandler),authenticationEntryPoint(AuthenticationEntryPoint),将自定义的异常设置到配置中去
 * 
 * {@link AbstractAuthenticationProcessingFilter#doFilter}:与认证相关的拦截器从此处触发
 * 
 * {@link RegisteredClientRepository}:认证的客户端数据操作,自定义操作需实现该接口
 * {@link JdbcRegisteredClientRepository}:数据库认证客户端操作
 * {@link InMemoryRegisteredClientRepository}:内存认证客户端操作,可直接在配置文件中编写
 * 
 * {@link BearerTokenAuthenticationFilter}:Bearer Token拦截器
 * 
 * {@link OAuth2AuthorizationService}:OAuth2认证服务接口,自定义操作需实现该接口
 * {@link JdbcOAuth2AuthorizationService}:基于数据库的OAuth2认证服务
 * {@link InMemoryOAuth2AuthorizationService}:基于内存的OAuth2认证服务
 * 
 * {@link OAuth2AuthorizationConsentService}:授权确认管理服务,自定义操作需实现该接口
 * {@link JdbcOAuth2AuthorizationConsentService}:基于数据库的授权确认管理服务
 * {@link InMemoryOAuth2AuthorizationConsentService}:基于内存的授权确认管理服务
 * 
 * {@link JWKSource}:JWK是一种JSON格式的密钥表示,用于描述加密算法使用的密钥.JWT使用JWK签名和验签,确保令牌的真实和完整性
 * </pre>
 * 
 * 相关拦截器
 * 
 * <pre>
 * {@link SecurityFilterChain}:入口,默认实现为{@link DefaultSecurityFilterChain}
 * 
 * {@link WebAsyncManagerIntegrationFilter}:
 * {@link HeaderWriterFilter}
 * {@link CorsFilter}
 * {@link CsrfFilter}
 * {@link LogoutFilter}
 * 
 * {@link OAuth2AuthorizationEndpointFilter}:拦截/oauth2/authorize,处理授权码认证过程中获取 code 的请求
 * 		eg:http://ip:port/oauth2/authorize?client_id=client&response_type=code&scope=testScope
 * 		若请求成功,返回302的跳转路径
 * 		eg:http://localhost:8080/callback?code=EuO9WT96cMPoTB7
 * 
 * {@link OidcProviderConfigurationEndpointFilter}:拦截/.well-known/openid-configuration请求,该请求可获取认证服务器信息,但是要开启OIDC
 * 
 * {@link NimbusJwkSetEndpointFilter}
 * {@link OAuth2AuthorizationServerMetadataEndpointFilter}
 * 
 * {@link OAuth2ClientAuthenticationFilter#doFilterInternal}:客户端认证过滤器,对请求的客户端进行认证.无论grant_type是授权码还是客户端认证,请求中都会包含client_id和client_secret.
 * 		此过滤器就是确认客户端的client_id和client_secret是否正确.如果客户端认证成功,会把客户端信息转为{@link Authentication},保存在{@link SecurityContext}中,然后流转到下一个过滤器
 *	->{@link OAuth2ClientAuthenticationFilter.authenticationConverter#convert}:DelegatingAuthenticationConverter筛选转换器,负责转换各种验证请求方式
 *	-->{@link JwtClientAssertionAuthenticationConverter}:
 *	-->{@link ClientSecretBasicAuthenticationConverter}:客户端请求头Basic模式.从请求头中获取Authorization等参数用于验证,转换得到OAuth2ClientAuthenticationToken
 * -->{@link ClientSecretPostAuthenticationConverter}:客户端POST,参数模式.从URL请求参数中获取client_id等参数
 * -->{@link PublicClientAuthenticationConverter}
 * ->{@link OAuth2ClientAuthenticationFilter#validateClientIdentifier}:验证的客户端id是否符合oauth2规范
 * ->{@link OAuth2ClientAuthenticationFilter.authenticationManager#authenticate}:{@link ProviderManager}中筛选{@link AuthenticationProvider#authenticate}返回Authentication.
 * 		当前provider为{@link ClientSecretAuthenticationProvider},token为{@link OAuth2ClientAuthenticationToken}
 * ->{@link OAuth2ClientAuthenticationFilter.authenticationSuccessHandler#onAuthenticationSuccess}:发布成功事件
 * 
 * {@link UsernamePasswordAuthenticationFilter}
 * {@link RequestCacheAwareFilter}
 * {@link SecurityContextHolderAwareRequestFilter}
 * {@link AnonymousAuthenticationFilter}
 * {@link SessionManagementFilter}
 * {@link ExceptionTranslationFilter}
 * 
 * {@link OAuth2TokenEndpointFilter}:拦截/oauth2/token.处理不同grant_type,并真正颁发access_token和refresh_token的过滤器.拦截器核心
 * ->{@link OAuth2TokenEndpointFilter.authenticationConverter#convert}:DelegatingAuthenticationConverter筛选转换器,获取OAuth2ClientCredentialsAuthenticationConverter,当前版本有3种
 * -->{@link OAuth2AuthorizationCodeAuthenticationConverter}:授权码转换器
 * -->{@link OAuth2RefreshTokenAuthenticationConverter}:token刷新转换器
 * -->{@link OAuth2ClientCredentialsAuthenticationConverter}:客户端转换器
 * -->OAuth2DeviceCodeAuthenticationConverter:该方式在SpringBoot3版本中存在,当前版本不存在
 * ->{@link OAuth2TokenEndpointFilter.authenticationManager#authenticate}:{@link ProviderManager}中筛选{@link AuthenticationProvider#authenticate}返回Authentication,返回token,完成认证
 * 		当前provider为{@link OAuth2ClientCredentialsAuthenticationProvider},token为{@link OAuth2AccessTokenAuthenticationToken}
 * -->{@link OAuth2ClientCredentialsAuthenticationProvider#authenticate}:将OAuth2ClientCredentialsAuthenticationToken处理后转换为OAuth2AccessTokenAuthenticationToken.
 * --->{@link OAuth2ClientCredentialsAuthenticationProvider.tokenGenerator#generate}:生成oauth2Token,有2种方式:
 * 		1.通过DelegatingOAuth2TokenGenerator获取默认的{@link JwtGenerator}生成默认格式的token
 * 		{@link JwtGenerator}会使用{@link RegisteredClient}中的{@link TokenSettings#builder()}中设置的默认的{@link OAuth2TokenFormat#SELF_CONTAINED}格式
 * 		{@link OAuth2TokenFormat#SELF_CONTAINED}:透明的token,支持JWT Token,不支持access_token和id_token,不经过其他处理,直接就是结果
 * 		2.通过DelegatingOAuth2TokenGenerator获取{@link OAuth2AccessTokenGenerator}生成token
 * 		{@link OAuth2AccessTokenGenerator}会使用{@link RegisteredClient}中的{@link TokenSettings}进行自定义设置为{@link OAuth2TokenFormat#REFERENCE}格式
 * 		{@link OAuth2TokenFormat#REFERENCE}:不透明的token,经过处理后是一串96长度的字符串
 *
 * {@link OAuth2TokenIntrospectionEndpointFilter}:拦截/oauth2/introspect.该请求从资源服务器请求,用来确认客户端传递的token的有效性,token有效则返回属于这个token的认证授权信息.
 * 		eg:http://ip:port/oauth2/introspect?token=AccessTokenUUID
 * ->{@link OAuth2TokenIntrospectionAuthenticationProvider}:校验token,调用OAuth2AuthorizationService获取OAuth2Authorization.
 * 		返回token对应的tokenClaims认证信息{@link OAuth2TokenIntrospection},被包含在{@link OAuth2TokenIntrospectionAuthenticationToken}中
 * 
 * {@link OAuth2TokenRevocationEndpointFilter}:拦截/oauth2/revoke.负责token的注销
 * 		eg:http://ip:port/oauth2/revoke?token=AccessTokenUUID
 * 
 * {@link OAuth2LoginAuthenticationFilter}:拦截/login/oauth2/code/*
 * </pre>
 * 
 * 授权码模式
 * 
 * <pre>
 * http://localhost:17127/oauth2/authorize?response_type=code&client_id=oidc- client&scope=profile&redirect_url=http://www.baidu.com
 * </pre>
 * 
 * 客户端模式
 * 
 * <pre>
 * POST(/oauth2/token):
 * 		请求头:Authorization,格式为:Basic Base64编码的{client_id}:{client_secret}
 * 		Content-Type:application/x-www-form-urlencoded
 * 		请求参数:
 * 			grant_type:授权类型,固定为client_credentials
 * 			scope:可选.申请的权限范围
 * 		请求响应:
 * 			access_token:访问token,格式为uuid
 * 			scope:申请并获得授权的 scope
 * 			token_type:token的类型,固定值Bearer
 * 			expires_in:访问token有效期,单位为秒
 * 
 * 拦截器流程:
 * {@link OAuth2ClientAuthenticationFilter#doFilterInternal()}
 * {@link ClientSecretAuthenticationProvider#authenticate()}
 * {@link OAuth2TokenEndpointFilter}
 * {@link ClientSecretAuthenticationProvider#authenticate()}
 * {@link OAuth2TokenCustomizer}
 * {@link JwtEncoder}
 * {@link OAuth2AuthorizationService}
 * 
 *	1.对客户端信息进行认证:/oauth2/token接口需要客户端认证通过才能访问.
 *		OAuth2ClientAuthenticationFilter拦截请求,调用ClientSecretAuthenticationProvider对传入的client_id和client_secret进行判断.
 *		登录认证成功后,设置认证成功的结果(OAuth2ClientAuthenticationToken,内含客户端信息的RegisteredClient结果)到SecurityContext中.然后跳到下一步的过滤器中
 *	2.对客户端信息进行二次认证
 *		过滤器OAuth2TokenEndpointFilter继续拦截此请求,然后在颁发Token之前,请求OAuth2ClientCredentialsAuthenticationProvider#authenticate()进行第二次认证.
 *		因为第一步的OAuth2ClientAuthenticationFilter已经校验过client_secret,这里主要对client的授权模式是否吻合,以及scope的授权范围进行校验就通过了.
 *		在OAuth2TokenEndpointFilter中,调用OAuth2XxxxAuthenticationProvider#authenticate()进行认证是固定流程,
 *		实际会根据不同的grant_type去选择调用不同的OAuth2XxxxAuthenticationProvider进行认证.因此,对客户端信息进行了二次认证,第一次和第二次的认证的职责是不通的
 *	3.创建访问token
 *	->3.1.开始颁发token:增强token中包含的信息
 *		首先要确定token中包含的信息.在OAuth2ClientCredentialsAuthenticationProvider中通过OAuth2TokenCustomizer定制token相关信息,
 *		本质是定制JwtEncodingContext对象的Claims属性,相当于在一个Map中放进自定义的Key和Value 值
 *	->3.2.开始颁发token:修改tokenValue值为uuid格式
 *		然后JwtEncoder根据定制之后的JwtEncodingContext,生成jwtAccessToken,这里的jwtAccessToken可以认为只是一个以jwt为容器存储了token的不同属性信息的候选access_token
 *	->3.3.开始颁发token:正式颁发OAuth2AccessToken
 *		之后,真正颁发access_token.通过jwtAccessToken构建OAuth2AccessToken,使用jwtAccessToken的tokenValue值(该值是UUID).OAuth2AccessToken才是真正要颁发的access_token对象
 *	4.持久化token及认证过程中的所有信息
 *		为了在颁发token后,能对token进行验证其合法性,以及返回token包含的信息,因此需要对token及其相关的信息进行持久化.
 *		结合OAuth2AccessToken和jwtAccessToken这两个对象,把它们中相关的属性抽出构建为OAuth2Authorization对象,
 *		该对象包含了客户端信息、token 信息、GrantType 信息、authorizedScopes 信息,token中的所有Claims信息的集合,
 *		然后通过接口OAuth2AuthorizationService保存OAuth2Authorization对象,因为这个对象包含的信息最完整,这里保存下来后便于后续对token进行校验
 * 5.返回响应信息
 * 		OAuth2ClientCredentialsAuthenticationProvider返回新的OAuth2AccessTokenAuthenticationToken对象到OAuth2TokenEndpointFilter,
 * 		这个Filter处理转换为返回给前端的响应OAuth2AccessTokenResponse
 * </pre>
 * 
 * Token注销过程:
 * 
 * <pre>
 * {@link OAuth2ClientAuthenticationFilter#doFilterInternal()}:拦截请求,判断是否已登录,若未登录,调用ClientSecretAuthenticationProvider;已登录,则到OAuth2TokenRevocationEndpointFilter
 * {@link ClientSecretAuthenticationProvider}:对传入的clientId和clientSecret进行判断.认证成功后,设置认证对象到SecurityContext中
 * {@link OAuth2TokenRevocationEndpointFilter}:拦截/oauth2/revoke,对已经登录的请求进行注销
 * {@link OAuth2TokenRevocationAuthenticationProvider}:处理token注销,调用OAuth2AuthorizationService获取OAuth2Authorization,处理注销
 * {@link OAuth2AuthorizationService}:对token增加一个meta标签metadata.token.invalidated=true,并重新保存此OAuth2Authorization到内存或Redis中.
 * 		不直接删除存储中的token,是因为需要非常清晰明确该token已被注销,如果提早删去或清理掉,授权服务器就无法判断该token最正确的状态(区分不了token错误还是token已注销)
 * {@link OAuth2TokenRevocationEndpointFilter}:返回结果
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2021-04-09 11:04:34
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
@MapperScan(basePackages = "com.wy.mapper")
public class OAuthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OAuthServerApplication.class, args);
	}
}