package com.wy.divide;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import dream.flying.flower.framework.security.constant.ConstAuthorization;
import dream.flying.flower.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2024-11-04 17:54:19
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RequiredArgsConstructor
public class DeviceController {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	private final OAuth2AuthorizationConsentService oauth2AuthorizationConsentService;

	@SneakyThrows
	@ResponseBody
	@GetMapping(value = "/oauth2/consent/redirect")
	public Result<String> consentRedirect(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(OAuth2ParameterNames.SCOPE) String scope,
			@RequestParam(OAuth2ParameterNames.STATE) String state,
			@RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
			@RequestHeader(name = ConstAuthorization.NONCE_HEADER_NAME, required = false) String nonceId,
			@RequestParam(name = OAuth2ParameterNames.USER_CODE, required = false) String userCode) {

		// 携带当前请求参数与nonceId重定向至前端页面
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(ConstAuthorization.CONSENT_PAGE_URI)
				.queryParam(OAuth2ParameterNames.SCOPE, UriUtils.encode(scope, StandardCharsets.UTF_8))
				.queryParam(OAuth2ParameterNames.STATE, UriUtils.encode(state, StandardCharsets.UTF_8))
				.queryParam(OAuth2ParameterNames.CLIENT_ID, clientId)
				.queryParam(OAuth2ParameterNames.USER_CODE, userCode)
				.queryParam(ConstAuthorization.NONCE_HEADER_NAME,
						ObjectUtils.isEmpty(nonceId) ? session.getId() : nonceId);

		String uriString = uriBuilder.build(Boolean.TRUE).toUriString();
		if (ObjectUtils.isEmpty(userCode) || !UrlUtils.isAbsoluteUrl(ConstAuthorization.DEVICE_ACTIVATE_URI)) {
			// 不是设备码模式或者设备码验证页面不是前后端分离的,无需返回json,直接重定向
			redirectStrategy.sendRedirect(request, response, uriString);
			return null;
		}
		// 兼容设备码,需响应JSON,由前端进行跳转
		return Result.ok(uriString);
	}

	@ResponseBody
	@GetMapping(value = "/oauth2/consent/parameters")
	public Result<Map<String, Object>> consentParameters(Principal principal,
			@RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
			@RequestParam(OAuth2ParameterNames.SCOPE) String scope,
			@RequestParam(OAuth2ParameterNames.STATE) String state,
			@RequestParam(name = OAuth2ParameterNames.USER_CODE, required = false) String userCode) {

		// 获取consent页面所需的参数
		Map<String, Object> consentParameters = getConsentParameters(scope, state, clientId, userCode, principal);

		return Result.ok(consentParameters);
	}

	@GetMapping(value = "/oauth2/consent")
	public String consent(Principal principal, Model model,
			@RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
			@RequestParam(OAuth2ParameterNames.SCOPE) String scope,
			@RequestParam(OAuth2ParameterNames.STATE) String state,
			@RequestParam(name = OAuth2ParameterNames.USER_CODE, required = false) String userCode) {

		// 获取consent页面所需的参数
		Map<String, Object> consentParameters = getConsentParameters(scope, state, clientId, userCode, principal);
		// 转至model中,让框架渲染页面
		consentParameters.forEach(model::addAttribute);

		return "consent";
	}

	/**
	 * 根据授权确认相关参数获取授权确认与未确认的scope相关参数
	 *
	 * @param scope scope权限
	 * @param state state
	 * @param clientId 客户端id
	 * @param userCode 设备码授权流程中的用户码
	 * @param principal 当前认证信息
	 * @return 页面所需数据
	 */
	private Map<String, Object> getConsentParameters(String scope, String state, String clientId, String userCode,
			Principal principal) {
		// Remove scopes that were already approved
		Set<String> scopesToApprove = new HashSet<>();
		Set<String> previouslyApprovedScopes = new HashSet<>();
		RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
		if (registeredClient == null) {
			throw new RuntimeException("客户端不存在");
		}
		OAuth2AuthorizationConsent currentAuthorizationConsent =
				this.oauth2AuthorizationConsentService.findById(registeredClient.getId(), principal.getName());
		Set<String> authorizedScopes;
		if (currentAuthorizationConsent != null) {
			authorizedScopes = currentAuthorizationConsent.getScopes();
		} else {
			authorizedScopes = Collections.emptySet();
		}
		for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
			if (OidcScopes.OPENID.equals(requestedScope)) {
				continue;
			}
			if (authorizedScopes.contains(requestedScope)) {
				previouslyApprovedScopes.add(requestedScope);
			} else {
				scopesToApprove.add(requestedScope);
			}
		}

		Map<String, Object> parameters = new HashMap<>(7);
		parameters.put("clientId", registeredClient.getClientId());
		parameters.put("clientName", registeredClient.getClientName());
		parameters.put("state", state);
		parameters.put("scopes", withDescription(scopesToApprove));
		parameters.put("previouslyApprovedScopes", withDescription(previouslyApprovedScopes));
		parameters.put("principalName", principal.getName());
		parameters.put("userCode", userCode);
		if (StringUtils.hasText(userCode)) {
			parameters.put("requestURI", "/oauth2/device_verification");
		} else {
			parameters.put("requestURI", "/oauth2/authorize");
		}
		return parameters;
	}

	/**
	 * 重定向至设备码校验页面时携带当前sessionId(nonceId)重定向至前端项目中,由认证服务重定向,携带sessionId以保持登录状态
	 * 
	 * @param session
	 * @param userCode
	 * @return
	 */
	@GetMapping("/activate/redirect")
	public String activateRedirect(HttpSession session,
			@RequestParam(value = "user_code", required = false) String userCode) {

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(ConstAuthorization.DEVICE_ACTIVATE_URI)
				.queryParam("userCode", userCode)
				.queryParam(ConstAuthorization.NONCE_HEADER_NAME, session.getId());
		return "redirect:" + uriBuilder.build(Boolean.TRUE).toUriString();
	}

}