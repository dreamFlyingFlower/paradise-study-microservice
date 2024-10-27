package com.wy.crl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wy.feign.FeignUserService;

/**
 * 此处的接口供外部调用,可和内部调用的api接口url不同
 * 
 * @author 飞花梦影
 * @date 2021-09-21 16:34:14
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RestController
@RequestMapping("user")
public class UserCrl extends FeignCrl {

	@Autowired
	private FeignUserService feignUserService;

	@Override
	public FeignUserService getService() {
		return feignUserService;
	}
}