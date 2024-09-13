package com.wy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dream.flying.flower.result.Result;

/**
 * 测试API
 *
 * @author 飞花梦影
 * @date 2021-07-01 13:25:20
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@RestController
@RequestMapping("test")
public class TestController {

	@GetMapping("getAuthenticaiton")
	public Result<?> getAuthenticaiton(Authentication authentication) {
		return Result.ok(authentication);
	}

	@Autowired
	private ObjectMapper objectMapper;

	@GetMapping(value = "messages",
			consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	// @GetMapping("messages")
	public String getMessages() {
		String[] messages = new String[] { "Message 1", "Message 2", "Message 3" };
		try {
			return objectMapper.writeValueAsString(messages);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}