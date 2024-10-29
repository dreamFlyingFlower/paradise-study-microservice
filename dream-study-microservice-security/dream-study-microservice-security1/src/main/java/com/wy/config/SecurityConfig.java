package com.wy.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.session.SimpleRedirectInvalidSessionStrategy;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

import com.wy.filters.JwtTokenFilter;
import com.wy.filters.VerifyFilter;
import com.wy.properties.UserProperties;
import com.wy.provider.user.UserAuthenticationProvider;
import com.wy.service.UserService;
import com.wy.social.qq.QqSocialConfigurer;

import dream.flying.flower.framework.security.entrypoint.LoginAuthenticationEntryPoint;
import dream.flying.flower.framework.security.handler.LoginFailureHandler;
import dream.flying.flower.framework.security.handler.LoginSuccessHandler;
import dream.flying.flower.framework.security.handler.LogoutSuccessHandler;

/**
 * 重写security的configure方法,见官网
 * {@link https://docs.spring.io/spring-security/site/docs/current/guides/html5//helloworld-boot.html#creating-your-spring-security-configuration}
 * 
 * {@link ExceptionTranslationFilter}:认证异常处理类
 * 
 * {@link Deprecated}:{@link WebSecurityConfigurerAdapter}在5.7以上废弃,使用见{@link SecurityHighConfig}
 * 
 * @author 飞花梦影
 * @date 2020-12-08 10:23:47
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserAuthenticationProvider provider;

	@Autowired
	private UserService userService;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private UserProperties userProperties;

	@Autowired
	private VerifyFilter verifyFilter;

	@Autowired
	private JwtTokenFilter jwtTokenFilter;

	/**
	 * session失效策略
	 */
	@Autowired
	private SessionExpiredStrategy sessionExpiredStrategy;

	@Autowired
	private QqSocialConfigurer qqSocialConfigurer;

	/**
	 * 自定义数据库中的token实现,当有记住我的时候,将会将token存入数据库
	 */
	@Bean
	PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
		tokenRepository.setDataSource(dataSource);
		// 启动的时候新建表,但是重启后下一次启动的时候需要去掉该参数或设置成false
		// tokenRepository.setCreateTableOnStartup(true);
		return tokenRepository;
	}

	/**
	 * 解决异常找不到authenticationManager,不能在初始化其他spring组件的时候调用security,否则找不到authenticationManager会报错
	 */
	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManagerBean();
	}

	/**
	 * 使用数据库中的数据来判断登录是否成功,在登录请求时会自动拦截请求,并进入验证
	 */
	@Override
	@Bean
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(provider);

		// 在内存中添加一些内置的用户,当其他微服务访问当前服务时,使用这些内置的用户即可
		auth.inMemoryAuthentication()
				.passwordEncoder(new BCryptPasswordEncoder())
				.withUser("test")
				.password(new BCryptPasswordEncoder().encode("123456"))
				.roles("USER")
				.and()
				.withUser("admin")
				.password(new BCryptPasswordEncoder().encode("123456"))
				.roles("USER", "ADMIN");
	}

	/**
	 * 登录的请求必须是post请求
	 * 
	 * <pre>
	 * apply:进行另外的验证;
	 * authorizeRequests:开始请求权限配置
	 * antmatchers:匹配表达式的所有请求,若是资源文件目录,不可写/resources/**或/static/**,仍然会拦截
	 * permitAll:只要匹配表达式,任意请求都可以访问,无需登录校验
	 * denyAll:所有的请求都拦截 
	 * anonymous:匿名用户才通过
	 * rememberMe:只有当前用户是记住用户时通过 
	 * authenticated:当前用户不是anonymous时通过
	 * fullAuthenticated:当前用户既不是anonymous也不是rememberme,且校验通过 
	 * hasRole:用户拥有指定的角色
	 * hasAnyRole:拥有指定的任意一种角色
	 * hasAuthority:用户拥有指定权限
	 * hasAnyAuthority:用户有任意一个指定的权限
	 * hasIpAddress:请求发送的ip匹配时才通过
	 * anyrequest.authenticated:所有的请求登录后才可访问
	 * formlogin:表示允许表单登录
	 * httpbasic:表示允许http请求登录
	 * loginPage:拦截未登录的请求到指定页面,只能是内置的页面,默认/login.或者跳转到指定的url,进行其他处理
	 * loginProcessingUrl:自定义的登录请求url,程序会从该url中读取登录参数,注意开头必须有/,默认是/login
	 * usernameParameter:自定义用户名的请求字段,默认username.可写在配置文件中
	 * passwordParameter:自定义密码的请求字段,默认password,可写在配置文件中
	 * successHandler:登录成功后的处理方法,要实现AuthenticationFailureHandler或重写其子类
	 * failureHandler:登录失败后的处理方法,要实现AuthenticationSuccessHandler或重写其子类
	 * exceptionHandling:异常处理 authenticationEntryPoint:开始一个验证的时候,验证失败的时候不跳到默认的登录界面
	 * AuthLoginConfig:自定义的未登录拦截类,不跳转到默认的未登录页面,而是自定义返回json
	 * csrf.disabled:禁用csrf防御机制,即可跨域请求
	 * </pre>
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity

				// 基于token,不需要csrf
				.csrf()
				.disable()

				// 设置在页面可以通过iframe访问受保护的页面,默认为不允许访问
				.headers(header -> header.frameOptions(frame -> frame.sameOrigin()))
				// 进行短信验证
				// .apply(new SmsAuthenticationSecurityConfig())
				// 进行social验证
				.apply(qqSocialConfigurer)

				.and()

				// 在用户名和密码校验之后进行拦截
				.addFilterBefore(verifyFilter, UsernamePasswordAuthenticationFilter.class)
				// 添加jwt校验
				.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)

				// 对session的操作
				.sessionManagement()
				// 基于token,不需要session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				// 同一个用户的最大session数量,若再登录时则会将前面登录的session失效
				.maximumSessions(1)
				// 当session达到最大数时,不让后面的用户再次登录;false则不限制
				.maxSessionsPreventsLogin(true)
				// 用户多session登录导致前面session失效时触发的事件,默认会踢掉前面登录的用户
				.expiredSessionStrategy(sessionExpiredStrategy)
				.and()
				// session失效之后跳转的地址,若是jsp则是页面,不是则为api接口地址,不需要安全验证
				.invalidSessionUrl("/session/invalid")
				.invalidSessionStrategy(new SimpleRedirectInvalidSessionStrategy("session/"))

				.and()

				// 验证开始
				.authorizeRequests()
				// 默认情况下,Spring Security授权所有调度程序类型,即使请求转发上建立的安全上下文会延续到后续dispatch中,
				// 但细微的不匹配有时会导致意外的AccessDeniedException,如下可避免该问题
				// FORWARD主要用于内置页面,当跳转到内置页面时,一次授权通过Controller跳转到指定方法,一次是渲染内置页面
				.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR)
				.permitAll()
				// 所有匹配的url请求不需要验证
				.antMatchers(userProperties.getSecurity().getPermitSources())
				.permitAll()
				// 访问某些页面需要的权限,此处给的admin权限,是在用户登录时返回的,同时admin需要完全匹配
				// 而给权限的时候需要加上ROLE_,每一种权限都需要加,否则不识别
				// .antMatchers("/user").hasRole("admin")
				// 效果等同于hasRole和hasIpAddress
				// .access("hasRole('admin') and hasIpAddress('127.0.0.1')")
				// 可以指定请求的类型,可以用通配符指定一类的请求
				// .antMatchers(HttpMethod.GET,"user/*").hasRole("admin")
				// 其他请求都需要普通验证
				.anyRequest()
				.authenticated()

				.and()

				.formLogin()
				.loginProcessingUrl("/user/login")
				.usernameParameter("username")
				.passwordParameter("password")
				.successHandler(new LoginSuccessHandler())
				.failureHandler(new LoginFailureHandler())

				.and()

				// 记住密码
				.rememberMe()
				.alwaysRemember(true)
				// 使用记住密码功能需要使用数据库,只是服务端记住,而非浏览器,浏览器关掉之后仍然需要重新登录
				.tokenRepository(persistentTokenRepository())
				.tokenValiditySeconds(1209600)
				.userDetailsService(userService)

				.and()

				// 退出的自定义配置
				.logout()
				// 清除所有的认证
				.clearAuthentication(true)
				// 删除指定的cookie,参数为cookie的名字
				.deleteCookies("JSESSIONID")
				// 自定义退出的接口或页面,默认为logout
				.logoutUrl("/signout")
				// 自定义退出成功的页面,默认退出到登录页
				.logoutSuccessUrl("/logoutsuccess.html")
				// 自定义登出登录返回json字符串,若不定义则跳到默认地址,url和handler只能有一个生效
				.logoutSuccessHandler(new LogoutSuccessHandler())

				.and()

				.exceptionHandling()
				// 自定义拦截未登录请求,若不定义则跳转到loginForm自定义地址或默认的/login
				.authenticationEntryPoint(new LoginAuthenticationEntryPoint(null));

		return httpSecurity.build();
	}

	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		// 配置需要忽略检查的web url
		return web -> web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
	}

	protected RequestCache getRequestCache(HttpSecurity http) {
		RequestCache result = http.getSharedObject(RequestCache.class);
		if (result != null) {
			return result;
		}
		HttpSessionRequestCache defaultCache = new HttpSessionRequestCache();
		defaultCache.setRequestMatcher(createDefaultSavedRequestMatcher(http));
		return defaultCache;
	}

	private RequestMatcher createDefaultSavedRequestMatcher(HttpSecurity http) {
		ContentNegotiationStrategy contentNegotiationStrategy = http.getSharedObject(ContentNegotiationStrategy.class);
		if (contentNegotiationStrategy == null) {
			contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
		}

		RequestMatcher notFavIcon = new NegatedRequestMatcher(new AntPathRequestMatcher("/**/favicon.ico"));

		MediaTypeRequestMatcher jsonRequest =
				new MediaTypeRequestMatcher(contentNegotiationStrategy, MediaType.APPLICATION_JSON);
		jsonRequest.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
		RequestMatcher notJson = new NegatedRequestMatcher(jsonRequest);

		// RequestMatcher notXRequestedWith =
		// new NegatedRequestMatcher(new RequestHeaderRequestMatcher("X-Requested-With",
		// "XMLHttpRequest"));

		@SuppressWarnings("unchecked")
		boolean isCsrfEnabled = http.getConfigurer(CsrfConfigurer.class) != null;

		List<RequestMatcher> matchers = new ArrayList<>();
		if (isCsrfEnabled) {
			RequestMatcher getRequests = new AntPathRequestMatcher("/**", "GET");
			matchers.add(0, getRequests);
		}
		matchers.add(notFavIcon);
		matchers.add(notJson);
		// matchers.add(notXRequestedWith);

		return new AndRequestMatcher(matchers);
	}

}