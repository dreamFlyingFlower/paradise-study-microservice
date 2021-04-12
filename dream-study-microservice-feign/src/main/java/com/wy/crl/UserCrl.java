package com.wy.crl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wy.service.UserService;

/**
 * 此处的接口供外部调用,可和内部调用的api接口url不同
 * @author paradiseWy
 */
@RestController
@RequestMapping("user")
public class UserCrl extends FeignCrl {

	@Autowired
	private UserService userService;

	@Override
	public UserService getService() {
		return userService;
	}
}