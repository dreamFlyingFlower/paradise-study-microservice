package com.wy.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
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

import com.wy.filters.JwtAuthenticationFilter;
import com.wy.filters.VerifyFilter;
import com.wy.security.CustomizerAuthenticationProvider;
import com.wy.security.LoginAuthEntryPoint;
import com.wy.security.LoginFailureHandler;
import com.wy.security.LoginSuccessHandler;
import com.wy.security.LogoutSuccessHandler;
import com.wy.service.UserService;
import com.wy.sms.SmsSecurityConfigurer;
import com.wy.social.qq.QqSocialSecurityConfigurer;

import lombok.AllArgsConstructor;

/**
 * SpringSecurity6以上配置
 * 
 * 自定义数据库用户登录参考{@link JdbcUserDetailsManager}
 *
 * @author 飞花梦影
 * @date 2023-02-01 16:51:06
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class SecurityConfig {

	private final UserService userService;

	private final CustomizerAuthenticationProvider customizerAuthenticationProvider;

	private final DataSource dataSource;

	private final VerifyFilter verifyFilter;

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	private final LoginSuccessHandler loginSuccessHandler;

	private final LoginFailureHandler loginFailureHandler;

	private final LogoutSuccessHandler logoutSuccessHandler;

	private final SmsSecurityConfigurer smsSecurityConfigurer;

	private final QqSocialSecurityConfigurer qqSocialSecurityConfigurer;

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

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 使用多种自定义方式登录
	 * 
	 * @return AuthenticationManager
	 */
	@Bean
	AuthenticationManager authenticationManager() {
		List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
		// 普通数据库登录方式
		authenticationProviders.add(daoAuthenticationProvider());
		// 添加自定义的provider,此处和daoAuthenticationProvider类似
		authenticationProviders.add(customizerAuthenticationProvider);
		// 可以添加更多方式的登录方式
		// authenticationProviders.add(mobileAuthenticationProvider());
		// authenticationProviders.add(weixinAuthenticationProvider());
		ProviderManager providerManager = new ProviderManager(authenticationProviders);
		return providerManager;
	}

	/**
	 * 使用数据库中的数据来判断登录是否成功,在登录请求时会自动拦截请求,并进入验证
	 * 
	 * guest:Bcrpt加密->$2a$10$dXULkWNhddYNVH9yQkzwQeJinGE0e22iL4CSEdkm7sRPwa.A27iEi
	 * 123456:Bcrpt加密->$2a$10$lg5hcqs13V3c6FVjr1/mjO31clz7fkjlIKnppDhNDdxJVaWxh/xB6
	 * password:Bcrpt加密->$2a$10$owjsydvplVmh0wI6f.xOM.4TKBc/CoKYTvX.HmnS6Yeu7qlyukAPO
	 */
	@Bean
	AuthenticationManager configure(AuthenticationManagerBuilder auth) throws Exception {
		// 普通数据库登录方式
		auth.authenticationProvider(daoAuthenticationProvider());
		// 添加自定义的provider,此处和daoAuthenticationProvider类似
		auth.authenticationProvider(customizerAuthenticationProvider);
		// 可以添加更多方式的登录方式
		// auth.add(mobileAuthenticationProvider());
		// auth.add(weixinAuthenticationProvider());
		// auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
		return auth.build();
	}

	/**
	 * 单一一种授权认证方式,与上面的只能使用一种
	 * 
	 * @param authConfig
	 * @return
	 * @throws Exception
	 */
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	/**
	 * 普通用户数据库用户名密码登录
	 * 
	 * @return DaoAuthenticationProvider
	 */
	@Bean
	DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		daoAuthenticationProvider.setUserDetailsService(userService);
		return daoAuthenticationProvider;
	}

	/**
	 * 通过创建 SecurityFilterChain 来配置 HttpSecurity,登录认证的请求必须是POST
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
	 * 
	 * @param http HttpSecurity
	 * @throws Exception
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		// 在内存中添加一些内置的用户,当其他微服务访问当前服务时,使用这些内置的用户即可
		httpSecurity.getSharedObject(AuthenticationManagerBuilder.class).inMemoryAuthentication()
				.passwordEncoder(new BCryptPasswordEncoder()).withUser("test")
				.password(new BCryptPasswordEncoder().encode("123456")).roles("USER").and().withUser("admin")
				.password(new BCryptPasswordEncoder().encode("123456")).roles("USER", "ADMIN");

		// 自定义请求头
		httpSecurity.headers(headers -> headers
				// 设置在页面可以通过iframe访问受保护的页面,默认为不允许访问
				.frameOptions(frameOptions -> frameOptions.sameOrigin()));

		// 自定义csrf
		httpSecurity.csrf(csrf -> csrf
				// 禁用csrf
				.disable());

		// 自定义登录
		httpSecurity
				// 默认的登录方式,自动生成登录页面和注销页面的DefaultLoginPageGeneratingFilter和DefaultLogoutPageGeneratingFilter
				.formLogin(Customizer.withDefaults())
				// 自定义登录配置,仍然会添加UsernamePasswordAuthenticationFilter
				.formLogin(formLogin -> formLogin
						// 自定义登录页面,不使用内置的自动生成页面,若定义该参数,其他默认配置失效,见FormLoginConfigurer#initDefaultLoginFilter
						.loginPage("/login").loginProcessingUrl("/login")
						// 自定义登录的用户名和密码传参字段
						.usernameParameter("username").passwordParameter("password")
						// 登录成功的自定义处理
						.successHandler(loginSuccessHandler)
						// 登录失败的自定义处理
						.failureHandler(loginFailureHandler)
						// 访问login页面不需要认证和鉴权
						.permitAll());

		// 自定义退出配置
		httpSecurity.logout(customizer -> customizer
				// 清除所有的认证
				.clearAuthentication(true)
				// 删除指定的cookie,参数为cookie的名字
				.deleteCookies("JSESSIONID")
				// 自定义退出的接口或页面,默认为logout
				.logoutUrl("/signout")
				// 自定义登录处理器
				.addLogoutHandler(null)
				// 自定义退出成功的页面,默认退出到登录页
				.logoutSuccessUrl("/logoutsuccess.html")
				// 自定义登出登录返回json字符串,若不定义则跳到默认地址,url和handler只能有一个生效
				.logoutSuccessHandler(logoutSuccessHandler));

		// 自定义记住我
		httpSecurity.rememberMe(customizer -> customizer
				// 数据库存储
				.tokenRepository(persistentTokenRepository())
				// 记住我的有效时长
				.tokenValiditySeconds(1209600)
				// 使用的userService
				.userDetailsService(userService)
				// 是否一直记住
				.alwaysRemember(true));

		// 自定义缓存设置
		httpSecurity.requestCache(customizer -> customizer.requestCache(getRequestCache(httpSecurity)));

		// 自定义session
		httpSecurity.sessionManagement(management -> management
				// 基于 token,不需要 session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				// session失效之后跳转的地址,若是jsp则是页面,不是则为api接口地址,不需要安全验证
				.invalidSessionStrategy(new SimpleRedirectInvalidSessionStrategy("/login")).invalidSessionUrl("/login")
				// 同一个用户的最大session数量,若再登录时则会将前面登录的session失效
				.maximumSessions(1)
				// 当session达到最大数时,不让后面的用户再次登录;false则不限制
				.maxSessionsPreventsLogin(true)
				// 用户多session登录导致前面session失效时触发的事件,默认会踢掉前面登录的用户
				.expiredSessionStrategy(new SessionExpiredStrategy()));

		// 自定义异常认证处理
		httpSecurity.exceptionHandling(handling -> handling
				// 若不定义则跳转到loginForm自定义地址或默认的/login
				.authenticationEntryPoint(new LoginAuthEntryPoint(null))
				// 自定义权限错误处理器
				.accessDeniedHandler(new AccessDeniedHandlerImpl()));

		// 自定义oauth2
		httpSecurity
				// 配置OAuth2 Client和OAuth2 Server交互,启用SSO
				.oauth2Login(oauth2 -> oauth2
						// 登录地址
						.loginPage(null)
						// 用户端点,自定义service
						.userInfoEndpoint(userInfo -> userInfo.userService(null))
						// 自定义登录成功方法
						.successHandler(null));

		// 自定义saml配置
		httpSecurity.saml2Login(null);

		// HttpBasic设置
		httpSecurity.httpBasic(Customizer.withDefaults());

		// 自定义权限,相当于打开了鉴权模块,它会注册AuthorizationFilter到SecurityFilterChain的最后
		httpSecurity.authorizeHttpRequests(request -> request
				// "/system"要求有ADD_USER的权限
				.requestMatchers("/system").hasAuthority("ADD_USER")
				// "/user"要求有"ROLE_USER"角色权限
				.requestMatchers("/user").hasRole("USER")
				// 效果等同于hasRole和hasIpAddress,hasIpAddress需要自定义SpEL表达式
				.requestMatchers("/admin").access(AuthorityAuthorizationManager.hasRole("admin"))
				// .access("hasRole('admin') and hasIpAddress('127.0.0.1')")
				// 可以指定请求的类型,可以用通配符指定一类的请求
				.requestMatchers(HttpMethod.GET, "/admin/*").hasRole("ADMIN")
				// 所有请求无需验证即可通过
				.requestMatchers("/**").permitAll()
				// 其他请求只需要身份验证即可,无需其他特殊权限
				.anyRequest().authenticated());

		httpSecurity
				// 添加JWT 过滤器,JWT过滤器在用户名密码认证过滤器之前
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				// 在用户名和密码校验之后进行拦截
				.addFilterBefore(verifyFilter, UsernamePasswordAuthenticationFilter.class)
				// 进行短信验证
				.with(smsSecurityConfigurer, Customizer.withDefaults())
				.with(qqSocialSecurityConfigurer, Customizer.withDefaults());
		// .apply(new SmsAuthenticationSecurityConfig());
		// 进行social验证
		// .apply(qqSocialConfigurer)

		// 认证用户时用户信息加载配置
		httpSecurity.userDetailsService(userService);
		return httpSecurity.build();
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

	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		// 配置需要忽略检查的web url
		return web -> web.ignoring().requestMatchers("/js/**", "/css/**", "/images/**");
	}

	// /**
	// * LDAP相关配置,需要引入ldap相关jar
	// *
	// * @return
	// */
	// @Bean
	// EmbeddedLdapServerContextSourceFactoryBean contextSourceFactoryBean() {
	// EmbeddedLdapServerContextSourceFactoryBean contextSourceFactoryBean =
	// EmbeddedLdapServerContextSourceFactoryBean.fromEmbeddedLdapServer();
	// contextSourceFactoryBean.setPort(0);
	// return contextSourceFactoryBean;
	// }
	//
	// @Bean
	// AuthenticationManager ldapAuthenticationManager(BaseLdapPathContextSource
	// contextSource) {
	// LdapBindAuthenticationManagerFactory factory = new
	// LdapBindAuthenticationManagerFactory(contextSource);
	// factory.setUserDnPatterns("uid={0},ou=people");
	// factory.setUserDetailsContextMapper(new PersonContextMapper());
	// return factory.createAuthenticationManager();
	// }
}