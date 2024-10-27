package com.wy.crl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试API
 *
 * @author 飞花梦影
 * @date 2021-12-31 10:33:54
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@RestController
@RequestMapping("test")
@RefreshScope
public class TestCrl {

	@Value("${test.test}")
	private String testValue;
	
	@Value("${test.test1}")
	private String testValue2;
	
	@Value("${test.test2}")
	String testValue3;

	@GetMapping("test")
	public void test() {
		System.out.println(testValue);
		System.out.println(testValue2);
		System.out.println(testValue3);
	}
}