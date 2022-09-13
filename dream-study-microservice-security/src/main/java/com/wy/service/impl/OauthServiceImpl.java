package com.wy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.wy.entity.OauthClientDetails;
import com.wy.entity.dto.OauthClientDetailsDTO;
import com.wy.repository.OauthRepository;
import com.wy.service.OauthService;
import com.wy.util.WebUtils;

import lombok.extern.slf4j.Slf4j;

@Service("oauthService")
@Slf4j
public class OauthServiceImpl implements OauthService {

	@Autowired
	private OauthRepository oauthRepository;

	@Override
	@Transactional(readOnly = true)
	public OauthClientDetails loadOauthClientDetails(String clientId) {
		return oauthRepository.findOauthClientDetails(clientId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OauthClientDetailsDTO> loadAllOauthClientDetails() {
		List<OauthClientDetails> clientDetailses = oauthRepository.findAllOauthClientDetails();
		return OauthClientDetailsDTO.toDtos(clientDetailses);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void archiveOauthClientDetails(String clientId) {
		oauthRepository.updateOauthClientDetailsArchive(clientId, true);
		log.debug("{}|Update OauthClientDetails(clientId: {}) archive = true", WebUtils.getIp(), clientId);
	}

	@Override
	@Transactional(readOnly = true)
	public OauthClientDetailsDTO loadOauthClientDetailsDto(String clientId) {
		final OauthClientDetails oauthClientDetails = oauthRepository.findOauthClientDetails(clientId);
		return oauthClientDetails != null ? new OauthClientDetailsDTO(oauthClientDetails) : null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void registerClientDetails(OauthClientDetailsDTO formDto) {
		OauthClientDetails clientDetails = formDto.createDomain();
		oauthRepository.saveOauthClientDetails(clientDetails);
		log.debug("{}|Save OauthClientDetails: {}", WebUtils.getIp(), clientDetails);
	}
}