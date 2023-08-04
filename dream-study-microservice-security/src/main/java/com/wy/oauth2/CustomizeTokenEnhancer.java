package com.wy.oauth2;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.google.common.collect.ImmutableMap;

/**
 * 自定义的Token增强器,因为token的生成是私有的,
 * 见{@link DefaultTokenServices#createAccessToken(OAuth2Authentication)}
 * ->{@link DefaultTokenServices#createAccessToken(OAuth2Authentication,OAuth2RefreshToken)},
 * 所有需要增强器修改token的生成方式
 *
 * @author 飞花梦影
 * @date 2021-07-01 10:37:29
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@SuppressWarnings("deprecation")
public class CustomizeTokenEnhancer implements TokenEnhancer {

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		((DefaultOAuth2AccessToken) accessToken)
				.setAdditionalInformation(ImmutableMap.of("kuozhanxinxi", "kuozhanxinxi1"));
		return accessToken;
	}
}