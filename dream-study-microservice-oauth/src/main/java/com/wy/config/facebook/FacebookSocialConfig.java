package com.wy.config.facebook;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.context.annotation.RequestScope;

import com.wy.oauth.facebook.FacebookApiBinding;

/**
 * 第三方登录Facebook
 *
 * @author 飞花梦影
 * @date 2021-07-01 20:09:50
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
public class FacebookSocialConfig {

	@Bean
	@RequestScope
	public FacebookApiBinding facebook(OAuth2AuthorizedClientService clientService) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String accessToken = null;
		if (authentication.getClass().isAssignableFrom(OAuth2AuthenticationToken.class)) {
			OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
			String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
			if (clientRegistrationId.equals("facebook")) {
				OAuth2AuthorizedClient client =
						clientService.loadAuthorizedClient(clientRegistrationId, oauthToken.getName());
				accessToken = client.getAccessToken().getTokenValue();
			}
		}
		return new FacebookApiBinding(accessToken);
	}
}