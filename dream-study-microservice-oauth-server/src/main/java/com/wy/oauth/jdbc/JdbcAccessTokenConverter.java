package com.wy.oauth.jdbc;

import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import com.wy.util.IpHelper;

/**
 * 自定义JWT令牌中传输的信息
 *
 * @author 飞花梦影
 * @date 2023-04-08 13:16:38
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Configuration
public class JdbcAccessTokenConverter extends DefaultAccessTokenConverter {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		Map<String, Object> result = (Map<String, Object>) super.convertAccessToken(token, authentication);
		result.put("clientIp", IpHelper.getIp());
		return result;
	}
}