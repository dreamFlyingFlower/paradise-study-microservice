package com.wy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.wy.feign.FeignUserService;

import dream.flying.flower.result.Result;

@Service
public class UserServiceImpl extends FeignServiceImpl implements FeignUserService {

	@Autowired
	private FeignUserService feignUserService;

	@Override
	public Object checkUnique(String username) {
		return Result.ok(feignUserService.checkUnique(username));
	}

	@Override
	public Object getByParams(String username, Integer age) {
		return Result.ok("查无此人");
	}

	@Override
	public Object getByParams(Object object) {
		return null;
	}

	@Override
	public Object setHeader(String token, MultiValueMap<String, String> headers) {
		return null;
	}
}