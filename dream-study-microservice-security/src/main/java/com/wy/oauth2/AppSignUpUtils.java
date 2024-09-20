package com.wy.oauth2;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import dream.flying.flower.framework.security.exception.AuthException;
import dream.flying.flower.lang.StrHelper;

/**
 * 系统默认第三方登录成功后使用{@link ProviderSignInUtils}从session中获取用户信息,但是手机登录是没有session的,要重写
 *
 * @author 飞花梦影
 * @date 2021-06-30 18:30:14
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Component
public class AppSignUpUtils {

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	@Autowired
	private UsersConnectionRepository usersConnectionRepository;

	@Autowired
	private ConnectionFactoryLocator connectionFactoryLocator;

	public void saveData(WebRequest request, ConnectionData connectionData) {
		redisTemplate.opsForValue().set(getKey(request), connectionData, 10, TimeUnit.MINUTES);
	}

	public void doPostSignUp(String userId, WebRequest request) {
		String key = getKey(request);
		if (!redisTemplate.hasKey(key)) {
			throw new AuthException("无法找到该用户社交账号信息");
		}
		ConnectionData connectionData = (ConnectionData) (redisTemplate.opsForValue().get(key));
		Connection<?> connection = connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId())
				.createConnection(connectionData);
		usersConnectionRepository.createConnectionRepository(userId).addConnection(connection);
		redisTemplate.delete(key);
	}

	private String getKey(WebRequest request) {
		// 从请求头中获取app的设备编号或其他唯一标识
		String deviceId = request.getHeader("deviceId");
		if (StrHelper.isBlank(deviceId)) {
			throw new AuthException("请求设备id不能为空");
		}
		return "app_login_social_" + deviceId;
	}
}