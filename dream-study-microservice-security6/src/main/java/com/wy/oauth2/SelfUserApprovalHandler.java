package com.wy.oauth2;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;

import com.wy.entity.OauthClientDetails;
import com.wy.service.OauthService;

/**
 * 自定义tokenstore处理器
 * 
 * @author 飞花梦影
 * @date 2022-09-13 17:30:38
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class SelfUserApprovalHandler extends TokenStoreUserApprovalHandler {

	private OauthService oauthService;

	public SelfUserApprovalHandler() {
	}

	@Override
	public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
		if (super.isApproved(authorizationRequest, userAuthentication)) {
			return true;
		}
		if (!userAuthentication.isAuthenticated()) {
			return false;
		}

		OauthClientDetails clientDetails = oauthService.loadOauthClientDetails(authorizationRequest.getClientId());
		return clientDetails != null && clientDetails.trusted();

	}

	public void setOauthService(OauthService oauthService) {
		this.oauthService = oauthService;
	}
}