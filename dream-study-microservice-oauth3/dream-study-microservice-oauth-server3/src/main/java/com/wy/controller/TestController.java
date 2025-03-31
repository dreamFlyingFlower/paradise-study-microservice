package com.wy.controller;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dream.flying.flower.framework.json.JsonHelpers;

/**
 * 测试接口
 *
 * @author 飞花梦影
 * @date 2024-11-07 10:17:07
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RestController
@RequestMapping("test")
public class TestController {

	@GetMapping("getCode")
	public String getCode(@RequestParam(value = "code", required = false) String code) {
		System.out.println(code);
		return "success";
	}

	@GetMapping("/test01")
	@PreAuthorize("hasAnyAuthority('SCOPE_message.read','message.read')")
	public String test01() {
		return "test01";
	}

	@GetMapping("/test02")
	@PreAuthorize("hasAuthority('SCOPE_message.write')")
	public String test02() {
		return "test02";
	}

	@GetMapping("/app")
	@PreAuthorize("hasAuthority('app')")
	public String app() {
		return "app";
	}

	@GetMapping("/test03")
	public String test03() {
		return "test03";
	}

	/**
	 * authentication.credentials中的claims为Map,需要使用[]来获取属性
	 * 
	 * @return
	 */
	@PreAuthorize("isAuthenticated() and authentication.credentials.claims['scope'].contains('message.read')")
	@GetMapping("/test04")
	public String test04() {
		// System.out.println((authentication));
		return "test04";
	}

	@GetMapping("/test05")
	public String test05(@AuthenticationPrincipal Jwt jwt) {
		System.out.println(JsonHelpers.toString(jwt));
		return "test05";
	}

	@GetMapping("/test06")
	public String test06(Principal principal) {
		System.out.println(JsonHelpers.toString(principal));
		return "test06";
	}

	@GetMapping("/test07")
	@PreAuthorize("(#oauth2AuthorizedClient.authorizationGrantType.value() =='client_credentials' "
			+ "or #oauth2AuthorizedClient.scope.contains('message.read')) "
			+ "and #oauth2AuthorizedClient.principal.authorities.contains('SCOPE_message.read')")
	public String test07(OAuth2AuthorizedClient oauth2AuthorizedClient) {
		System.out.println(JsonHelpers.toString(oauth2AuthorizedClient));
		return "test07";
	}

	@GetMapping("test08")
	@PreAuthorize("hasPermission(#root,1)")
	public String test08() {
		return "test08";
	}
}