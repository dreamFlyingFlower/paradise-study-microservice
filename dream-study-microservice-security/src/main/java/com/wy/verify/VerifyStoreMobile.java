package com.wy.verify;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.ServletWebRequest;

import com.wy.properties.UserProperties;

import dream.flying.flower.lang.StrHelper;
import dream.flying.flower.result.ResultException;

/**
 * @apiNote 手机存储验证
 * @author ParadiseWY
 * @date 2019年9月29日
 */
@Configuration
public class VerifyStoreMobile implements VerifyStore {

	@Autowired
	private UserProperties userProperties;

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	@Override
	public String requestSource() {
		return "mobile";
	}

	private String getDeviceId(ServletWebRequest webRequest) {
		String deviceId = webRequest.getRequest()
				.getParameter(userProperties.getVerify().getMobile().getDeviceIdParam());
		if (StrHelper.isBlank(deviceId)) {
			throw new ResultException("手机设备编号为空");
		}
		return deviceId;
	}

	@Override
	public String generateKey(ServletWebRequest webRequest) {
		return webRequest.getRequest().getParameter(userProperties.getVerify().getMobile().getDeviceIdParam());
	}

	@Override
	public void store(ServletWebRequest webRequest, Object value) {
		redisTemplate.opsForValue().set(getDeviceId(webRequest), value, 30, TimeUnit.MINUTES);
	}

	@Override
	public Object getStore(ServletWebRequest webRequest) {
		return redisTemplate.opsForValue().get(getDeviceId(webRequest));
	}

	@Override
	public void removeStore(ServletWebRequest webRequest) {
		redisTemplate.opsForValue().set(getDeviceId(webRequest), null, -1, TimeUnit.MINUTES);
	}
}