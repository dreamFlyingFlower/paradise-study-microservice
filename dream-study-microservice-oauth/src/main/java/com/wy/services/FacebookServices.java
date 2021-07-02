package com.wy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Facebook相关操作
 *
 * @author 飞花梦影
 * @date 2021-07-02 09:22:12
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Service
public class FacebookServices {

	@Autowired
	private OAuth2AuthorizedClientService auth2AuthorizedClientService;

	/**
	 * 从facebook请求验证完成之后,获得验证后facebook返回的信息,包括accessToken
	 * 
	 * @return accessToken
	 */
	public String getAccessToken() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
		OAuth2AuthorizedClient client = auth2AuthorizedClientService
				.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());
		String accessToken = client.getAccessToken().getTokenValue();
		return accessToken;
	}
}