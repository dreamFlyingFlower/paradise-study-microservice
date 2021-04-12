package com.wy.social.qq;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactory;

import com.wy.entity.UserQq;
import com.wy.properties.UserProperties;
import com.wy.social.IToken;

/**
 * @apiNote 启动social
 * @author ParadiseWY
 * @date 2019年9月23日
 */
@Configuration
@ConditionalOnProperty(prefix = "user.social.qq",name="app-id")
public class SocialAutoConfig extends SocialConfigurerAdapter {

	private UserProperties properties;

	public ConnectionFactory<IToken<UserQq>> createConnectionFactory() {
		return new QqConnection(properties.getSocial().getQq().getProviderId(),
				properties.getSocial().getQq().getAppId(),
				properties.getSocial().getQq().getAppSecret());
	}
}