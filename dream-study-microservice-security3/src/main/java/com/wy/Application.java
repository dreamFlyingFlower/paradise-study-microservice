package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityDataConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizedUrl;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.authentication.ui.DefaultLogoutPageGeneratingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.filter.DelegatingFilterProxy;

/**
 * SpringSecurity6主要是通过{@link SecurityFilterChain}过滤链来实现权限,默认实现为{@link DefaultSecurityFilterChain}
 * 
 * {@link EnableWebSecurity}:使用SpringSecurity
 * ->{@link WebSecurityConfiguration}:被引入,会初始化名为{@link AbstractSecurityWebApplicationInitializer#DEFAULT_FILTER_NAME}的bean
 * ->#HttpSecurityConfiguration:被引入,初始化{@link HttpSecurity}等相关bean
 * 
 * {@link SecurityFilterChain}:包含了一个Filter数组,可以使用多个拦截器
 * ->{@link SecurityFilterChain#matches}:规则匹配
 * ->{@link SecurityFilterChain#getFilters()}:获得所有的拦截器
 * {@link CsrfFilter}:防止跨站点请求伪造攻击,这也是导致所有POST请求都失败的原因.基于Token验证的API服务可以选择关闭,而一般Web页面需要开启
 * {@link BasicAuthenticationFilter}:支持HTTP的标准Basic Auth的身份验证模块
 * {@link DefaultLoginPageGeneratingFilter}:用于自动生成登录页面
 * {@link DefaultLogoutPageGeneratingFilter}:用于自动生成注销页面
 * 
 * {@link SecurityAutoConfiguration}:SpringSecurity自动注入
 * ->#SpringBootWebSecurityConfiguration:默认配置,引入了基本的Form表单和Basic认证方式
 * ->{@link SecurityDataConfiguration}:整合Spring Data
 * {@link SecurityFilterAutoConfiguration}:SecurittyFilter自动注入类,
 * 会拿bean为{@link AbstractSecurityWebApplicationInitializer#DEFAULT_FILTER_NAME}的{@link DelegatingFilterProxyRegistrationBean}
 * {@link DelegatingFilterProxy#doFilter}:将Servlet中的Filter请求委托给Spring容器中的具体bean处理,实现Servlet和Spring的无缝连接
 * ->{@link DelegatingFilterProxy#initDelegate}:获得{@link AbstractSecurityWebApplicationInitializer#DEFAULT_FILTER_NAME}的bean
 * {@link FilterChainProxy#doFilter}:被DelegatingFilterProxy用来处理SecurityFilterChain的bean,是SpringSecurity拦截器的主要方法,DEBUG入口
 * ->{@link FilterChainProxy#getFilters}:遍历SecurityFilterChain,根据request获得匹配的SecurityFilterChain
 * 
 * {@link FormLoginConfigurer}:表单登录配置,注入了{@link UsernamePasswordAuthenticationFilter}
 * {@link FormLoginConfigurer#initDefaultLoginFilter}:默认表单配置,如果自定义了{@link FormLoginConfigurer#loginPage},初始化方法失效
 * 
 * {@link SecurityConfigurer}:自定义复杂SecurityFilter配置,通过{@link HttpSecurity#apply}来配置注入,
 * {@link SecurityConfigurerAdapter}:自定义复杂SecurityFilter配置,通过{@link HttpSecurity#with}来配置注入,
 * 参照FormLoginConfigurer,CsrfConfigurer等,在执行{@link HttpSecurity#build()}时,会调用这些配置类的configure(),
 * 根据用户的自定义配置,创建一个或者多个SecurityFilter,并将其注册到SecurityFilterChain
 * 
 * {@link HttpSecurity#addFilter}:添加自定义的简单Security Filter
 * 
 * 登录认证流程,以用户名密码方式登录为例:
 * 
 * <pre>
 * {@link UsernamePasswordAuthenticationFilter}:支持Form表单形式的身份验证模块,请求进来后,该类会从请求中获取用户名密码,
 * 		利用这些信息创建一个UsernamePasswordAuthenticationToken对象
 * {@link AuthenticationManager}:实际调用ProviderManager,负责对接受到的UsernamePasswordAuthenticationToken进行认证
 * {@link ProviderManager}:遍历所有的AuthenticationProvider,查找可处理UsernamePasswordAuthenticationToken的AuthenticationProvider进行认证,
 * {@link DaoAuthenticationProvider}:负责对UsernamePasswordAuthenticationToken进行认证.会先调用UserDetailService获取用户信息,
 * 		然后将获取到的密码委托给PasswordEncoder进行验证:
 * ->认证失败,DaoAuthenticationProvider会抛出AuthenticationException的子类异常
 * ->认证成功,返回处理后的UsernamePasswordAuthenticationToken对象,该对象除了包含原本信息外,还包含认证通过状态以及该用户的权限列表
 * {@link SecurityContext}:认证结果会被放入该对象中,其他模块如果需要这个结果(包括用户信息和权限列表),就可以通过以下方法获取:
 * 		SecurityContextHolder.getContext().getAuthentication()
 * </pre>
 * 
 * {@link AbstractAuthenticationProcessingFilter}:大部分认证方式都会继承该抽象拦截器
 * 
 * <pre>
 * {@link AbstractAuthenticationProcessingFilter#doFilter}:模板方法,定义了整个认证流程
 * ->{@link AbstractAuthenticationProcessingFilter#requiresAuthentication}:判断该请求是否是认证请求或者登录请求
 * ->{@link AbstractAuthenticationProcessingFilter#attemptAuthentication}:实际认证逻辑,由子类重写,返回Authentication
 * -->{@link UsernamePasswordAuthenticationFilter#attemptAuthentication}:用户名密码登录拦截器,返回UsernamePasswordAuthenticationToken
 * --->{@link AuthenticationManager#authenticate}:根据不同的登录类型对Authentication处理
 * ---->{@link ProviderManager#authenticate}:AuthenticationManager的通用实现类,将具体的认证工作委托给一系列的AuthenticationProvider
 * ----->{@link AuthenticationProvider#authenticate}:具体的认证工作
 * 
 * ->{@link AbstractAuthenticationProcessingFilter#successfulAuthentication}:认证成功.将Authentication放到SecurityContext中,
 * 后续需要认证结果时都从SecurityContext获取.还会处理其它一些相关功能,如RememberMe,事件发布,最后调用AuthenticationSuccessHandler
 * ->{@link AbstractAuthenticationProcessingFilter#unsuccessfulAuthentication}:认证失败.清空SecurityContext,
 * 调用RememberMe相关服务和AuthenticationFailureHandler来处理认证失败后的回调逻辑,比如跳转到错误页面
 * </pre>
 * 
 * {@link Authentication}:认证结果接口
 * 
 * <pre>
 * {@link Authentication#getAuthorities()}:当前认证用户拥有的权限列表
 * {@link Authentication#getPrincipal()}:用户的一个身份标识,通常就是用户名,认证后一般是用户对象
 * {@link Authentication#getCredentials()}:可用于证明用户身份的一个凭证,通常就是用户密码
 * {@link Authentication#isAuthenticated()}:当前用户是否认证通过
 * {@link Authentication#setAuthenticated()}:更新用户的认证状态
 * {@link Authentication#getDetails()}:获取附加的详情信息,比如原始的Http请求体等
 * 
 * {@link AbstractAuthenticationToken}:抽象认证结果
 * {@link AnonymousAuthenticationToken}:匿名登录用户认证结果
 * {@link UsernamePasswordAuthenticationToken}:用户名密码认证结果
 * {@link JwtAuthenticationToken}:JWT认证结果
 * </pre>
 * 
 * {@link AuthenticationProvider}:具体的Authentication认证方式
 * 
 * <pre>
 * {@link AnonymousAuthenticationProvider}:匿名登录的认证方式
 * {@link DaoAuthenticationProvider}:用户名密码的认证方式,通过UserDetailsService和PasswordEncoder来验证用户名和密码
 * ->{@link UserDetailsService}:查找用户信息,默认是内存存储
 * -->{@link InMemoryUserDetailsManager}:内存存储
 * -->{@link JdbcUserDetailsManager}:数据库存储
 * {@link JwtAuthenticationProvider}:JWT Token的认证方式
 * </pre>
 * 
 * {@link AuthorizationFilter}:鉴权,对登录后的用户访问行为做权限处理,鉴权DEBUG入口,在老版本中鉴权模块是FilterSecurityInterceptor
 * 
 * <pre>
 * {@link AuthorizationFilter#doFilter}:负责鉴权模块,在老版本中鉴权模块是FilterSecurityInterceptor,是整个SecurityFilterChain的最后一个Filter
 * {@link AuthorizationManager#check}:替代之前的AccessDecisionManager和AccessDecisionVoter,校验Authentication,执行鉴权
 * {@link AuthorityAuthorizationManager}:常用鉴权类,调用Authentication#getAuthorities()获取用户的权限列表,将这些权限与请求需要的权限进行匹配
 * {@link AuthenticatedAuthorizationManager}:只需要通过身份认证的请求就可以访问的处理
 * 
 * {@link AuthorizedUrl#hasAuthority}:细粒度权限配置,主要是增删改查,也可以是角色.配置角色最好使用hasRole
 * {@link AuthorizedUrl#hasRole}:角色权限配置,会自动添加ROLE_前缀
 * {@link AuthorizedUrl#withRoleHierarchy}:打开角色继承的功能,角色继承允许一个角色继承另一个角色的所有权限,从而简化权限配置
 * {@link AuthorizedUrl#access}:将AuthorityAuthorizationManager实例注册到权限控制中,可自定义实现
 * </pre>
 * 
 * 以一个标准的鉴权流程为例
 * 
 * <pre>
 * 一个请求进来,经过了一系列Security Filter后,最终来到AuthorizationFilter,进而调用AuthorizationManager#check()进行权限校验
 * 实际的校验工作继续委托给AuthoritiesAuthorizationManager
 * AuthoritiesAuthorizationManager先从SecurityContext中获取到Authentication,然后基于其权限列表构建GrantedAuthority列表,用于权限项的匹配
 * 最终会返回一个AuthorizationDecision表示权限校验结果
 * </pre>
 * 
 * 异常处理
 * 
 * <pre>
 * {@link ExceptionTranslationFilter}:捕获并处理SpringSecurity异常
 * {@link AuthenticationEntryPoint#commence}:处理认证异常错误AuthenticationException及其子类
 * {@link AccessDeniedHandler#handle}:处理鉴权错误AccessDeniedException及其子类
 * </pre>
 * 
 * SpringSecurity5废弃,SpringSecurity6可用的注解
 * 
 * <pre>
 * {@link EnableOAuth2Sso}:5废弃,6可用,配置{@link SecurityFilterChain}时由{@link HttpSecurity}的oauth2Login参数指定
 * </pre>
 * 
 * SpringSecurity整合OAuth2,在{@link HttpSecurity#oauth2Login()}中配置
 * 
 * <pre>
 * {@link OAuth2AuthorizationRequestResolver}:生成授权请求对象{@link OAuth2AuthorizationRequest},
 * 最终用于发起授权请求的地址authorizationRequestUri就是从OAuth2AuthorizationRequest对象中获取的
 * ->{@link DefaultOAuth2AuthorizationRequestResolver#resolve()}:默认实现.生成过程依赖于OAuth2AuthorizationRequest.Builder,
 * 		authorizationRequestCustomizer对象可以实现对Builder的定制
 * ->{@link OAuth2AuthorizationRequest.Builder#buildAuthorizationRequestUri}:有两个扩展点:
 * 		parametersConsumer:用于替换参数名称,以及调整参数顺序;
 * 		authorizationRequestUriFunction:对UriBuilder作进一步的定制,用来添加"#wechat_redirect"
 * {@link OAuth2AccessTokenResponseClient}:定义获取access_token的客户端操作
 * ->{@link DefaultAuthorizationCodeTokenResponseClient}:授权码模式默认实现类,有两个扩展点:
 * 		requestEntityConverter:调整参数
 * 		RestOperations:支持响应的MediaType,以及默认填充token_type字段,再对RestTemplate做进一步定制
 * {@link OAuth2UserService}:定义了发起获取用户信息请求的客户端操作
 * ->{@link DefaultOAuth2UserService}:默认实现,有两个扩展点:requestEntityConverter和RestOperations,定制逻辑也基本类似
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2019-01-31 00:09:33
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableRedisHttpSession
@SpringBootApplication
//@EnableOAuth2Sso
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}