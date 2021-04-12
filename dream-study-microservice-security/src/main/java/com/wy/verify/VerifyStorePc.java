package com.wy.verify;

import org.springframework.context.annotation.Configuration;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.context.request.ServletWebRequest;

import com.wy.common.Constants;

/**
 * @apiNote 电脑端存储验证码
 * @author ParadiseWY
 * @date 2019年9月29日
 */
@Configuration
public class VerifyStorePc implements VerifyStore {

	SessionStrategy sessionStraegy = new HttpSessionSessionStrategy();
	
	@Override
	public String sourceType() {
		return "pc";
	}

	@Override
	public String generateKey(ServletWebRequest webRequest) {
		return Constants.SESSION_KEY_PREFIX;
	}

	@Override
	public void store(ServletWebRequest webRequest, Object value) {
		sessionStraegy.setAttribute(webRequest, generateKey(webRequest), value);
	}

	@Override
	public Object getStore(ServletWebRequest webRequest) {
		return sessionStraegy.getAttribute(webRequest, generateKey(webRequest));
	}

	@Override
	public void removeStore(ServletWebRequest webRequest) {
		sessionStraegy.removeAttribute(webRequest, generateKey(webRequest));
	}
}