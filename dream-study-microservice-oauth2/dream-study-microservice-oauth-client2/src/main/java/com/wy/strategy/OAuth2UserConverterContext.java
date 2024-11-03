package com.wy.strategy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.wy.vo.OAuth2ClientVO;

import dream.flying.flower.helper.DateTimeHelper;
import lombok.RequiredArgsConstructor;

/**
 * 注入所有用户信息转换策略,根据传入的type(RegistrationId)获取对应的解析类
 *
 * @author 飞花梦影
 * @date 2024-11-03 22:58:29
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
@RequiredArgsConstructor
public class OAuth2UserConverterContext {

	/**
	 * 注入所有实例,map的key是实例在ioc中的名字
	 */
	private final Map<String, OAuth2UserConverterStrategy> userConverterStrategyMap;

	/**
	 * 获取转换器实例
	 *
	 * @param loginType 三方登录方式
	 * @return 转换器实例 {@link OAuth2UserConverterStrategy}
	 */
	public OAuth2UserConverterStrategy getInstance(String loginType) {
		if (ObjectUtils.isEmpty(loginType)) {
			throw new UnsupportedOperationException("登录方式不能为空");
		}
		OAuth2UserConverterStrategy userConverterStrategy = userConverterStrategyMap.get(loginType);
		if (userConverterStrategy == null) {
			throw new UnsupportedOperationException("不支持[" + loginType + "]登录方式获取用户信息转换器");
		}
		return userConverterStrategy;
	}

	/**
	 * 根据登录方式获取转换器实例,使用转换器获取用户信息
	 *
	 * @param userRequest 获取三方用户信息入参
	 * @param oAuth2User 三方登录获取到的认证信息
	 * @return {@link Oauth2ThirdAccount}
	 */
	public OAuth2ClientVO convert(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
		// 获取三方登录配置的registrationId,这里将他当做登录方式
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		// 转换用户信息
		OAuth2ClientVO oauth2ClientVo = this.getInstance(registrationId).convert(oAuth2User);
		// 获取AccessToken
		OAuth2AccessToken accessToken = userRequest.getAccessToken();
		// 设置token
		oauth2ClientVo.setCredentials(accessToken.getTokenValue());
		// 设置账号的方式
		oauth2ClientVo.setAuthorizationGrantTypes(registrationId);
		Instant expiresAt = accessToken.getExpiresAt();
		if (expiresAt != null) {
			LocalDateTime tokenExpiresAt = expiresAt.atZone(ZoneId.of("UTC")).toLocalDateTime();
			// token过期时间
			oauth2ClientVo.setCredentialsExpiresAt(DateTimeHelper.local2Date(tokenExpiresAt));
		}
		return oauth2ClientVo;
	}
}