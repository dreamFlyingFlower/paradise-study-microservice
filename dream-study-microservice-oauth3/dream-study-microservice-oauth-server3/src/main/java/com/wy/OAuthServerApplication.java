package com.wy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.AbstractConfiguredSecurityBuilder;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.DefaultLoginPageConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer;
import org.springframework.security.config.annotation.web.configurers.ServletApiConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationCodeGrantFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
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
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerMetadataEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2ClientAuthenticationConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2DeviceAuthorizationEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2DeviceVerificationEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2TokenEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2TokenIntrospectionEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2TokenRevocationEndpointConfigurer;
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
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2DeviceCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2TokenExchangeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.PublicClientAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.X509ClientCertificateAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
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
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.filter.CorsFilter;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.wy.config.AuthorizationClientConfig;
import com.wy.config.AuthorizationServerConfig;
import com.wy.repository.RedisOAuth2AuthorizationService;

/**
 * SpringSecurity6认证服务器,抛弃了EnableAuthorizationServer等相关注解,直接使用拦截器SecurityFilterChain
 * 
 * 文档:https://www.spring-doc.cn/spring-authorization-server/1.3.1/protocol-endpoints.html
 * 
 * 自定义授权模式:https://docs.spring.io/spring-authorization-server/reference/guides/how-to-ext-grant-type.html
 * 
 * https://juejin.cn/post/7258466145653096504 :17,18
 * https://juejin.cn/post/7430748937147432970:基于Session的前后端分离
 * 
 * Spring Authorization Server
 * 
 * <pre>
 * 是Spring Security OAuth的进化版本,引入了对OAuth 2.1和OpenID Connect 1.0(OIDC)规范的支持.
 * 基于Spring Security,为构建OpenID Connect 1.0身份提供者和OAuth2授权服务器产品提供了安全、轻量级和可定制的基础.
 * OAuth 2.1和OpenID Connect 1.0是用于身份验证和授权的行业标准协议,被广泛应用于各种应用程序和系统,以实现安全的用户身份验证和授权流程.
 * 
 * JWT和Opaque:Jwt是公开的,直接在线就可以解析看到里面的数据,但不能修改.Opaque一个不透明的token,在看起来就是一个字符串
 * 
 * 相关参数:
 * client_id: 客户端的id
 * client_secret: 客户端秘钥
 * redirect_uri: 申请授权成功后的回调地址
 * response_type: 授权码模式固定参数code
 * code_verifier: 一段随机字符串
 * code_challenge: 根据指定的加密方式将code_verifier加密后得到的字符串
 * code_challenge_method: 加密方式
 * scope: 客户端申请的授权范围
 * state: 跟随code原样返回,防止CSRF攻击
 * refresh_token: 刷新token
 * authorization_code: 根据授权码模式的授权码获取
 * client_credentials: 客户端模式获取
 * id_token:当开启OIDC时,用户的基本信息将保存在该字段中,也是JWT格式
 * </pre>
 * 
 * 如果认证服务器开启了oidc,可调用该URL查询认证服务器相关端点以及配置信息:http://ip:port/{context-path}/.well-known/openid-configuration.
 * 
 * <code>
 * {
		// 认证服务器地址
	    "issuer": "http://127.0.0.1:8888",
	    // 认证地址
	    "authorization_endpoint": "http://127.0.0.1:8888/oauth2/authorize",
	    // 设备认证地址
	    "device_authorization_endpoint": "http://127.0.0.1:8888/oauth2/device_authorization",
	    // Token获取地址
	    "token_endpoint": "http://127.0.0.1:8888/oauth2/token",
	    // 支持的客户端获取Token请求方式
	    "token_endpoint_auth_methods_supported": [
	        "client_secret_basic",
	        "client_secret_post",
	        "client_secret_jwt",
	        "private_key_jwt"
	    ],
	    // JWKS地址
	    "jwks_uri": "http://127.0.0.1:8888/oauth2/jwks",
	    // 用户信息地址
	    "userinfo_endpoint": "http://127.0.0.1:8888/userinfo",
	    // 登出地址
	    "end_session_endpoint": "http://127.0.0.1:8888/connect/logout",
	    // 支持的授权类型
	    "response_types_supported": [
	        "code"
	    ],
	    // 支持的授权方式
	    "grant_types_supported": [
	        "authorization_code",
	        "client_credentials",
	        "refresh_token",
	        "urn:ietf:params:oauth:grant-type:device_code"
	    ],
	    // 移除Token地址
	    "revocation_endpoint": "http://127.0.0.1:8888/oauth2/revoke",
	    // 支持移除Token的客户端请求方式
	    "revocation_endpoint_auth_methods_supported": [
	        "client_secret_basic",
	        "client_secret_post",
	        "client_secret_jwt",
	        "private_key_jwt"
	    ],
	    // 客户端调用资源服务器,资源服务器调用认证服务器校验Token的接口
	    "introspection_endpoint": "http://127.0.0.1:8888/oauth2/introspect",
	    // 支持的客户端请求方式
	    "introspection_endpoint_auth_methods_supported": [
	        "client_secret_basic",
	        "client_secret_post",
	        "client_secret_jwt",
	        "private_key_jwt"
	    ],
	    "subject_types_supported": [
	        "public"
	    ],
	    // OIDC加密方式
	    "id_token_signing_alg_values_supported": [
	        "RS256"
	    ],
	    // 支持的scope
	    "scopes_supported": [
	        "openid"
	    ]
	}
 * </code>
 * 
 * 客户端向其他认证服务器进行认证:
 * 
 * <pre>
 * {@link RegisteredOAuth2AuthorizedClient}:注解,使用方式见该文件.直接使用客户端配置
 * {@link JdbcOAuth2AuthorizedClientService}:配合{@link JdbcRegisteredClientRepository}使用Jdbc存储客户端Token信息
 * {@link ClientRegistration}:注册的客户端
 * {@link ClientRegistrationRepository}:ClientRegistration的存储仓库
 * {@link OAuth2AuthorizedClient}:已授权过的客户端
 * {@link OAuth2AuthorizedClientRepository}:已授权过的客户端存储库持久化
 * {@link OAuth2AuthorizationRequestRedirectFilter}:该过滤器处理/oauth2/authorization 路径,转发给认证中心对应的路径/oauth2/authorize
 * {@link OAuth2AuthorizationCodeGrantFilter}:负责处理 认证中心 的授权码回调请求,如地址重定向
 * {@link OAuth2LoginAuthenticationFilter}:处理第三方认证的回调(该回调有授权码),拿着授权码到第三方认证服务器获取access_token和refresh_token
 * {@link RegisteredClientRepository}:认证的客户端数据操作,自定义操作需实现该接口
 * {@link JdbcRegisteredClientRepository}:数据库认证客户端实现
 * {@link InMemoryRegisteredClientRepository}:内存认证客户端实现,可直接从配置文件中读取
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
 * 		设置固定访问端点,每个端点都有拦截器进行拦截.包括/oauth2/authorize,/oauth2/token,/oauth2/jwks,/oauth2/revoke,/oauth2/introspect,/connect/register,/userinfo
 * {@link AuthorizationGrantType}:支持的授权模式
 * {@link SecurityContext}:SpringSecurity上下文,保存登录相关信息
 * ->{@link SecurityContextImpl}:SecurityContext默认实现类
 * {@link ExceptionTranslationFilter}:在认证或授权过程中,捕获出现的{@link AuthenticationException}(认证异常)和{@link AccessDeniedException}(授权异常)异常.
 * 		自定义对AuthenticationException,AccessDeniedException异常的处理,需要自定义AuthenticationEntryPoint,AccessDeniedException的实现类,然后将自定义的异常实现类设置到配置中去.
 * 		通过accessDeniedHandler(AccessDeniedHandler),authenticationEntryPoint(AuthenticationEntryPoint),将自定义的异常设置到配置中去
 * {@link OAuth2ResourceServerConfigurer}:配置资源服务器相关信息
 * ->{@link OAuth2ResourceServerConfigurer#registerDefaultEntryPoint}:注册默认异常处理类.
 * 
 * {@link AbstractAuthenticationProcessingFilter#doFilter}:与认证相关的拦截器从此处触发
 * 
 * {@link RegisteredClientRepository}:认证的客户端数据操作,自定义操作需实现该接口
 * {@link JdbcRegisteredClientRepository}:数据库认证客户端操作
 * {@link InMemoryRegisteredClientRepository}:内存认证客户端操作,可直接在配置文件中编写
 * {@link ClientAuthenticationMethod}:客户端请求认证服务器的方式
 * 
 * ->{@link ClientAuthenticationMethod#CLIENT_SECRET_BASIC}:最常用,header:Authorization Base64.encode({client_id}:{client_secret}).
 * 		客户端将client_id和client_secret通过:拼接,使用Base64编码后,将结果放到header的Authorization发送请求.
 * 		认证服务器通过{@link ClientSecretBasicAuthenticationConverter}解析header获得相关参数
 * ->{@link ClientAuthenticationMethod#CLIENT_SECRET_POST}:常用,客户端的client_id和client_secret通过表单请求将参数传递给授权服务器
 * 		认证服务器通过{@link ClientSecretPostAuthenticationConverter}解析请求体获得相关参数
 * ->{@link ClientAuthenticationMethod#CLIENT_SECRET_JWT}:利用JWT进行认证.
 * 		请求方和授权服务器都知道客户端的client_secret,通过相同的HMAC算法(对称签名算法)去加签和验签JWT,可以达到客户端认证的目的.
 * 		请求方通过HMAC算法,以client_secret作为密钥,将客户端信息加签生成JWT;授权服务器使用相同的HMAC算法和client_secret,对请求方的JWT进行验签以认证客户端
 * ->{@link ClientAuthenticationMethod#PRIVATE_KEY_JWT}:利用JWT进行认证,请求方拥有自己的公私钥(密钥对),使用私钥对JWT 加签,并将公钥暴露给授权服务器.
 * 		授权服务器通过请求方的公钥验证JWT,也能达到客户端认证的目的.
 * ->{@link ClientAuthenticationMethod#NONE}:公共客户端.认证服务器不会对客户端进行验证,PKCE(Proof Key for Code Exchange)流程要求客户端为公共客户端
 * 
 * {@link BearerTokenAuthenticationFilter}:Bearer Token拦截器
 * 
 * {@link OAuth2AuthorizationService}:OAuth2认证服务接口,自定义操作需实现该接口,保存{@link OAuth2Authorization}
 * {@link JdbcOAuth2AuthorizationService}:基于数据库的OAuth2认证服务,保存{@link OAuth2Authorization}
 * {@link InMemoryOAuth2AuthorizationService}:基于内存的OAuth2认证服务,保存{@link OAuth2Authorization}
 * {@link RedisOAuth2AuthorizationService}:基于Redis的OAuth2认证服务,保存{@link OAuth2Authorization}
 * 
 * {@link OAuth2AuthorizationConsentService}:授权确认管理服务,自定义操作需实现该接口
 * {@link JdbcOAuth2AuthorizationConsentService}:基于数据库的授权确认管理服务
 * {@link InMemoryOAuth2AuthorizationConsentService}:基于内存的授权确认管理服务
 * 
 * {@link JWKSource}:JWK是一种JSON格式的密钥表示,用于描述加密算法使用的密钥.JWT使用JWK签名和验签,确保令牌的真实和完整性
 * {@link BearerTokenAccessDeniedHandler}:权限不足的默认拒绝类,会将错误信息放到请求头中
 * {@link JwtGrantedAuthoritiesConverter}:通过token获取scope权限,会默认添加SCOPE_前缀,可自定义
 * 
 * {@link DaoAuthenticationProvider}:默认的用户名密码处理类
 * ->{@link AbstractUserDetailsAuthenticationProvider}:默认的用户名密码抽象处理类
 * ->{@link AbstractUserDetailsAuthenticationProvider#additionalAuthenticationChecks}:允许子类使用userDetails做任何附加校验,如果需要实现自定义逻辑需要重写该方法
 * </pre>
 * 
 * 相关类-{@link AbstractConfiguredSecurityBuilder}
 * 
 * <pre>
 * {@link SecurityFilterChain}:入口
 * ->{@link DefaultSecurityFilterChain}:默认实现类
 * {@link AbstractConfiguredSecurityBuilder}:SpringSecurity各种配置适配器建造类抽象实现
 * ->{@link HttpSecurity#doBuild}:SpringSecurity API主要配置,继承AbstractConfiguredSecurityBuilder.泛型O为DefaultSecurityFilterChain,B为HttpSecurity
 * ->{@link WebSecurity}:SpringSecurity Web页面主要配置,继承AbstractConfiguredSecurityBuilder.泛型O为Filter,B为WebSecurity
 * {@link AbstractConfiguredSecurityBuilder.configurers}:SecurityConfigurer各种配置集合
 * ->{@link SecurityConfigurer}:各种配置的适配器,如果需要自定义,实现该接口.泛型O为DefaultSecurityFilterChain,B为HttpSecurity
 * -->{@link CsrfConfigurer}:CSRF配置类
 * -->{@link ExceptionHandlingConfigurer}:异常处理配置 
 * -->{@link HeadersConfigurer}:请求头配置 
 * -->{@link SessionManagementConfigurer}:Session配置
 * -->{@link SecurityContextConfigurer}:SecurityContext上下文配置
 * -->{@link RequestCacheConfigurer}:请求缓存配置
 * -->{@link AnonymousConfigurer}:匿名请求配置
 * -->{@link ServletApiConfigurer}:Web请求配置
 * -->{@link DefaultLoginPageConfigurer}:登录相关配置
 * -->{@link LogoutConfigurer}:登出相关配置
 * -->{@link CorsConfigurer}:跨域请求配置
 * -->{@link OAuth2AuthorizationServerConfigurer#createConfigurers}:OAuth2.1认证服务配置,额外添加OAuth2.1相关配置类以及其后置类
 * 
 * --->{@link OAuth2ClientAuthenticationConfigurer#init}:OAuth2.1客户端配置,添加各种内置API
 * ---->{@link OAuth2ClientAuthenticationConfigurer#createDefaultAuthenticationProviders}:添加各种客户端鉴权的Provider
 * --->{@link OAuth2ClientAuthenticationConfigurer#configure}:配置各种AuthenticationConverter
 * ---->{@link OAuth2ClientAuthenticationConfigurer#createDefaultAuthenticationConverters}:添加各种客户端认证转换器,不同版本不一样
 * ----->{@link JwtClientAssertionAuthenticationConverter}
 * ----->{@link ClientSecretBasicAuthenticationConverter}
 * ----->{@link ClientSecretPostAuthenticationConverter}
 * ----->{@link PublicClientAuthenticationConverter}
 * ----->{@link X509ClientCertificateAuthenticationConverter}:SpringSecurity6
 * 
 * --->{@link OAuth2AuthorizationServerMetadataEndpointConfigurer}
 * --->{@link OAuth2AuthorizationEndpointConfigurer}
 * --->{@link OAuth2TokenEndpointConfigurer#init}:初始化各种Token操作
 * ---->{@link OAuth2TokenEndpointConfigurer#createDefaultAuthenticationProviders}:添加各种Token Provider
 * --->{@link OAuth2TokenEndpointConfigurer#configure}:配置各种AuthenticationConverter
 * ---->{@link OAuth2TokenEndpointConfigurer#createDefaultAuthenticationConverters}:添加各种客户端认证转换器,不同版本不一样
 * ----->{@link OAuth2AuthorizationCodeAuthenticationConverter}:
 * ----->{@link OAuth2RefreshTokenAuthenticationConverter}:
 * ----->{@link OAuth2ClientCredentialsAuthenticationConverter}:
 * ----->{@link OAuth2DeviceCodeAuthenticationConverter}:SpringSecurity6
 * ----->{@link OAuth2TokenExchangeAuthenticationConverter}:SpringSecurity6
 * 
 * --->{@link OAuth2TokenIntrospectionEndpointConfigurer}
 * --->{@link OAuth2TokenRevocationEndpointConfigurer}
 * --->{@link OAuth2DeviceAuthorizationEndpointConfigurer}
 * --->{@link OAuth2DeviceVerificationEndpointConfigurer}:SpringSecurity6
 * 
 * -->{@link OAuth2AuthorizationServerConfigurer#configure}:添加一些Filter和JWKSource
 * 
 * -->{@link OAuth2ResourceServerConfigurer}:OAuth2.1资源服务配置
 * -->{@link OAuth2ResourceServerConfigurer#configure}:加入BearerTokenAuthenticationFilter,校验header中的token
 * 
 * {@link AbstractConfiguredSecurityBuilder.sharedObjects}:可重用的对象实例
 * ->{@link ApplicationContext}:Spring上下文
 * ->{@link ContentNegotiationStrategy}:
 * ->{@link AuthenticationManagerBuilder}:认证管理器构造器
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
 * ->{@link OAuth2TokenEndpointFilter.authenticationConverter#convert}:DelegatingAuthenticationConverter筛选转换器,获取OAuth2ClientCredentialsAuthenticationConverter,当前版本有4种
 * -->{@link OAuth2AuthorizationCodeAuthenticationConverter}:授权码转换器
 * -->{@link OAuth2RefreshTokenAuthenticationConverter}:token刷新转换器
 * -->{@link OAuth2ClientCredentialsAuthenticationConverter}:客户端转换器
 * -->{@link OAuth2DeviceCodeAuthenticationConverter}:设备授权码转换器
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
 * 1.客户端认证:GET/POST(/oauth2/authorize):http://ip:port/oauth2/authorize?response_type=code&client_id=messaging-client&scope=message.read&redirect_url=http://www.baidu.com
 * 请求头:
 * 		Content-Type:application/x-www-form-urlencoded
 * 请求参数:
 * 		client_id:客户端ID
 * 		response_type:授权码模式固定为code
 * 		scope:授权的scope编码,多个用空格隔开.虽然scope不传或为空仍可以继续调用,但是后续需要授权的地方无法通过,所以此处必传
 * 		redirect_uri:客户端获取授权码的回调URI.draft-ietf-oauth-v2-1-01地址中本机不能是localhost,可使用127.0.0.1,且和客户端中的redirect_uri一致
 * 
 * 2.未登录则重定向用户登录页面:POST(/login):http://ip:port/login,可自定义
 * 请求头:
 * 		Content-Type:application/x-www-form-urlencoded
 * 请求参数:
 * 		username:用户名
 * 		password:密码
 * 请求响应:
 *		登录成功后的Cookie,含Session,后续读取授权码需要从Session中读取当前用户的信息
 *		用户信息对象,前端会缓存该用户信息到前端,用于判断当前是否有用户登录,用于给前端根据用户登录状态显示不同的界面
 *
 * 授权页面是用于呈现给用户,第三方客户端请求获取当前用户的什么权限.作为权限的拥有者(用户)必须要确认是否允许第三方客户端访问自己的信息.
 * 因此,这个授权页面(Code页面)是需要用户确认并做出选择,每一个客户端的配置选项中,通过字段requiry_user_consent来设置是否展现该页面
 * 对于第三方的客户端,最好都要求开启授权页面的展示.如果是受信任的第一方客户端,则可以自行决定是否展示
 * 如果客户端配置表中的require_user_consent=1,用户登录成功后的下一步,前端应当展现授权页面
 * 
 * 3.用户授权:GET(/oauth2/authorize):http://ip:port/oauth2/authorize
 * 请求参数:
 * 		client_id:客户端ID
 * 		response_type:授权码凭证许可固定值为code
 * 		scope:授权的scope编码,分配多个则用空格分隔
 * 请求响应:
 * 		如果需要用户授权,会转发到用户授权页面,同时调用用户授权数据API获取授权信息
 *		http://ip:port/oauth2/consent?client_id={{client}}&scope={{scope}}&state={{consentState}}
 *
 *	4.重定向至授权页面:GET(oauth2/consent):http://ip:port/oauth2/consent?client_id={{client}}&scope={{scope}}&state={{consentState}}
 *
 * 5.用户确认授权:POST(oauth2/authorize):http://ip:port/oauth2/authorize
 * 请求头:
 * 		Content-Type:application/x-www-form-urlencoded
 * 请求参数:
 * 		client_id:客户端ID
 * 		state:授权页面响应的json中的state值
 * 		scope:授权的scope编码,分配多个则参数名有多个同名的scope键.不再是用空格分割,而是会在form表单中用相同的键(键值就是scope)来填写多个不同的scope
 * 
 * 6.授权服务器客户端回调(redirect_uri)地址,并带上授权码(code).注意:调用回调地址之前会检查oauth2_authorization_consent表中的登录用户是否有scope中的权限,若么有,抛异常
 * 
 * 7.获取token:POST(oauth2/token):http://ip:port/oauth2/token?grant_type=authorization_code&code=
 * 请求头:
 * 		Authorization:Basic Base64编码的({client_id}:{client_secret})
 * 		Content-Type:form-data
 * 请求参数:
 * 		grant_type:授权模式,固定为authorization_code
 * 		code:上一步中获得的授权码
 * 		redirect_uri:与第一步请求授权时携带的redirect_uri一致,并且是严格匹配模式,客户端配置中不能只配置一个域名
 * 请求响应:
 * 		access_token:访问token,格式为uuid
 * 		refresh_token:刷新token,用来请求下一次的access_token
 * 		scope:申请并获得授权的scope
 * 		id_token:如果认证服务器开启了OIDC,且当前scope中有openid权限,才会返回该参数
 * 		token_type:token的类型,固定值Bearer
 * 		expires_in:访问token有效期,单位为秒
 * 
 * 主要流程:
 * {@link OAuth2ClientAuthenticationFilter#doFilterInternal()}:判断是否已认证,对传入的client_id和client_secret进行判断.
 * 		若未认证,调用ClientSecretAuthenticationProvider认证.认证成功后,设置结果OAuth2ClientAuthenticationToken(内含客户端信息RegisteredClient)到SecurityContext中,跳到下一步过滤器
 * 		已认证,调用OAuth2TokenEndpointFilter
 * {@link ClientSecretAuthenticationProvider#authenticate()}:客户端认证管理器提供者.
 * {@link OAuth2TokenEndpointFilter}:对客户端信息进行二次认证.在颁发token之前,调用OAuth2AuthorizationCodeAuthenticationProvider进行第二次认证.
 * 		因为OAuth2ClientAuthenticationFilter已经校验过client_secret,这里主要对client的授权模式以及scope的授权范围进行校验.
 * 		在OAuth2TokenEndpointFilter中,调用OAuth2XxxxAuthenticationProvider进行认证是固定流程,实际会根据grant_type选择不同的OAuth2XxxxAuthenticationProvider进行认证
 * {@link OAuth2AuthorizationCodeAuthenticationProvider#authenticate()}:授权码凭据许可认证管理器的提供者.对客户端进行二次认证
 * {@link OAuth2TokenCustomizer}:创建访问token,定制JwtEncodingContext对象的Claims属性.
 * 		增强token中包含的信息,修改tokenValue值为uuid格式,调用JwtEncoder定制jwtAccessToken,通过jwtAccessToken构建OAuth2AccessToken.
 * 		OAuth2AccessToken才是真正要颁发的AccessToken对象.
 * 		如果客户端支持刷新token,则会创建OAuth2RefreshToken.
 * {@link JwtEncoder}:根据定制之后的JwtEncodingContext,生成jwtAccessToken.jwtAccessToken可以认为只是一个以jwt为容器存储了token的不同属性信息的候选access_token
 * {@link OAuth2AuthorizationService}:保存OAuth2Authorization,因为这个对象包含的信息最完整,便于后续对token进行校验其合法性以及返回token包含的信息
 * 		结合OAuth2AccessToken和jwtAccessToken构建OAuth2Authorization(包含客户端信息,token 信息,GrantType,authorizedScopes信息,token中的所有 Claims 信息的集合)
 * {@link OAuth2AuthorizationCodeAuthenticationProvider}:返回新的OAuth2AccessTokenAuthenticationToken对象到OAuth2TokenEndpointFilter,
 * 
 *	8.刷新token:POST(oauth2/token):http://ip:port/oauth2/token?grant_type=refresh_token&refresh_token=
 * 请求头:
 * 		Authorization:Basic Base64编码的({client_id}:{client_secret})
 * 		Content-Type:form-data
 * 请求参数:
 * 		grant_type:授权模式,固定为refresh_token
 * 		refresh_token:上一步中获得的授权码
 * 请求响应:
 * 		access_token:访问token,格式为uuid
 * 		refresh_token:刷新token,用来请求下一次的access_token
 * 		scope:申请并获得授权的 scope
 * 		id_token:如果认证服务器开启了OIDC,且当前scope中有openid权限,才会返回该参数
 * 		token_type:token的类型,固定值Bearer
 * 		expires_in:访问token有效期,单位为秒
 * 
 * {@link OAuth2ClientAuthenticationFilter#doFilterInternal()}:同获取token
 * {@link ClientSecretAuthenticationProvider#authenticate()}:同获取token
 * {@link OAuth2TokenEndpointFilter}:同获取token,但是用于处理token的Provider换成了OAuth2RefreshTokenAuthenticationProvider
 * {@link OAuth2RefreshTokenAuthenticationProvider}:刷新token认证提供者.根据refresh_token,通过OAuth2AuthorizationService找到OAuth2Authorization,
 * 		生成新的token设置到OAuth2Authorization中,生成访问token的处理过程和获取访问token时处理逻辑一致
 * {@link OAuth2AuthorizationService}:同获取token
 * {@link OAuth2TokenEndpointFilter}:将返回的OAuth2AccessTokenAuthenticationToken处理转换为返回给前端的响应OAuth2AccessTokenResponse
 * </pre>
 * 
 * 客户端模式
 * 
 * <pre>
 * 客户端授权:POST(/oauth2/token):http://ip:port/oauth2/token?grant_type=client_credentials&scope=test1
 * 请求头:
 * 		Authorization:Basic Base64编码的({client_id}:{client_secret})
 * 		Content-Type:application/x-www-form-urlencoded
 * 请求参数:
 * 		grant_type:授权类型,固定为client_credentials
 * 		scope:可选.申请的权限范围
 * 请求响应:
 * 		access_token:访问token,格式为uuid
 * 		scope:申请并获得授权的 scope
 * 		token_type:token的类型,固定值Bearer
 * 		expires_in:访问token有效期,单位为秒
 * 		refresh_token:无该参数.若token过期,需要重新请求
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
 * Token校验过程:若是单体服务,则客户端调用认证服务器后自行校验;若是微服务,则是从客户端到资源服务器,再调用认证服务器的/oauth2/introspect
 * 
 * <pre>
 * POST(/oauth2/introspect):http://ip:port/oauth2/introspect?token=
 * 请求头:
 * 		Authorization:Basic Base64编码的({client_id}:{client_secret})
 * 		Content-Type:application/x-www-form-urlencoded
 * 请求参数:
 * 		token:客户端从认证服务器获得的token
 * 请求响应:
 * 		active:true->token有效;false->表示无效
 * 		client_id:客户端 ID
 * 		iat:token的签发时间
 * 		exp:token的过期时间,这个过期时间必须要大于签发时间
 * 		scope:申请的 scope
 * 		token_type:token的类型
 * 		nbf:定义在什么时间之前,该jwt都是不可用的
 * 		sub:token所面向的用户
 * 		aud:接收tokent的一方
 * 		jti:token的唯一身份标识,该值与token值应该一致,主要用来作为一次性token,从而回避重放攻击
 * 
 * 1.{@link OAuth2TokenIntrospectionEndpointFilter}:拦截从资源服务器或自身发起的/oauth2/introspect,调用OAuth2TokenIntrospectionAuthenticationProvider校验token
 * 2.{@link OAuth2TokenIntrospectionAuthenticationProvider}:处理token校验请求,调用OAuth2AuthorizationService获取OAuth2Authorization对象.
 * 		从Provider中返回token对应的tokenClaims认证信息,OAuth2TokenIntrospection实际上就是包含tokenClaims认证信息的对象
 * 3.调用完成后,再次转到资源服务器或自身
 * </pre>
 * 
 * Token注销过程:
 * 
 * <pre>
 * POST(/oauth2/revoke):http://ip:port/oauth2/revoke?token=
 * 请求头:
 * 		Authorization:Basic Base64编码的({client_id}:{client_secret})
 * 		Content-Type:application/x-www-form-urlencoded
 * 请求参数:
 * 		token:客户端从认证服务器获得的token
 * 请求响应:
 * 		成功或失败
 * 
 * {@link OAuth2ClientAuthenticationFilter#doFilterInternal()}:判断是否已认证,若未认证,调用ClientSecretAuthenticationProvider;已登录,则到OAuth2TokenRevocationEndpointFilter
 * {@link ClientSecretAuthenticationProvider}:对传入的clientId和clientSecret进行判断.认证成功后,设置认证对象到SecurityContext中
 * {@link OAuth2TokenRevocationEndpointFilter}:拦截/oauth2/revoke,对已经登录的请求进行注销
 * {@link OAuth2TokenRevocationAuthenticationProvider}:处理token注销,调用OAuth2AuthorizationService获取OAuth2Authorization,处理注销
 * {@link OAuth2AuthorizationService}:对token增加一个meta标签metadata.token.invalidated=true,并重新保存此OAuth2Authorization到内存或Redis中.
 * 		不直接删除存储中的token,是因为需要非常清晰明确该token已被注销,如果提早删去或清理掉,授权服务器就无法判断该token最正确的状态(区分不了token错误还是token已注销)
 * {@link OAuth2TokenRevocationEndpointFilter}:返回结果
 * </pre>
 * 
 * PKCE:授权码扩展模式.授权服务器需要对客户端开启proofkey:RegisteredClient.clientSettings(ClientSettings.builder().requireProofKey(Boolean.TRUE).build()),
 * 同时需要生成CodeVerifier和CodeChallenge,可以在网上生成.随机生成的CodeVerifier和CodeChallenge可以保证流程的安全,无法让他人拆包获取clientId和clientSecret来伪造登录信息
 * 
 * <pre>
 * POST(/oauth2/authorize):http://ip:port/oauth2/authorize?response_type=code&client_id=pkce-message-client&redirect_uri=https://baidu.com&scope=message.read
 * &code_challenge=xxxx&code_challenge_method=S256
 * 请求参数:
 * 		response_type:固定值为code
 * 		client_id:客户端id
 * 		redirect_uri:获取授权的回调地址
 * 		scope:请求授权的范围
 * 		code_challenge:在CodeVerifier的SHA256值基础上,再用BASE64URL编码
 * 
 * 其他流程和授权码模式一样,只有获取token时传递的参数不一样
 * 
 * POST(/oauth2/token):http://ip:port/oauth2/token
 * 请求参数:
 * 		grant_type:固定为authorization_code
 * 		client_id:客户端id
 * 		redirect_uri:获取授权的回调地址
 * 		code:授权码
 * 		code_verifier:和code_challenge一对的code
 * </pre>
 * 
 * 调用OIDC的/userinfo接口:
 * 
 * <pre>
 * 1.认证服务必须开启OIDC功能,{@link AuthorizationServerConfig#authorizationServerSecurityFilterChain}
 * 2.客户端被授权的scope中必须有openid权限,{@link AuthorizationClientConfig#registeredClientRepository}中客户端messaging-client的配置
 * 3.客户端调用/oauth2/authorize进行认证时,scope必须带上openid
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