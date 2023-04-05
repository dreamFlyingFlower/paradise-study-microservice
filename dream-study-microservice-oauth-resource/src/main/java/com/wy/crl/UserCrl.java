package com.wy.crl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.wy.result.Result;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2023-04-05 22:27:26
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@RestController
@RequestMapping("user")
public class UserCrl {

	@Autowired
	private JWKSet jwkSet;

	@Autowired
	private ObjectMapper objectMapper;

	@GetMapping("getAuthenticaiton")
	public Result<?> getAuthenticaiton(Authentication authentication) {
		return Result.ok(authentication);
	}

	@GetMapping(value = "messages",
			consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public String getMessages() {
		String[] messages = new String[] { "Message 1", "Message 2", "Message 3" };
		try {
			return objectMapper.writeValueAsString(messages);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping(value = "token_key", produces = "application/json; charset=UTF-8")
	public String keys() {
		return this.jwkSet.toString();
	}
}