package com.wy;

import javax.mail.AuthenticationFailedException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.filter.CorsFilter;

/**
 * 资源服务器,就是从认证服务获取公钥,然后解析jwt类型的token
 * 
 * 相关拦截器,和授权服务器差不多,但是多了BearerTokenAuthenticationFilter
 * 
 * <pre>
 * {@link SecurityFilterChain}:入口,默认实现为{@link DefaultSecurityFilterChain}
 * 
 * {@link WebAsyncManagerIntegrationFilter}:
 * {@link AbstractRequestLoggingFilter}
 * {@link HeaderWriterFilter}
 * {@link CorsFilter}
 * {@link CsrfFilter}
 * {@link LogoutFilter}
 * {@link UsernamePasswordAuthenticationFilter}
 * {@link ConcurrentSessionFilter}
 * 
 * {@link BearerTokenAuthenticationFilter}:调用认证管理器AuthenticationManager校验Token.成功后把信息存储在SecurityContext中,然后转到下一步的过滤器进行鉴权
 * 
 * {@link RequestCacheAwareFilter}
 * {@link SecurityContextHolderAwareRequestFilter}
 * {@link RememberMeAuthenticationFilter}
 * {@link AnonymousAuthenticationFilter}
 * {@link SessionManagementFilter}
 * {@link ExceptionTranslationFilter}
 * </pre>
 * 
 * Token校验过程:需要获取客户端请求头中的Authorization:Bearer token
 * 
 * <pre>
 * {@link BearerTokenAuthenticationFilter}:调用认证管理器AuthenticationManager校验Token.成功后把信息存储在SecurityContext中,然后转到下一步的过滤器进行鉴权
 * ->{@link BearerTokenResolver}:从request中读取解析出token值
 * ->{@link BearerTokenAuthenticationToken}:成功读取Token后,封装到成对象,进行下一步对Token的验证
 * {@link OpaqueTokenAuthenticationProvider}:token的认证者.校验token成功后,此处可以扩展.比如根据授权服务器校验token返回的信息,组装权限信息等
 * {@link NimbusOpaqueTokenIntrospector}:通过配置好的URL地址/oauth2/introspect,请求授权服务器.由Spring内部处理,以下为远程服务调用的 HTTP 调用的详细参数说明
 * #OAuth2TokenIntrospectionEndpointFilter:调用授权服务器的拦截器
 * #OAuth2TokenIntrospectionAuthenticationProvider:调用授权服务器的Provider
 * #OAuth2AuthorizationService:调用授权服务的认证服务
 * {@link NimbusOpaqueTokenIntrospector}:获取远程请求的结果TokenIntrospectionResponse,包含tokenClaims认证信息,返回{@link OAuth2AuthenticatedPrincipal}
 * {@link OpaqueTokenAuthenticationProvider}:根据OAuth2AuthenticatedPrincipal,返回{@link BearerTokenAuthentication}
 * {@link BearerTokenAuthenticationFilter}:设置BearerTokenAuthentication到SecurityContext中,以便其他需要API鉴权的Filter进行权限鉴定,通过后,则进入业务Controller
 * </pre>
 * 
 * {@link PreAuthorize}:资源服务器解析access_token时会将用户通过客户端请求的scope当做权限放入authorities中,当使用该注解时的hasAuthority校验用户权限时,
 * 实际上校验的是access_token中拥有的权限.可以自定义jwt(access_token)中的claims,同时对应的resourceserver也提供了对应的自定义解析配置
 * 
 * 请求到达资源服务对token的异常处理,以下两种异常处理默认都是在响应头中添加,响应头是WWW-Authenticate,值就是具体的异常信息
 * 
 * <pre>
 * 没有携带token访问认证信息会抛出AccessDeniedException,并且会调用BearerAuthenticationEntryPoint去处理
 * ->{@link SecurityFilterChain};
 * ->{@link AccessDeniedException}
 * ->{@link ExceptionTranslationFilter}
 * ->{@link BearerTokenAuthenticationEntryPoint}
 * 
 * 请求携带token到达资源服务器后会使用BearerTokenAuthenticationFilter去解析和校验token,成功会将认证信息存入SecurityContextHolder中,失败则调用AuthenticationEntryPoint返回异常信息
 * ->{@link SecurityFilterChain};
 * ->{@link BearerTokenAuthenticationFilter}
 * ->{@link BearerTokenAuthenticationToken}
 * ->{@link AuthenticationManager}
 * ->{@link SecurityContextHolder}
 * ->{@link AuthenticationFailedException}
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2021-04-09 11:04:34
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
public class OAuthResourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OAuthResourceApplication.class, args);
	}
}