package com.wy.oauth.customizer;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;

import com.wy.entity.OAuthClientDetail;
import com.wy.service.OAuthService;

/**
 * 自定义tokenstore处理器
 * 
 * @author 飞花梦影
 * @date 2022-09-13 17:30:38
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@SuppressWarnings("deprecation")
public class CustomizerUserApprovalHandler extends TokenStoreUserApprovalHandler {

	private OAuthService oauthService;

	public CustomizerUserApprovalHandler() {
	}

	@Override
	public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
		if (super.isApproved(authorizationRequest, userAuthentication)) {
			return true;
		}
		if (!userAuthentication.isAuthenticated()) {
			return false;
		}

		OAuthClientDetail clientDetails = oauthService.loadOauthClientDetails(authorizationRequest.getClientId());
		return clientDetails != null && clientDetails.trusted();

	}

	public void setOauthService(OAuthService oauthService) {
		this.oauthService = oauthService;
	}
}