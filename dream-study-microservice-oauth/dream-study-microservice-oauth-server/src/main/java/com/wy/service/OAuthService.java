package com.wy.service;

import java.util.List;

import com.wy.dto.OAuthClientDetailDTO;
import com.wy.entity.OAuthClientDetail;

public interface OAuthService {

	OAuthClientDetail loadOauthClientDetails(String clientId);

	List<OAuthClientDetailDTO> loadAllOauthClientDetails();

	void archiveOauthClientDetails(String clientId);

	OAuthClientDetailDTO loadOauthClientDetailsDto(String clientId);

	void registerClientDetails(OAuthClientDetailDTO formDto);
}