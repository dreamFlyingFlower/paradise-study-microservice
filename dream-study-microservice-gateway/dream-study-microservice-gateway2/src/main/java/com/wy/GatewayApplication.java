package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayClassPathWarningAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayReactiveLoadBalancerClientAutoConfiguration;
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.web.server.authentication.OAuth2LoginAuthenticationWebFilter;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.handler.AbstractHandlerMapping;

/**
 * SpringCloud Gateway网关,API管理,官网 http://spring.io/guides/gs/gateway/
 * 
 * {@link GatewayAutoConfiguration}:Gateway自动配置入口,加载了Gateway需要注入的类
 * {@link GatewayClassPathWarningAutoConfiguration}:检查是否配置webfux依赖
 * {@link GatewayReactiveLoadBalancerClientAutoConfiguration}:网关需要使用的负载均衡
 * {@link DispatcherHandler#handle}:前端请求处理转发
 * {@link AbstractHandlerMapping#getHandler}:根据Web请求获得转发处理
 * {@link RoutePredicateHandlerMapping#getHandlerInternal}:处理转发请求
 * {@link RoutePredicateHandlerMapping#lookupRoute}:主要的路由转发类
 * 
 * 执行流程:
 * 
 * <pre>
 * 1.Gateway Client向Gateway Server发送请求
 * 2.请求首先会被HttpWebHandlerAdapter进行提取组装成网关上下文
 * 3.然后网关的上下文会传递到DispatcherHandler,它负责将请求分发给RoutePredicateHandlerMapping
 * 4.RoutePredicateHandlerMapping负责路由查找,并根据路由断言判断路由是否可用
 * 5.如果过断言成功,由FilteringWebHandler创建过滤器链并调用
 * 6. 请求会一次经过PreFilter--微服务--PostFilter的方法,终返回响应
 * 
 * Gateway Client->HttpWebHandlerAdapter(组装网关上下文)->DispatcherHandler(循环遍历Mapping,获取Handler)
 * ->RoutePredicateHandlerMapping(匹配路由信息,通过路由断言,判断路由是否可用)
 * ->lookupRoute->断言失败->DispatcherHandler(创建过滤器链,调用过滤器)->继续断言
 * ->lookupRoute->断言成功->FilteringWebHandler(创建过滤器链,调用过滤器)->执行Filter
 * </pre>
 * 
 * SpringGateway集成OAuth2 Client,Resource Server,见ReadMe.md
 * 
 * <pre>
 * 1.用户请求受限资源
 * 2.网关检测没有认证信息,通过{@link RedirectServerAuthenticationEntryPoint}处理并发起OAuth2登录授权申请
 * 3.授权申请到达认证服务,认证服务检测到未登录重定向至登录页面并展示给用户
 * 4.用户登录成功后请求重定向至授权申请接口,通过校验后携带token重定向至回调地址(redirect_uri),
 * 	这里回调地址要设置为网关的地址,网关ip:port/login/oauth2/code/{registrationId},/login/oauth2/code/{registrationId}是固定的,由框架(Security OAuth2 Client)自带
 * 5.请求到达网关,由{@link OAuth2LoginAuthenticationWebFilter}拦截并调用父类{@link AuthenticationWebFilter}进行处理
 * 6.{@link AuthenticationWebFilter}调用{@link OidcAuthorizationCodeReactiveAuthenticationManager}或{@link OAuth2LoginReactiveAuthenticationManager}
 * 处理(由授权申请的scope决定,包含openid就走{@link OidcAuthorizationCodeReactiveAuthenticationManager},否则走另一个)
 * 7.在获取AccessToken成功以后调用{@link ReactiveOAuth2UserService}获取用户信息
 * 8.获取到用户信息后会解析并将认证信息保存至{@link ReactiveSecurityContextHolder}中
 * 9.完成这一系列的认证之后会重定向至最一开始请求的受限资源,这时候就能获取到认证信息了
 * 10.如果访问的是被网关代理的服务则会通过令牌中继(TokenRelay)携带token访问
 * </pre>
 * 
 * Web访问spring-doc的地址:http://127.0.0.1:8080/swagger-ui/index.html
 * WebFlux的地址为:http://127.0.0.1:8080/webjars/swagger-ui/index.html
 * 
 * @author 飞花梦影
 * @date 2020-12-08 10:52:37
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
@EnableHystrix
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
}