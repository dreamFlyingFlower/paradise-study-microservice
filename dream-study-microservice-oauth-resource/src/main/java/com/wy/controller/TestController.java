package com.wy.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping("test1")
	public Result<?> test1(Authentication authentication) {
		return Result.ok(authentication);
	}

}