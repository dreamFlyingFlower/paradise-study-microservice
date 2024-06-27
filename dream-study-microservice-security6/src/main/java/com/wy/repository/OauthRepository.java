package com.wy.repository;

import java.util.List;

import com.wy.entity.OauthClientDetails;

public interface OauthRepository {

	OauthClientDetails findOauthClientDetails(String clientId);

	List<OauthClientDetails> findAllOauthClientDetails();

	void updateOauthClientDetailsArchive(String clientId, boolean archive);

	void saveOauthClientDetails(OauthClientDetails clientDetails);
}