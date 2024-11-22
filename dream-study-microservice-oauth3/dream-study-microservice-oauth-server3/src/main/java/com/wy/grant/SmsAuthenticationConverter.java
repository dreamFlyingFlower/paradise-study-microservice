package com.wy.grant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import com.wy.helpers.SecurityOAuth2Helpers;

import dream.flying.flower.framework.security.constant.ConstOAuthGrantType;
import dream.flying.flower.framework.security.constant.ConstOAuthParameter;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 短信验证码登录Token转换器
 *
 * @author 飞花梦影
 * @date 2024-09-18 22:17:41
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class SmsAuthenticationConverter implements AuthenticationConverter {

	static final String ACCESS_TOKEN_REQUEST_ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";

	@Override
	public Authentication convert(HttpServletRequest request) {
		// grant_type (REQUIRED)
		String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
		if (!ConstOAuthGrantType.SMS_CODE.getValue().equals(grantType)) {
			return null;
		}

		// 目前是客户端认证信息
		Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();

		// 获取请求中的参数
		MultiValueMap<String, String> parameters = SecurityOAuth2Helpers.getParameters(request);

		// scope (OPTIONAL)
		String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
		if (StringUtils.hasText(scope) && parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
			SecurityOAuth2Helpers.throwError(OAuth2ErrorCodes.INVALID_REQUEST,
					"OAuth 2.0 Parameter: " + OAuth2ParameterNames.SCOPE, ACCESS_TOKEN_REQUEST_ERROR_URI);
		}
		Set<String> requestedScopes = null;
		if (StringUtils.hasText(scope)) {
			requestedScopes = new HashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
		}

		// Mobile phone number (REQUIRED)
		String username = parameters.getFirst(ConstOAuthParameter.PHONE);
		if (!StringUtils.hasText(username) || parameters.get(ConstOAuthParameter.PHONE).size() != 1) {
			SecurityOAuth2Helpers.throwError(OAuth2ErrorCodes.INVALID_REQUEST,
					"OAuth 2.0 Parameter: " + ConstOAuthParameter.PHONE, ACCESS_TOKEN_REQUEST_ERROR_URI);
		}

		// SMS verification code (REQUIRED)
		String password = parameters.getFirst(OAuth2ParameterNames.CODE);
		if (!StringUtils.hasText(password) || parameters.get(OAuth2ParameterNames.CODE).size() != 1) {
			SecurityOAuth2Helpers.throwError(OAuth2ErrorCodes.INVALID_REQUEST,
					"OAuth 2.0 Parameter: " + OAuth2ParameterNames.CODE, ACCESS_TOKEN_REQUEST_ERROR_URI);
		}

		// 提取附加参数
		Map<String, Object> additionalParameters = new HashMap<>();
		parameters.forEach((key, value) -> {
			if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) && !key.equals(OAuth2ParameterNames.CLIENT_ID)) {
				additionalParameters.put(key, value.get(0));
			}
		});

		// 构建AbstractAuthenticationToken子类实例并返回
		return new SmsAuthenticationToken(ConstOAuthGrantType.SMS_CODE, clientPrincipal, requestedScopes,
				additionalParameters);
	}
}