package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationCodeGrantFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.client.web.server.OAuth2AuthorizationCodeGrantWebFilter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.wy.wechat.WechatAuthorizationRequestConsumer;
import com.wy.wechat.WechatCodeGrantRequestEntityConverter;

/**
 * OAuth2相关类:
 * 
 * <pre>
 * {@link OAuth2ClientAutoConfiguration}:客户端自动配置类
 * {@link #OAuth2ClientRegistrationRepositoryConfiguration}:将配置文件中注册的client构造成ClientRegistration保存到内存中.
 * ->{@link CommonOAuth2Provider}:事先定义好了几种常用的三方登录授权服务器的各种参数
 * {@link OAuth2WebSecurityConfiguration}:配置web相关的类,如怎么样保存和获取已经授权过的客户端,以及默认的oauth2客户端相关的配置
 * </pre>
 * 
 * OAuth2 Client相关流程:
 * 
 * <pre>
 * ->{@link FilterChainProxy}:最终代理
 * ->{@link DefaultSecurityFilterChain}:SpringSecurity主要拦截器
 * ->{@link OAuth2AuthorizationRequestRedirectFilter}:用户在第三方应用页面点击服务提供商登录按钮,页面跳转到服务提供商的授权页
 * -->{@link DefaultOAuth2AuthorizationRequestResolver#resolve}:判断请求类型,最终返回{@link OAuth2AuthorizationRequest}.
 * 		如果OAuth2AuthorizationRequest不为null,说明当前请求是一个授权请求,接下来就要拿着这个请求重定向到授权服务器的授权端点
 * --->{@link OAuth2AuthorizationRequestRedirectFilter#sendRedirectForAuthorization}:重定向方法.
 * 		1.如果当前是授权码类型请求,就将这个请求信息保存下来,因为授权服务器回调时需要用到该授权请求的参数进行校验(比对state).
 * 		这里是通过authorizationRequestRepository保存授权请求的,默认是通过HttpSessionOAuth2AuthorizationRequestRepository保存在httpsession中
 * 		2.保存完成之后就要开始重定向到授权服务端点了,默认的authorizationRedirectStrategy是DefaultRedirectStrategy,
 * 		通过response.sendRedirect方法使前端页面重定向到指定的授权
 * ->{@link OAuth2LoginAuthenticationFilter}:用户授权后,服务提供商调用第三方应用的回调地址,将code参数放到回调地址中,
 * 		第三方应用从回调地址中获取这个code,再次去调用服务提供商的access_token地址获取令牌(access_token)
 * -->{@link HttpSessionOAuth2AuthorizationRequestRepository#removeAuthorizationRequest}:
 * 		从httpsession中取出OAuth2AuthorizationRequestRedirectFilter中保存的授权请求,若没有,抛异常
 * -->{@link OAuth2LoginAuthenticationProvider#authenticate}:对未认证的OAuth2LoginAuthenticationToken进行认证,从服务提供商获取code,
 * 		并将认证后的结果封装成 OAuth2AuthenticationToken 和 OAuth2AuthorizedClient
 * --->{@link OAuth2AuthorizationCodeAuthenticationProvider#authenticate}:根据授权模型不同,调用服务提供商接口获取access_token
 * ---->{@link DefaultAuthorizationCodeTokenResponseClient#getTokenResponse}:授权码模式获取access_token
 * -->{@link DefaultOAuth2UserService#loadUser()}:根据上一步获取的access_token获取服务提供商中用户的信息
 * -->{@link AuthenticatedPrincipalOAuth2AuthorizedClientRepository#saveAuthorizedClient()}:将所有经过授权的客户端信息保存起来.
 * 		之后可通过该类对授权的未授权的第三方应用进行相关操作
 * ->{@link AbstractAuthenticationProcessingFilter}:处理所有与认证相关的拦截器
 * ->{@link DefaultSecurityFilterChain}:过滤保存Authentication到HttpSession中
 * </pre>
 * 
 * SpringSocial和SpringSecurityOAuth都已经不维护了,只能直接使用SpringSecurity进行相关操作,详见SpringSecurity官网(spring.io)
 * 
 * 相关组件:
 * 
 * <pre>
 * {@link ClientRegistration}:注册的客户端
 * {@link ClientRegistrationRepository}:ClientRegistration的存储仓库
 * {@link OAuth2AuthorizedClient}:已授权过的客户端
 * {@link OAuth2AuthorizedClientRepository}:已授权过的客户端存储库持久化
 * {@link OAuth2AuthorizationRequestRedirectFilter}:该过滤器处理/oauth2/authorize 路径,转发给认证中心对应的路径 /oauth2/authorize
 * {@link OAuth2AuthorizationCodeGrantFilter}:负责处理认证中心的授权码回调请求,如地址重定向
 * {@link OAuth2LoginAuthenticationFilter}:处理第三方认证的回调(该回调有授权码),拿着授权码到第三方认证服务器获取 access_token 和 refresh_token
 * </pre>
 * 
 * 注意:认证服务器和客户端在同一个机器上时不能使用同一个ip,例如127.0.0.1,在存储cookie时不会区分端口的,他们的cookie是同一个的,后者会覆盖前者.
 * 如果配置认证服务的地址是127.0.0.1:17127,然后通过127.0.0.1:55300去访问客户端则会在登录后出现[authorization_request_not_found]异常
 * 
 * 客户端在OAuth2角色解释中是第三方的一个应用,一般会配合资源服务一起使用
 * 
 * <pre>
 * 单体项目:会同时添加资源服务依赖,客户端调用认证服务登录,资源服务器解析token,获取权限,目前token中的权限只有scope,并且不太好自定义,需要通过资源服务器配置去更好的解析token
 * 
 * 分布式项目:在网关中添加客户端依赖,检查用户认证信息,由网关代理的微服务添加资源服务依赖,解析网关通过令牌中继的方式携带的access_token;各个微服务添加自己的授权校验.
 * 在网关中集成客户端依赖,同时集成资源服务依赖,由网关检查用户的认证和授权信息;
 * 各个微服务不用添加任何的认证与授权相关的处理,可以直接访问.这种方式需要屏蔽各微服务其它ip的访问,只能由网关代理访问
 * </pre>
 * 
 * {@link OAuth2UserService}:从客户端获取用户信息
 * 
 * <pre>
 * {@link OAuth2LoginConfigurer}:配置OAuth2Client登录相关信息,包括OAuth2UserService
 * ->{@link OAuth2LoginConfigurer#getOidcUserService}:获得OAuth2UserService<OidcUserRequest, OidcUser> 
 * ->{@link OAuth2LoginConfigurer#getOAuth2UserService}:获得OAuth2UserService<OAuth2UserRequest, OAuth2User>
 * {@link OAuth2UserService#loadUser}:从客户端获取用户信息.该Bean会先从配置中获取,获取不到会从IOC中获取,IOC中没有就初始化一个
 * ->{@link DefaultOAuth2UserService}:默认实现.通过RestOperations的实例请求用户的基础信息,有可自定义的一些配置,例如请求参数转换、响应数据处理.
 * {@link OidcAuthorizationCodeAuthenticationProvider}:调用{@link OAuth2UserService#loadUser},注入的实例是OidcUserService,需要有openid的scope才会被处理.
 * 		当scope中包含openid时就是openid connect登录,获取token后会响应idToken,里边包含了用户信息,如果要自定义这两个service也需要实现这两个service提高兼容性
 * {@link OAuth2LoginAuthenticationProvider}:调用{@link OAuth2UserService#loadUser},注入的实例是DefaultOAuth2UserService,scope中未包含openid才会去处理
 * </pre>
 * 
 * {@link OAuth2AuthorizationCodeGrantFilter}:拦截授权码模式的授权码接口,其中的OAuth2ParameterNames.REGISTRATION_ID参数,
 * 和配置文件中的spring.security.oauth2.client.registration.[registration_id]对应,故该registration_id不能重复
 * {@link OAuth2AuthorizationCodeGrantWebFilter}:功能和上述类相同,但是是WebFlux的类
 * 
 * {@link WechatAuthorizationRequestConsumer}:自定义微信授权获取code,官方文档:
 * https://docs.spring.io/spring-security/reference/servlet/oauth2/client/authorization-grants.html#_customizing_the_authorization_request
 * {@link WechatCodeGrantRequestEntityConverter}:自定义code解析,获取token,官方文档:
 * https://docs.spring.io/spring-security/reference/servlet/oauth2/client/authorization-grants.html#_customizing_the_access_token_request
 * 
 * @author 飞花梦影
 * @date 2021-04-09 11:04:34
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
public class OAuthClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(OAuthClientApplication.class, args);
	}
}