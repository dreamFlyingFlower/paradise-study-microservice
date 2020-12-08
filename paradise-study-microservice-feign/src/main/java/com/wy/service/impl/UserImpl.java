package com.wy.service.impl;

import com.wy.result.Result;
import com.wy.service.UserService;

public class UserImpl extends FeignImpl implements UserService {

	@Override
	public Object checkUnique(String username) {
		return Result.error("该用户名已被占用,请重新输入!");
	}

	@Override
	public Object getByParams(String username, Integer age) {
		return Result.error("查无此人");
	}

}