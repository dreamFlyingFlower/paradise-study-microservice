package com.wy.verify;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.ServletWebRequest;

import com.wy.properties.DreamSecurityProperties;

import dream.flying.flower.lang.StrHelper;
import dream.flying.flower.result.ResultException;

/**
 * 手机存储验证
 *
 * @author 飞花梦影
 * @date 2019-09-29 23:40:32
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class VerifyStoreMobile implements VerifyStore {

	@Autowired
	private DreamSecurityProperties dreamSecurityProperties;

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	@Override
	public String requestSource() {
		return "mobile";
	}

	private String getDeviceId(ServletWebRequest webRequest) {
		String deviceId = webRequest.getRequest()
				.getParameter(dreamSecurityProperties.getVerify().getMobile().getDeviceIdParam());
		if (StrHelper.isBlank(deviceId)) {
			throw new ResultException("手机设备编号为空");
		}
		return deviceId;
	}

	@Override
	public String generateKey(ServletWebRequest webRequest) {
		return webRequest.getRequest().getParameter(dreamSecurityProperties.getVerify().getMobile().getDeviceIdParam());
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