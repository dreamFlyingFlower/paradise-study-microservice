package com.wy.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.wy.properties.UserProperties;
import com.wy.security.LoginAuthEntryPoint;
import com.wy.security.LoginFailureHandler;
import com.wy.security.LoginSuccessHandler;
import com.wy.security.LogoutSuccessHandler;
import com.wy.security.UserAuthenticationProvider;
import com.wy.service.UserService;
import com.wy.verify.VerifyFilter;

/**
 * 重写security的configure方法,见官网
 * {@link https://docs.spring.io/spring-security/site/docs/current/guides/html5//helloworld-boot.html#creating-your-spring-security-configuration}
 * 
 * @apiNote ExceptionTranslationFilter:认证异常处理类
 * @author paradiseWy
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserAuthenticationProvider provider;

	@Autowired
	private LoginSuccessHandler loginSuccessHandler;

	@Autowired
	private LoginFailureHandler loginFailHandler;

	@Autowired
	private UserService userService;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private UserProperties userProperties;

	@Autowired
	private VerifyFilter verifyFilter;

	/**
	 * session失效策略
	 */
	// @Autowired
	// private SessionExpiredStrategy sessionExpiredStrategy;

	@Autowired
	private LogoutSuccessHandler logoutSuccessHandler;

	// @Autowired
	// private PenguinSocialConfigurer penguinSocialConfigurer;

	/**
	 * 自定义数据库中的token实现,当有记住我的时候,将会将token存入数据库
	 */
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
		tokenRepository.setDataSource(dataSource);
		// 启动的时候新建表,但是重启后下一次启动的时候需要去掉该参数或设置成false
		// tokenRepository.setCreateTableOnStartup(true);
		return tokenRepository;
	}

	/**
	 * 解决异常找不到authenticationManager,不能在初始化其他spring组件的时候调用security,否则找不到authenticationManager会报错
	 */
//	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
//	@Override
//	public AuthenticationManager authenticationManagerBean() throws Exception {
//		return super.authenticationManagerBean();
//	}

	/**
	 * 使用数据库中的数据来判断登录是否成功,在登录请求时会自动拦截请求,并进入验证
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(provider);
	}

	/**
	 * 登录的请求必须是post请求
	 * 
	 * @instruction apply:进行另外的验证;authorizeRequests:开始请求权限配置
	 *              antmatchers:匹配表达式的所有请求,若是资源文件目录,不可写/resources/**或/static/**,仍然会拦截
	 *              permitall:只要匹配表达式,任意请求都可以访问,无需登录校验 denyAll:所有的请求都拦截 anonymous:匿名用户才通过
	 *              rememberme:只有当前用户是记住用户时通过 authenticated:当前用户不是anonymous时通过
	 *              fullAuthenticated:当前用户既不是anonymous也不是rememberme时通过 hasRole:用户拥有指定的权限时才通过
	 *              hasAnyRole:拥有指定的任意一种权限时就可以通过 hasAnyAuthority:用户有任意一个指定的权限时才通过
	 *              hasIpAddress:请求发送的ip匹配时才通过 anyrequest.authenticated:所有的请求登录后才可访问
	 *              formlogin:表示允许表单登录,httpbasic:表示允许http请求登录 loginPage:拦截未登录的请求到指定页面,只能是内置的页面,默认/login
	 *              loginProcessingUrl:自定义的登录请求url,程序会从该url中读取登录参数,注意开头必须有/,默认是/login
	 *              usernameParameter:自定义用户名的请求字段,默认username.可写在配置文件中
	 *              passwordParameter:自定义密码的请求字段,默认password,可写在配置文件中
	 *              successHandler:登录成功后的处理方法,要实现AuthenticationFailureHandler或重写其子类
	 *              failureHandler:登录失败后的处理方法,要实现AuthenticationSuccessHandler或重写其子类
	 *              exceptionHandling:异常处理 authenticationEntryPoint:开始一个验证的时候,验证失败的时候不跳到默认的登录界面
	 *              AuthLoginConfig:自定义的未登录拦截类,不跳转到默认的未登录页面,而是自定义返回json csrf.disabled:禁用csrf防御机制,即可跨域请求
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				// 进行短信验证
				// .apply(new SmsAuthenticationSecurityConfig()).and()
				// 进行social验证
				// .apply(penguinSocialConfigurer).and()
				// 在用户名和密码校验之后进行拦截
				.addFilterBefore(verifyFilter, UsernamePasswordAuthenticationFilter.class)
				// 对session的操作
				// .sessionManagement()
				// session失效之后跳转的地址,若是jsp则是页面,不是则为api接口地址,不需要安全验证
				// .invalidSessionUrl("/session/invalid")
				// .invalidSessionStrategy("session/")
				// 同一个用户的最大session数量,若再登录时则会将前面登录的session失效
				// .maximumSessions(1)
				// 当session达到最大数时,不然后面的用户session再次登录;false则不限制
				// .maxSessionsPreventsLogin(true)
				// 用户多session登录导致前面session失效时触发的事件
				// .expiredSessionStrategy(sessionExpiredStrategy)
				// .and().and()
				// 验证开始
				.authorizeRequests().antMatchers(userProperties.getSecurity().getPermitSources())
				// 所有匹配的url请求不需要验证
				.permitAll()
				// 访问某些页面需要的权限,此处给的admin权限,是在用户登录时返回的,同时admin需要完全匹配
				// 而给权限的时候需要加上ROLE_,每一种权限都需要加,否则不识别
				// .antMatchers("/user").hasRole("admin")
				// 可以指定请求的类型,可以用通配符指定一类的请求
				// .antMatchers(HttpMethod.GET,"user/*").hasRole("admin")
				.anyRequest().authenticated().and().formLogin().loginProcessingUrl("/user/login")
				.usernameParameter("username").passwordParameter("password").successHandler(loginSuccessHandler)
				// 失败的自定义处理
				.failureHandler(loginFailHandler)
				// 使用记住密码功能需要使用数据库,只是服务端记住,而非浏览器,浏览器关掉之后仍然需要重新登录
				.and().rememberMe().tokenRepository(persistentTokenRepository()).tokenValiditySeconds(1209600)
				.userDetailsService(userService).and()
				// 退出的自定义配置
				.logout()
				// 清除所有的认证
				// .clearAuthentication(true)
				// 删除指定的cookie,参数为cookie的名字
				// .deleteCookies("JSESSIONID")
				// 自定义退出的接口或页面,默认为logout
				// .logoutUrl("/signout")
				// 自定义退出成功的页面,默认退出到登录页
				// .logoutSuccessUrl("/logoutsuccess.html")
				// 自定义登出登录返回json字符串,若不定义则跳到默认地址,url和handler只能有一个生效
				.logoutSuccessHandler(logoutSuccessHandler).and()
				// 自定义拦截未登录请求,若不定义则跳转到loginForm自定义地址或默认的/login
				.exceptionHandling().authenticationEntryPoint(new LoginAuthEntryPoint(null)).and().csrf().disable();
	}
}