package com.wy.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.social.ApiBinding;
import org.springframework.social.ServiceProvider;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SocialAuthenticationProvider;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;
import org.springframework.social.security.provider.OAuth2AuthenticationService;
import org.springframework.social.security.provider.SocialAuthenticationService;

import com.wy.crl.UserCrl;
import com.wy.entity.UserQq;
import com.wy.properties.UserProperties;
import com.wy.social.qq.QqConnectionFactory;
import com.wy.social.qq.QqConnectionSignUp;
import com.wy.social.qq.QqOAuth2Template;
import com.wy.social.qq.QqSocialConfigurer;

/**
 * Social相关配置,必须要开启{@link EnableSocial}
 * 
 * SpringSocial已经不维护了,只能直接使用SpringSecurity进行Social相关操作,详见SpringOauth
 * 
 * 从服务提供商获得需要登录的用户信息,登录拦截social的流程:
 * 
 * <pre>
 * ->{@link SecurityContextPersistenceFilter}:通过请求获得用户登录信息,并在拦截器离开时将登录信息再次存入到session中
 * ->{@link UsernamePasswordAuthenticationFilter}:真正的登录请求拦截与验证
 * ->{@link SocialAuthenticationFilter}:第三方登录请求验证
 * ->{@link SocialAuthenticationService (OAuth2AuthenticationService)}:
 * ->{@link Authentication(SocialAuthenticationToken)}
 * ->{@link AuthenticationManager(ProviderManager)}
 * ->{@link AuthenticationProvider(SocialAuthenticationProvider)}
 * ->{@link UsersConnectionRepository(JdbcUsersConnectionRepository)}
 * ->{@link SocialUserDetailsService}
 * ->{@link SocialUserDetails}
 * ->{@link Authentication(SocialAuthenticationToken)}
 * </pre>
 * 
 * Spring Social相关接口,抽象类,类:
 * 
 * <pre>
 * {@link ServiceProvider}:服务器提供者需要实现的接口
 * {@link AbstractOAuth2ServiceProvider}:ServiceProvider的抽象实现,自定义类可以继承该抽象类,实现服务提供商
 * {@link OAuth2Operations}:封装了从用户请求第三方应用,到第三方应用请求服务提供商获得令牌的全部操作
 * {@link OAuth2Template}:完成OAuth2协议执行的流程
 * {@link ApiBinding}:SpringSocial提供的第三用应用获取服务提供商数据的接口
 * {@link AbstractOAuth2ApiBinding}:ApiBinding的抽象实现,自定义类可以继承该抽象类,实现对服务提供商数据的调用
 * ->{@link AbstractOAuth2ApiBinding#accessToken}:验证完成后由服务提供商发放的令牌,实现该抽象类的类不能是单例
 * {@link Connection}:非数据库链接,是封装第三方应用从服务提供商获取的用户信息
 * {@link ConnectionFactory}:创建Connection的链接工厂,包含ServiceProvider的实例
 * {@link OAuth2Connection}:Connection的抽象实现类
 * {@link OAuth2ConnectionFactory}:ConnectionFactory的抽象实现
 * {@link ApiAdapter}:在Connection和ServiceProvider之间做适配,用来自定义服务提供商返回的用户信息对象
 * {@link UsersConnectionRepository}:用来在Connection和第三方(本地)数据库进行交互的接口
 * {@link JdbcUsersConnectionRepository}:UsersConnectionRepository的实现类
 * </pre>
 * 
 * Spring Social登录相关类:
 * 
 * <pre>
 * {@link SocialAuthenticationFilter#attemptAuthentication}:拦截第三方登录请求
 * ->{@link SocialAuthenticationService}:Social登录验证服务
 * -->{@link OAuth2AuthenticationService#getAuthToken()}:从请求获取认证的令牌,失败会跳到默认的/signin页面
 * --->{@link OAuth2Template#exchangeForAccess()}:获得access_token,同时可重写该方法自动传递client_id和client_secret
 * ---->{@link OAuth2Template#postForAccessGrant()}:发送请求获取access_token,请求类型必须是application/json
 * ----->{@link OAuth2Template#createRestTemplate()}:创建restTemplate请求,该请求默认添加了3中请求类型,没有text/plain这种方式.
 * 			若需要添加自定义的请求方式,需要重写,见{@link QqOAuth2Template}
 * ----->{@link OAuth2Template#postForAccessGrant()}:解析结果,默认是Map,qq默认是拼接在url后面.见{@link QqOAuth2Template}
 * {@link ConnectionFactory}
 * {@link Authentication}
 * ->{@link SocialAuthenticationToken}
 * {@link AuthenticationManager}
 * ->{@link ProviderManager}
 * {@link AuthenticationProvider#authenticate()}:登录认证
 * ->{@link SocialAuthenticationProvider#authenticate()}:根据Connection中的信息从本地数据库获取用户id,若获取不到,跳转signup
 * -->{@link SocialAuthenticationProvider#toUserId()}:从本地数据库获取用户id
 * {@link UsersConnectionRepository}
 * ->{@link JdbcUsersConnectionRepository}
 * {@link SocialUserDetailsService}
 * ->{@link SocialUserDetails}
 * </pre>
 * 
 * Spring Social登录相关流程:
 * 
 * <pre>
 * {@link SocialAuthenticationFilter}:所有以/auth开头的请求都会被拦截,失败的请求都会被以/signin开头的请求拦截.
 * 		若需要登录的服务提供商为qq,则前端访问的地址为/auth/qq,而qq则是由本程序定义的providerId,必须唯一.
 * 		如果不想使用系统默认的拦截地址,可以重写SecurityConfigurerAdapter#postProcess()
 * {@link SecurityConfigurerAdapter#postProcess()}:重写该方法可以自定义SocialAuthenticationFilter拦截的方法,默认是/auth
 * {@link SpringSocialConfigurer#configure}:SecurityConfigurerAdapter实现类,将SocialAuthenticationFilter加入到拦截器中
 * </pre>
 * 
 * @auther 飞花梦影
 * @date 2019-09-23 00:18:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@ConditionalOnProperty(prefix = "user.social.qq", name = "app-id")
@EnableSocial
public class SocialConfig extends SocialConfigurerAdapter {

	@Autowired
	private UserProperties properties;

	@Autowired
	private DataSource dataSource;

	@Autowired(required = false)
	private QqConnectionSignUp signUp;

	/**
	 * 添加服务提供商链接,可添加多个
	 */
	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer,
			Environment environment) {
		QqConnectionFactory qqConnectionFactory =
				new QqConnectionFactory(properties.getSocial().getQq().getProviderId(),
						properties.getSocial().getQq().getAppId(), properties.getSocial().getQq().getAppSecret());
		connectionFactoryConfigurer.addConnectionFactory(qqConnectionFactory);
	}

	/**
	 * 配置本地数据库链接,用于存储和取出用户信息
	 */
	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		/**
		 * 参数:数据源,连接,加密(此处无加密),需要在本地数据库建表,建表语句可在JdbcUsersConnectionRepository源码包中查找
		 * 数据库表名UserConnection不可修改,只可加前缀等,数据库中表字段和适配器中ConnectionValues的值对应
		 */
		JdbcUsersConnectionRepository connectionRepository =
				new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
		// 设置表前缀,也可不设置
		connectionRepository.setTablePrefix("third_");
		// 若是不需要在登录服务提供商之后强制注册,则可以注册一个ConnectionSignUp实现类
		if (signUp != null) {
			connectionRepository.setConnectionSignUp(signUp);
		}
		return connectionRepository;
	}

	/**
	 * 自定义security拦截的social登录,也不可以直接返回一个SpringSocialConfigurer
	 */
	@Bean
	public SpringSocialConfigurer userSocialConfigurer() {
		QqSocialConfigurer configurer = new QqSocialConfigurer("filterUrl");
		/**
		 * 自定义注册页面,默认是/signin.在自定义上文的getUsersConnectionRepository方法中设置ConnectionSignUp的实现类即可
		 */
		configurer.signupUrl("自定义的登录页面,默认的是/signin");
		return configurer;
	}

	/**
	 * spring提供的注册工具类,会将授权服务器的信息提供给自定义的注册接口,见{@link UserCrl#getSocialUser()}
	 * 
	 * @param connectionFactoryLocator 连接
	 */
	@Bean
	public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator) {
		return new ProviderSignInUtils(connectionFactoryLocator,
				getUsersConnectionRepository(connectionFactoryLocator));
	}

	/**
	 * FIXME
	 */
	@Override
	public UserIdSource getUserIdSource() {
		return new UserQq();
	}
}