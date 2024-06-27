package com.wy.social.qq;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;

import com.wy.entity.UserQq;
import com.wy.social.IToken;

/**
 * 构建第三方client(本项目)与服务提供商(qq)的连接
 * 
 * @auther 飞花梦影
 * @date 2019-09-23 00:15:36
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class QqConnectionFactory extends OAuth2ConnectionFactory<IToken<UserQq>> {

	/**
	 * 构建基本的连接
	 * 
	 * @param providerId 服务提供商,唯一标识,由本系统自行设定,不重复即可
	 * @param serviceProvider
	 * @param apiAdapter
	 */
	public QqConnectionFactory(String providerId, String appId, String appSecret) {
		super(providerId, new QqOAuth2ServiceProvider(appId, appSecret), new QqApiAdapter());
	}
}