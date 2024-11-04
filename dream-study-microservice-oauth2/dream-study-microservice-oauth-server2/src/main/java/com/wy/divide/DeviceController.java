package com.wy.divide;

import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.wy.core.CustomizerOAuth2ParameterNames;

import dream.flying.flower.framework.security.constant.ConstAuthorization;
import dream.flying.flower.result.Result;
import lombok.SneakyThrows;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2024-11-04 17:54:19
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class DeviceController {

	@SneakyThrows
	@ResponseBody
	@GetMapping(value = "/oauth2/consent/redirect")
	public Result<String> consentRedirect(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(OAuth2ParameterNames.SCOPE) String scope,
			@RequestParam(OAuth2ParameterNames.STATE) String state,
			@RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
			@RequestHeader(name = ConstAuthorization.NONCE_HEADER_NAME, required = false) String nonceId,
			@RequestParam(name = CustomizerOAuth2ParameterNames.USER_CODE, required = false) String userCode) {

		// 携带当前请求参数与nonceId重定向至前端页面
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(CONSENT_PAGE_URI)
				.queryParam(OAuth2ParameterNames.SCOPE, UriUtils.encode(scope, StandardCharsets.UTF_8))
				.queryParam(OAuth2ParameterNames.STATE, UriUtils.encode(state, StandardCharsets.UTF_8))
				.queryParam(OAuth2ParameterNames.CLIENT_ID, clientId)
				.queryParam(OAuth2ParameterNames.USER_CODE, userCode)
				.queryParam(NONCE_HEADER_NAME, ObjectUtils.isEmpty(nonceId) ? session.getId() : nonceId);

		String uriString = uriBuilder.build(Boolean.TRUE).toUriString();
		if (ObjectUtils.isEmpty(userCode) || !UrlUtils.isAbsoluteUrl(DEVICE_ACTIVATE_URI)) {
			// 不是设备码模式或者设备码验证页面不是前后端分离的，无需返回json，直接重定向
			redirectStrategy.sendRedirect(request, response, uriString);
			return null;
		}
		// 兼容设备码，需响应JSON，由前端进行跳转
		return Result.success(uriString);
	}
}