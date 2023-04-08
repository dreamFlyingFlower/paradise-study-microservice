package com.wy.oauth2;

import java.util.Map;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.stereotype.Component;

/**
 * 如果认证服务器在JWT中传输了其他数据,默认的{@link DefaultAccessTokenConverter}无法解析,需要自定义解析
 *
 * @author 飞花梦影
 * @date 2023-04-08 14:43:36
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Component
public class SelfAccessTokenConverter extends DefaultAccessTokenConverter {

	@Override
	public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
		OAuth2Authentication auth2Authentication = super.extractAuthentication(map);
		// 将JWT认证信息中额外数据设置到认证信息中
		auth2Authentication.setDetails(map);
		return auth2Authentication;
	}
}