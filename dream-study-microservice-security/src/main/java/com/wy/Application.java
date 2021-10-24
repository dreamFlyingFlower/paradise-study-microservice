package com.wy;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.ConsensusBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.social.security.SocialAuthenticationFilter;

import com.wy.config.ExtraMethodSecurityExpressionHandler;
import com.wy.config.ExtraSecurityExpressionRoot;
import com.wy.crl.UserCrl;

/**
 * springsecurity默认的登录请求是/login,而且必须是post请求<br>
 * https://blog.csdn.net/abcwanglinyong/article/details/80981389<br>
 * https://blog.csdn.net/qq_29580525/article/details/79317969<br>
 * https://blog.csdn.net/XlxfyzsFdblj/article/details/82083443 实现动态权限控制<br>
 * {@link https://www.cnblogs.com/softidea/p/7068149.html}<br>
 *
 * SpringSecurity基本原理，过滤器链:
 *
 * <pre>
 * ->{@link SecurityContextPersistenceFilter}:进入时检查SecurityContext中是否有登录信息,离开时将登录信息存入到session中
 * ->{@link BasicAuthenticationFilter}:Basic登录认证,拦截请求头中的Authorization:Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
 * ->{@link UsernamePasswordAuthenticationFilter}:默认的登录拦截器,使用用户名和密码登录
 * ->{@link RememberMeAuthenticationFilter}:记住我拦截器,会根据请求中的session进行自动登录
 * ->{@link SocialAuthenticationFilter}:第三方服务登录，在新的SpringBoot版本中已经移到SpringOAuth项目中
 * ->{@link OAuth2AuthenticationProcessingFilter}:
 * ->{@link OAuth2ClientAuthenticationProcessingFilter}:
 * ->{@link AnonymousAuthenticationFilter}:授权相关,匿名访问过滤器
 * ->{@link ExceptionTranslationFilter}:异常拦截器，接收FilterSecurityInterceptor抛出的异常
 * ->{@link FilterSecurityInterceptor}:授权相关,最终决定请求是否能通过各种过滤器访问到程序中的API
 * ->REST API
 * </pre>
 * 
 * SpringSecurity用户登录主要流程:
 * 
 * <pre>
 * ->{@link SecurityContextPersistenceFilter}:进入时检查SecurityContext中是否有登录信息,离开时将登录信息存入到session中
 * ->{@link AbstractAuthenticationProcessingFilter#attemptAuthentication()}:默认调用UsernamePasswordAuthenticationFilter的实现
 * ->{@link UsernamePasswordAuthenticationFilter#attemptAuthentication()}:默认的登录拦截器,使用用户名和密码登录
 * ->{@link ProviderManager#authenticate()}:验证登录管理器进行验证,主要管理验证的方式,如用户名密码,第三方等
 * -->{@link AuthenticationProvider#authenticate()}:不同登录方式验证.如用户名密码登录,第三方登录等
 * -->{@link DaoAuthenticationProvider#authenticate()}:用户名密码默认使用该类进行登录验证,抽象父类验证.不同方式使用不同的验证类
 * --->{@link DaoAuthenticationProvider#retrieveUser()}:使用自定义的 UserDetailsService 实现类获得数据库用户名和密码
 * ---->{@link UserDetailsService}:用户自定义用户名和密码的登录校验接口,被{@link DaoAuthenticationProvider#retrieveUser()}调用
 * --->{@link AbstractUserDetailsAuthenticationProvider.DefaultPreAuthenticationChecks#check}:前置检查数据库用户的权限,如锁定等
 * --->{@link DaoAuthenticationProvider#additionalAuthenticationChecks}:检查数据库密码和前端密码是否匹配
 * --->{@link AbstractUserDetailsAuthenticationProvider.DefaultPostAuthenticationChecks#check}:后置检查密码是否过期
 * ->{@link AbstractAuthenticationProcessingFilter#successfulAuthentication}:认证成功的调用方法,会调用自定义的认证成功处理类
 * -->{@link SecurityContextHolder}:默认实现类{@link SecurityContextImpl},该类会将认证信息存入{@link SecurityContext}中
 * ->{@link AbstractAuthenticationProcessingFilter#unsuccessfulAuthentication}:认证失败的处理方法,会调用自定义的认证失败处理类
 * ->{@link BasicAuthenticationFilter}...
 * ->{@link ExceptionTranslationFilter}->{@link FilterSecurityInterceptor}->REST API
 * </pre>
 * 
 * SpringSecurity记住我主要流程:
 * 
 * <pre>
 * ->{@link UsernamePasswordAuthenticationFilter#attemptAuthentication()}:默认的登录拦截器,使用用户名和密码登录
 * ->{@link AbstractAuthenticationProcessingFilter#successfulAuthentication}:认证成功的调用方法,会调用自定义的认证成功处理类
 * ->{@link RememberMeServices#loginSuccess()}:当验证成功并将登录写入context之后,该方法将处理记住我
 * -->{@link AbstractRememberMeServices#loginSuccess()}:RememberMeServices的实现类,真实调用方法
 * --->{@link PersistentTokenBasedRememberMeServices#onLoginSuccess()}:将token持久化写入数据库,并将cookie写入浏览器中
 * 
 * ->{@link RememberMeAuthenticationFilter}:记住我拦截器,会根据请求中的session进行自动登录
 * -->{@link AbstractRememberMeServices#autoLogin()}:自动登录
 * --->{@link PersistentTokenBasedRememberMeServices#processAutoLoginCookie()}:将token从数据库取出并和请求中的token比对
 * ---->之后的流程大部分同登录流程
 * </pre>
 * 
 * SpringSecurity的主要拦截器以及功能:
 * 
 * <pre>
 * {@link SecurityContextPersistenceFilter}:初始拦截器,拦截Session,创建SecurityContext,并保存到{@link SecurityContextHolder}中
 * {@link LogoutFilter}:登出拦截器,默认只拦截/logout请求
 * {@link AbstractAuthenticationProcessingFilter}:处理form登录过滤器,默认拦截post的/login请求,UsernamePasswordAuthenticationFilter
 * {@link DefaultLoginPageGeneratingFilter}:生成默认的登录页面,即使重新定义,也只能是内置页面,默认是/login页面
 * {@link RememberMeAuthenticationFilter}:若启用了rememberme功能,对该功能进行拦截,主要依赖cookie实现
 * {@link SecurityContextHolderAwareRequestFilter}:对SecurityContext进行包装,以便实现其他功能
 * {@link AnonymousAuthenticationFilter}:未登录用户访问权限拦截器,即匿名用户
 * {@link ExceptionTranslationFilter}:处理{@link FilterSecurityInterceptor}中的异常,将对应的异常抛出的相应页面
 * {@link SessionManagementFilter}:拦截回话伪造攻击,主要是在登录时销毁用户session,之后再重新生成一个session
 * {@link FilterSecurityInterceptor}:用户的权限过滤都包含在该拦截中 
 * {@link FilterChainProxy}:对上述拦截器按照指定顺序完整功能
 * </pre>
 *
 * SpringSecurity控制授权:
 * 
 * <pre>
 * ->{@link FilterSecurityInterceptor#invoke()}:权限过滤器,在登录验证成功之后才会调用,实际上是一个拦截器
 * -->{@link AbstractSecurityInterceptor#beforeInvocation()}:调用真正的服务
 * --->{@link DefaultFilterInvocationSecurityMetadataSource#getAttributes()}:获得所有配置的拦截,验证等URL匹配信息,
 * 		即{@link WebSecurityConfigurerAdapter#configure(HttpSecurity)}中需要重写的URL拦截信息,封装到{@link ConfigAttribute}中
 * --->{@link AbstractSecurityInterceptor#authenticateIfRequired()}:获得登录的验证信息
 * ->{@link AccessDecisionManager#decide}:权限管理接口，管理一组AccessDecisionVoter
 * -->{@link AbstractAccessDecisionManager#decide}:权限管理接口抽象实现类
 * --->{@link AffirmativeBased#decide}:一组投票中只要有一个投票通过,则请求通过,默认实现
 * ---->{@link AffirmativeBased#getDecisionVoters}:在web环境中,默认只有WebExpressionVoter投票器
 * ---->{@link AccessDecisionVoter#vote()}:对请求进行投票,验证当前URL请求权限是否能通过
 * ----->{@link WebExpressionVoter#vote()}:在Web环境下,所有投票器都由该类决定是否通过
 * --->{@link ConsensusBased}:比较通过和不通过的票数多少,谁多就根据谁决定是否通过
 * --->{@link UnanimousBased}:一组投票中只要有一个不通过,则请求不通过
 * ->{@link ExceptionTranslationFilter}:若抛出异常,会被该类拦截,根据异常不同进行不同的操作,同时会对匿名操作进行验证
 * </pre>
 * 
 * SpringSecurity的主要接口类以及注解:
 * 
 * <pre>
 * {@link UserDetails}:具体的用户实现类需要实现该接口,权限方法等需要在该类中添加<br>
 * {@link UserDetails#getAuthorities()}:角色权限方法等需要在该类中添加,角色都要添加ROLE_前缀,权限不需要添加
 * {@link UserDetailsService#loadUserByUsername}:该方法定义登录时的具体行为
 * {@link CachingUserDetailsService}:从{@link UserDetailsService}中获取信息并放入到缓存中
 * {@link AuthenticationSuccessHandler}:自定义登录成功处理接口
 * {@link AuthenticationFailureHandler}:自定义登录失败处理接口
 * {@link Authentication}:SpringSecurity对权限信息的主要操作类
 * {@link Authentication#getCredentials()}:获取凭证,基本上相当于密码
 * {@link Authentication#getAuthorities()}:获取权限集合,由{@link UserDetails#getAuthorities()}注入
 * {@link Authentication#getDetails()}:获取认证的一些额外信息
 * {@link Authentication#getPrincipal()}:获取凭证,主要是在登录时存入到SecurityContext中的数据
 * {@link AbstractAccessDecisionManager}:自定义决策,可继承该抽象类,也可以实现接口,核心方法为supports.多权限时,需要重写该方法
 * {@link AccessDecisionVoter}:投票器,决定请求是否有权限访问资源
 * ->{@link RoleVoter}:角色投票器,根据角色判断是否有权限访问,主要方法为vote
 * ->{@link AffirmativeBased}:只要有一个投票器通过就允许访问,主要方法为decide
 * ->{@link ConsensusBased}:有一半以上的投票器通过就允许访问,主要方法为decide
 * ->{@link UnanimousBased}:所有投票器都通过才允许访问,主要方法为decide
 * 
 * {@link EnableGlobalMethodSecurity}:开启Security安全管理
 * {@link EnableGlobalMethodSecurity#prePostEnabled()}:是否开启{PreAuthorize PostAuthorize}注解,默认不开启
 * {@link PreAuthorize}:该注解用来管理角色,权限等,值为SpringEL表达式,解析规则{@link SecurityExpressionRoot}
 * ->角色比较:在写入角色时需要在角色之前添加ROLE_,但是使用的时候是直接使用角色即可
 * -->如写入时为ROLE_ADMIN,拦截的时候写ADMIN即可,hasAnyRole('ADMIN','USER')或hasRole('ADMIN')
 * -->若需要同时满足多个角色条件,可以使用AND,如hasRole('ADMIN') AND hasRole('USER')
 * ->权限比较:写入权限时和使用时写一样的即可,即hasAnyAuthority('create')或hasAuthority('create')
 * ->在EL表达式中可以直接使用的变量有:authentication,principal,都是在登录时存入的信息,authentication包含principal
 * {@link AuthenticationPrincipal}:不需要依托EnableGlobalMethodSecurity,修饰参数时直接取得Authentication中的用户信息,
 * 		即直接取得在登录时存入session的实现了UserDetails的类信息
 * </pre>
 * 
 * 自定义权限:
 * 
 * <pre>
 * 1.直接使用@permissionService.methodname(args),其中@后面必须接一个spring中的组件标识,
 * methodname是该组件中的方法名,args是传入其中的参数,返回值必须是boolean,详见{@link UserCrl#test}
 * 2.继承{@link SecurityExpressionRoot},实现{@link MethodSecurityExpressionOperations},
 * 所有需要实现的方法可以直接从MethodSecurityExpressionRoot中复制,该类是私有的,
 * 详见{@link ExtraSecurityExpressionRoot}和{@link ExtraMethodSecurityExpressionHandler}
 * </pre>
 * 
 * {@link PostAuthorize}:该注解在方法执行完之后判断是否有权限,可以用returnObject表示返回值对象
 * 
 * {@link PreFilter}:对Collection类型的方法参数进行拦截,只能用在实现了Collection的类型上,数组也不行
 * {@link PreFilter#filterTarget()}:被拦截的形参名,若方法中只有一个参数,可以不指定
 * {@link PreFilter#value()}:对参数的处理,可以是SpringEl表达式,可以使用filterObject代表实际参数中的单个值
 * 
 * {@link PostFilter}:作用等同于{@link PreFilter},只不过是在方法执行完之后过滤,filterObject表示实际结果的单个值
 * 
 * {@link EnableGlobalMethodSecurity#securedEnabled()}:是否开启{@link Secured}注解,默认不开启
 * {@link Secured}:该注解功能和{@link PreAuthorize}类似,但是没有它全面,只能判断角色,不能判断权限,
 * 且在UserDetails中写入的角色和拦截时候的角色一样,如写入时是ROLE_ADMIN,则拦截时也要写ROLE_ADMIN
 * 
 * {@link EnableGlobalMethodSecurity#jsr250Enabled()}:是否开启jsr250相关注解,默认不开启.相关注解包括如下:
 * {@link RolesAllowed}:同{@link Secured};{@link DenyAll}:全部拒绝;{@link PermitAll}:全部允许
 * 
 * {@link EnableRedisHttpSession}:通过redis开启session的集群共享功能,或者通过配置文件的sorttype配置
 * 
 * SpringSecurity的权限设计不够精细,可以结合自定义权限,更精细控制权限:
 * 
 * <pre>
 * 用户表->用户组织关联表->组织表->组织角色关联表->角色表
 * 用户表->用户职位(部门)关联表->职位表->职位角色关联表->角色表(可选)
 * 用户表->用户组关联表->用户组表->用户组角色关联表->角色表(可选,用户组概念基本和角色一样)
 * 角色表->角色菜单权限关联表->菜单权限表
 * 角色表->角色按钮(数据)权限关联表->按钮(数据)权限表
 * 角色表->角色文件权限关联表->文件表(可选)
 * 菜单表->菜单按钮(权限)表->按钮(数据)表
 * </pre>
 * 
 * API安全:
 * 
 * <pre>
 * 传输安全:将比较敏感的信息进行RSA加密,同时要将时间戳拼接到信息中同时加密.
 * 		时间戳用来判断过期时间,但是会存在服务器和客户端时间不同步的问题,这样可能会导致请求一直不成功
 * 加密串存储:为了更好的防止黑客利用上一次的请求在过期时间内再次请求,可以将加密串在服务器中利用Map进行存储,
 * 		当请求再次被调用时,先从内存中Map中查找加密字符串是否使用过,使用过就不能再调用
 * 请求防篡改:请求签名(MD5),对参数进行MD5加密,同时要指定盐(salt),salt不会随着请求进行传输,但客户端和服务器都要存
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2019-01-31 00:09:33
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableRedisHttpSession
@SpringBootApplication
@EnableOAuth2Sso
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}