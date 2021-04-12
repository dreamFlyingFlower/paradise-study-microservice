package com.wy.social.qq;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.SpringSocialConfigurer;

import com.wy.entity.UserQq;

/**
 * @apiNote 将得到的用户信息存入到本地数据库中
 * @author ParadiseWY
 * @date 2019年9月23日
 */
@Configuration
@EnableSocial
public class SocialAdapter extends SocialConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Autowired(required = false)
	private SocialConnectionSignUp signUp;

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(
			ConnectionFactoryLocator connectionFactoryLocator) {
		/**
		 * 数据库连接,参数:数据源,连接,加密(此处无加密),需要在本地数据库中新建表,
		 * 建表语句可在JdbcUsersConnectionRepository所在源码包中查找,表名不可修改,只可加前缀等
		 * 数据库中表字段和适配器中ConnectionValues的值对应
		 */
		JdbcUsersConnectionRepository connectionRepository = new JdbcUsersConnectionRepository(
				dataSource, connectionFactoryLocator, Encryptors.noOpText());
		/**
		 * 若是不需要在登录第三方服务器之后强制注册,则可以注册一个ConnectionSignUp实现类
		 */
		if (signUp != null) {
			connectionRepository.setConnectionSignUp(signUp);
		}
		return connectionRepository;
	}

	/**
	 * 自定义security拦截的social登录
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
	public ProviderSignInUtils providerSignInUtils(
			ConnectionFactoryLocator connectionFactoryLocator) {
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