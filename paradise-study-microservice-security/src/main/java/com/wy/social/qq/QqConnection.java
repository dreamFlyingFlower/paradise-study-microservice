package com.wy.social.qq;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;

import com.wy.entity.UserQq;
import com.wy.social.IToken;

/**
 * @apiNote 构建与服务器端的连接
 * @author ParadiseWY
 * @date 2019年9月23日
 */
public class QqConnection extends OAuth2ConnectionFactory<IToken<UserQq>> {

	/**
	 * 构建基本的连接
	 * 
	 * @param providerId 服务提供商,唯一标识
	 * @param serviceProvider
	 * @param apiAdapter
	 */
	public QqConnection(String providerId, String appId, String appSecret) {
		super(providerId, new QqProvider(appId, appSecret), new QqApiAdapter());
	}
}