package com.wy.oauth;

import java.time.temporal.ChronoUnit;

import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.google.common.collect.ImmutableMap;
import com.wy.properties.OAuthServerSecurityProperties;

import dream.flying.flower.helper.DateTimeHelper;

/**
 * 自定义Token生成器,因为Token的生成是私有的
 *
 * 见{@link DefaultTokenServices#createAccessToken(OAuth2Authentication)}
 * ->{@link DefaultTokenServices#createAccessToken(OAuth2Authentication,OAuth2RefreshToken)},
 * 所有需要增强器修改Token的生成方式
 *
 * @author 飞花梦影
 * @date 2024-07-14 00:09:38
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Deprecated
public class CustomizeTokenEnhancer implements TokenEnhancer {

	private OAuthServerSecurityProperties oauthServerSecurityProperties;

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication auth) {
		if (accessToken instanceof DefaultOAuth2AccessToken) {
			DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
			// 添加授权码
			String userKey = auth.getUserAuthentication().getPrincipal().toString();
			token.setValue(JwtHelper.encode(userKey, new RsaSigner(oauthServerSecurityProperties.getJwtPrivateKey()))
					.getEncoded());
			// 2秒过后重新认证 ->
			// 本系统只依赖于工具类生成的token,不依赖于springsecurity的token,这么做是方便token过期后,springsecurity这边不能认证的情况
			// 因为springsecurity的token未过期,就不会给你进行重新登录
			token.setExpiration(DateTimeHelper.plusDate(2, ChronoUnit.SECONDS));
			// 添加扩展信息
			token.setAdditionalInformation(ImmutableMap.of("kuozhanxinxi", "kuozhanxinxi1"));
			return token;
		}
		return accessToken;
	}
}