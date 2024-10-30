package com.wy.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.wy.entity.OAuthClientDetail;
import com.wy.util.GuidGenerator;

import dream.flying.flower.helper.DateTimeHelper;
import dream.flying.flower.lang.StrHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthClientDetailDTO implements Serializable {

	private static final long serialVersionUID = -6650829879035029696L;

	private String createTime;

	private boolean archived;

	@Builder.Default
	private String clientId = GuidGenerator.generate();

	private String resourceIds;

	@Builder.Default
	private String clientSecret = GuidGenerator.generateClientSecret();

	private String scope;

	private String authorizedGrantTypes;

	private String webServerRedirectUri;

	private String authorities;

	private Integer accessTokenValidity;

	private Integer refreshTokenValidity;

	private String additionalInformation;

	private boolean trusted;

	public OAuthClientDetailDTO(OAuthClientDetail clientDetails) {
		this.clientId = clientDetails.clientId();
		this.clientSecret = clientDetails.clientSecret();
		this.scope = clientDetails.scope();
		this.createTime = DateTimeHelper.formatDateTime(clientDetails.createTime());
		this.archived = clientDetails.archived();
		this.resourceIds = clientDetails.resourceIds();
		this.webServerRedirectUri = clientDetails.webServerRedirectUri();
		this.authorities = clientDetails.authorities();
		this.accessTokenValidity = clientDetails.accessTokenValidity();
		this.refreshTokenValidity = clientDetails.refreshTokenValidity();
		this.additionalInformation = clientDetails.additionalInformation();
		this.trusted = clientDetails.trusted();
		this.authorizedGrantTypes = clientDetails.authorizedGrantTypes();
	}

	public String getScopeWithBlank() {
		if (scope != null && scope.contains(",")) {
			return scope.replaceAll(",", " ");
		}
		return scope;
	}

	public static List<OAuthClientDetailDTO> toDtos(List<OAuthClientDetail> clientDetailses) {
		List<OAuthClientDetailDTO> dtos = new ArrayList<>(clientDetailses.size());
		for (OAuthClientDetail clientDetailse : clientDetailses) {
			dtos.add(new OAuthClientDetailDTO(clientDetailse));
		}
		return dtos;
	}

	public boolean isContainsAuthorizationCode() {
		return this.authorizedGrantTypes.contains("authorization_code");
	}

	public boolean isContainsPassword() {
		return this.authorizedGrantTypes.contains("password");
	}

	public boolean isContainsImplicit() {
		return this.authorizedGrantTypes.contains("implicit");
	}

	public boolean isContainsClientCredentials() {
		return this.authorizedGrantTypes.contains("client_credentials");
	}

	public boolean isContainsRefreshToken() {
		return this.authorizedGrantTypes.contains("refresh_token");
	}

	public OAuthClientDetail createDomain() {
		OAuthClientDetail clientDetails = new OAuthClientDetail().clientId(clientId)
		        // encrypted client secret
		        .clientSecret(new BCryptPasswordEncoder().encode(clientSecret)).resourceIds(resourceIds)
		        .authorizedGrantTypes(authorizedGrantTypes).scope(scope);

		if (StrHelper.isNotBlank(webServerRedirectUri)) {
			clientDetails.webServerRedirectUri(webServerRedirectUri);
		}

		if (StrHelper.isNotBlank(authorities)) {
			clientDetails.authorities(authorities);
		}

		clientDetails.accessTokenValidity(accessTokenValidity).refreshTokenValidity(refreshTokenValidity)
		        .trusted(trusted);

		if (StrHelper.isNotEmpty(additionalInformation)) {
			clientDetails.additionalInformation(additionalInformation);
		}

		return clientDetails;
	}
}