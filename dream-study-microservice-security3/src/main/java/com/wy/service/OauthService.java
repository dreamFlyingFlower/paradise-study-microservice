package com.wy.service;

import java.util.List;

import com.wy.entity.OauthClientDetails;
import com.wy.entity.dto.OauthClientDetailsDTO;

public interface OauthService {

	OauthClientDetails loadOauthClientDetails(String clientId);

	List<OauthClientDetailsDTO> loadAllOauthClientDetails();

	void archiveOauthClientDetails(String clientId);

	OauthClientDetailsDTO loadOauthClientDetailsDto(String clientId);

	void registerClientDetails(OauthClientDetailsDTO formDto);
}