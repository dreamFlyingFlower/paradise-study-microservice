package com.wy.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

import com.wy.entity.UserQq;
import com.wy.properties.UserProperties;
import com.wy.social.qq.QqConnectionFactory;
import com.wy.social.qq.QqSocialConfigurer;
import com.wy.social.qq.SocialConnectionSignUp;

/**
 * Social相关配置,必须要开启{@link EnableSocial}
 * 
 * {@link SocialAuthenticationFilter}:所有以/auth开头的请求都会被拦截,失败的请求都会被以/signin开头的请求拦截.
 * 若需要登录的服务提供商为qq,则前端访问的地址为/auth/qq,而qq则是由本程序定义的providerId,必须唯一
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
	private SocialConnectionSignUp signUp;

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
		// 若是不需要在登录第三方服务器之后强制注册,则可以注册一个ConnectionSignUp实现类
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
		 * 设置注册页面,默认是/singin,可以自定义.此处为强制跳转注册页面
		 * 若不需要注册,则可查看SocialAuthenticationProvider#authenticate,#toUserId方法知晓原因
		 * 在自定义上文的getUsersConnectionRepository方法中设置ConnectionSignUp的实现类即可
		 */
		configurer.signupUrl("自定义的登录页面,默认的是/signin");
		return configurer;
	}

	/**
	 * spring提供的注册工具类,会将授权服务器的信息提供给自定义的注册接口
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