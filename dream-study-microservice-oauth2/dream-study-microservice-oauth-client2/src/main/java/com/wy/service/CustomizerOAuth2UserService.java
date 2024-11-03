package com.wy.service;

import java.util.LinkedHashMap;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.wy.vo.OAuth2ClientVO;

import lombok.RequiredArgsConstructor;

/**
 * 自定义登录第三方认证服务器获取普通方式用户信息服务,可继承{@link DefaultOAuth2UserService}或实现{@link OAuth2UserService}
 *
 * @author 飞花梦影
 * @date 2024-11-03 10:48:04
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RequiredArgsConstructor
public class CustomizerOAuth2UserService extends DefaultOAuth2UserService {

	private final OAuth2ClientService oauth2ClientService;

	private final Oauth2UserConverterContext userConverterContext;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		// 转为项目中的三方用户信息
		OAuth2ClientVO oauth2ThirdAccount = userConverterContext.convert(userRequest, oAuth2User);
		// 检查用户信息
		oauth2ClientService.checkAndSaveUser(oauth2ThirdAccount);
		// 将loginType设置至attributes中
		LinkedHashMap<String, Object> attributes = new LinkedHashMap<>(oAuth2User.getAttributes());
		// 将yml配置的RegistrationId当做登录类型
		attributes.put("loginType", userRequest.getClientRegistration().getRegistrationId());
		String userNameAttributeName = userRequest.getClientRegistration()
				.getProviderDetails()
				.getUserInfoEndpoint()
				.getUserNameAttributeName();
		return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, userNameAttributeName);
	}
}