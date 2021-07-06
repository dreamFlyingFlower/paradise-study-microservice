package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

/**
 * OAuth2:允许用户授权第三方应用访问用户存储在其他服务提供者上的信息,而不需要提供用户名和密码给第三方应用
 * eg:登录A网站,可以用QQ登录,此时QQ就是服务提供者,A网站就是第三方应用.在QQ上登录后就授权A网站使用用户信息
 * 
 * OAuth2请求其他服务器资源中的角色:
 * 
 * <pre>
 * ->资源拥有者:通常为用户,也可以使应用程序
 * ->第三方应用:本身不存储资源,需要通过资源拥有者去请求资源服务器的资源
 * ->授权服务器:也叫认证服务器,用于对资源拥有者身份认证,访问资源授权等,认证成功后发放授权码(access_token)给第三方
 * ->资源服务器:存储拥有者资源的服务器,会给第三方应用一个客户端标识(client_id)和秘钥(client_secret),标识第三方的身份
 * </pre>
 * 
 * OAuth2有4种授权模式:授权码模式,用户名密码模式,简单模式,客户端模式(详情见notes/Security/OAuth2.0官方文档.pdf):
 * 
 * <pre>
 * 1.授权码模式:通常用于用户在第三方应用登录,第三方请求服务提供商用户信息
 * 
 * -> 用户在第三方客户端请求认证服务器服务授权
 * -> 证服服务同意给第三方授权
 * -> 第三方服务获得授权码
 * -> 第三方服务利用授权码从认证服务器申请令牌
 * -> 第三方服务获得证服服务器的响应令牌
 * -> 第三方服务携带令牌从资源服务器获得用户资源
 * -> 资源服务器返回用户信息
 * 
 * 2.客户端模式:通常用于信任的服务器之间调用,无需用户参与
 * 
 * -> 第三方应用请求服务器提供商的认证服务器,服务提供商返回令牌
 * -> 第三方应用在请求头中添加令牌,直接访问服务提供商的接口
 * </pre>
 * 
 * OAuth2主要拦截器:
 * 
 * <pre>
 * ->{@link SecurityContextPersistenceFilter}:SpringSecurity主要拦截器
 * ->{@link OAuth2AuthorizationRequestRedirectFilter}:用户在第三方应用页面点击服务提供商登录按钮,页面跳转到服务提供商的授权页
 * -->{@link DefaultOAuth2AuthorizationRequestResolver#resolve}:判断请求类型,最终返回OAuth2AuthorizationRequest.
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
 * ->{@link AbstractAuthenticationProcessingFilter}:
 * ->{@link SecurityContextPersistenceFilter}:过滤保存Authentication到HttpSession中
 * </pre>
 * 
 * OAuth2相关类:
 * 
 * <pre>
 * {@link OAuth2ClientAutoConfiguration}:客户端自动配置类
 * {@link #OAuth2ClientRegistrationRepositoryConfiguration}:将配置文件中注册的client构造成ClientRegistration保存到内存中.
 * 		{@link CommonOAuth2Provider}:枚举类,里面事先定义好了几种常用的三方登录授权服务器的各种参数
 * {@link OAuth2WebSecurityConfiguration}:配置web相关的类,如怎么样保存和获取已经授权过的客户端,以及默认的oauth2客户端相关的配置
 * </pre>
 * 
 * SpringSocial和SpringSecurityOAuth都已经不维护了,只能直接使用SpringSecurity进行相关操作,详见SpringSecurity官网(spring.io)
 * 
 * @author 飞花梦影
 * @date 2021-04-09 11:04:34
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
public class OauthClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(OauthClientApplication.class, args);
	}
}