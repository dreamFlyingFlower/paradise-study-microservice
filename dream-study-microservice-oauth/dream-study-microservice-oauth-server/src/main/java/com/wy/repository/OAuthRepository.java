package com.wy.repository;

import java.util.List;

import com.wy.entity.OAuthClientDetail;

public interface OAuthRepository {

	OAuthClientDetail findOauthClientDetails(String clientId);

	List<OAuthClientDetail> findAllOauthClientDetails();

	void updateOauthClientDetailsArchive(String clientId, boolean archive);

	void saveOauthClientDetails(OAuthClientDetail clientDetails);
}