package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
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
 * 资源服务器
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