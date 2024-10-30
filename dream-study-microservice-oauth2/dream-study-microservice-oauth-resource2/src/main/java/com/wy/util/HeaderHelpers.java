package com.wy.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 请求头工具类
 *
 * @author 飞花梦影
 * @date 2021-07-02 17:37:44
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class HeaderHelpers {

	private final static String INTROSPECTION_ENDPOINT = "http://localhost:17127/oauth2/introspect";

	/**
	 * 向认证服务器发送请求
	 * 
	 * @param clientId
	 * @param clientSecret
	 * @param token
	 */
	public static void generateHeader(String clientId, String clientSecret, String token) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		// 将clientId和clientSecret用:拼起来并进行Base64编码
		headers.setBasicAuth(clientId, clientSecret);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("token", token);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
		ResponseEntity<String> response =
				restTemplate.exchange(INTROSPECTION_ENDPOINT, HttpMethod.POST, request, String.class);

		System.out.println(response.getBody());
	}
}