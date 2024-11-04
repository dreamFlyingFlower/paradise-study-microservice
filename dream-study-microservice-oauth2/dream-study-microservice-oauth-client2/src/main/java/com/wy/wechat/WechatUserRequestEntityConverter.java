package com.wy.wechat;

import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.web.util.UriComponentsBuilder;

import dream.flying.flower.framework.security.constant.ConstOAuthClient;

/**
 * 修改请求用户信息的参数,请求用户信息时添加openid与access_token参数
 *
 * @author 飞花梦影
 * @date 2024-11-04 09:30:35
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class WechatUserRequestEntityConverter extends OAuth2UserRequestEntityConverter {

	@Override
	public RequestEntity<?> convert(OAuth2UserRequest userRequest) {
		// 获取配置文件中的客户端信息
		ClientRegistration clientRegistration = userRequest.getClientRegistration();
		if (ConstOAuthClient.OAUTH2_CLIENT_LOGIN_WECHAT.equals(clientRegistration.getRegistrationId())) {
			// 对于微信登录的特殊处理,请求用户信息时添加openid与access_token参数
			Object openid =
					userRequest.getAdditionalParameters().get(ConstOAuthClient.OAUTH2_CIENT_WECHAT_PARAMETER_OPENID);
			URI uri = UriComponentsBuilder
					.fromUriString(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri())
					.queryParam(ConstOAuthClient.OAUTH2_CIENT_WECHAT_PARAMETER_OPENID, openid)
					.queryParam(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.getAccessToken().getTokenValue())
					.build()
					.toUri();
			return new RequestEntity<>(HttpMethod.GET, uri);
		}
		return super.convert(userRequest);
	}
}