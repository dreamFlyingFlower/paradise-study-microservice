package com.wy.crl;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wy.result.Result;

/**
 * 测试API
 *
 * @author 飞花梦影
 * @date 2021-07-01 13:25:20
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@RestController
@RequestMapping("/test")
public class TestCrl {

	@GetMapping("getAuthenticaiton")
	public Result<?> getAuthenticaiton(Authentication authentication) {
		return Result.ok(authentication);
	}
}