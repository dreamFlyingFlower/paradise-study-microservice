package com.wy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.wy.entity.OAuthClientDetail;
import com.wy.entity.dto.OAuthClientDetailDTO;
import com.wy.repository.OAuthRepository;
import com.wy.service.OAuthService;

import dream.flying.flower.framework.web.helper.IpHelpers;
import lombok.extern.slf4j.Slf4j;

@Service("oauthService")
@Slf4j
public class OAuthServiceImpl implements OAuthService {

	@Autowired
	private OAuthRepository oauthRepository;

	@Override
	@Transactional(readOnly = true)
	public OAuthClientDetail loadOauthClientDetails(String clientId) {
		return oauthRepository.findOauthClientDetails(clientId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OAuthClientDetailDTO> loadAllOauthClientDetails() {
		List<OAuthClientDetail> clientDetailses = oauthRepository.findAllOauthClientDetails();
		return OAuthClientDetailDTO.toDtos(clientDetailses);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void archiveOauthClientDetails(String clientId) {
		oauthRepository.updateOauthClientDetailsArchive(clientId, true);
		log.debug("{}|Update OauthClientDetails(clientId: {}) archive = true", IpHelpers.getIp(), clientId);
	}

	@Override
	@Transactional(readOnly = true)
	public OAuthClientDetailDTO loadOauthClientDetailsDto(String clientId) {
		final OAuthClientDetail oauthClientDetails = oauthRepository.findOauthClientDetails(clientId);
		return oauthClientDetails != null ? new OAuthClientDetailDTO(oauthClientDetails) : null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void registerClientDetails(OAuthClientDetailDTO formDto) {
		OAuthClientDetail clientDetails = formDto.createDomain();
		oauthRepository.saveOauthClientDetails(clientDetails);
		log.debug("{}|Save OauthClientDetails: {}", IpHelpers.getIp(), clientDetails);
	}
}