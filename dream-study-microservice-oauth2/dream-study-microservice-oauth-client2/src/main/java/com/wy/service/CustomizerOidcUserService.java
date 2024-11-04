package com.wy.service;

import java.util.LinkedHashMap;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.StringUtils;

import com.wy.strategy.OAuth2UserConverterContext;
import com.wy.vo.OAuth2ClientVO;

import lombok.RequiredArgsConstructor;

/**
 * 自定义登录第三方认证服务器OIDC方式获取用户信息服务,可继承{@link DefaultOAuth2UserService}或实现{@link OAuth2UserService}
 *
 * @author 飞花梦影
 * @date 2024-11-03 10:48:04
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RequiredArgsConstructor
public class CustomizerOidcUserService extends OidcUserService {

	private final OAuth2ClientService oauth2ClientService;

	private final OAuth2UserConverterContext oauth2UserConverterContext;

	@Override
	public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
		// 获取三方用户信息
		OidcUser oidcUser = super.loadUser(userRequest);
		// 转为项目中的三方用户信息
		OAuth2ClientVO oauth2ThirdAccount = oauth2UserConverterContext.convert(userRequest, oidcUser);
		// 检查用户信息
		oauth2ClientService.checkAndSaveUser(oauth2ThirdAccount);
		OidcIdToken oidcIdToken = oidcUser.getIdToken();
		// 将loginType设置至attributes中
		LinkedHashMap<String, Object> attributes = new LinkedHashMap<>(oidcIdToken.getClaims());
		// 将RegistrationId当做登录类型
		attributes.put("loginType", userRequest.getClientRegistration().getRegistrationId());
		// 重新生成一个idToken
		OidcIdToken idToken = new OidcIdToken(oidcIdToken.getTokenValue(), oidcIdToken.getIssuedAt(),
				oidcIdToken.getExpiresAt(), attributes);
		String userNameAttributeName = userRequest.getClientRegistration()
				.getProviderDetails()
				.getUserInfoEndpoint()
				.getUserNameAttributeName();
		// 重新生成oidcUser
		if (StringUtils.hasText(userNameAttributeName)) {
			return new DefaultOidcUser(oidcUser.getAuthorities(), idToken, oidcUser.getUserInfo(),
					userNameAttributeName);
		}
		return new DefaultOidcUser(oidcUser.getAuthorities(), idToken, oidcUser.getUserInfo());
	}
}