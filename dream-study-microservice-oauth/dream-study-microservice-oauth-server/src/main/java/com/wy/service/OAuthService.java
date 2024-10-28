package com.wy.service;

import java.util.List;

import com.wy.entity.OAuthClientDetail;
import com.wy.entity.dto.OAuthClientDetailDTO;

public interface OAuthService {

	OAuthClientDetail loadOauthClientDetails(String clientId);

	List<OAuthClientDetailDTO> loadAllOauthClientDetails();

	void archiveOauthClientDetails(String clientId);

	OAuthClientDetailDTO loadOauthClientDetailsDto(String clientId);

	void registerClientDetails(OAuthClientDetailDTO formDto);
}