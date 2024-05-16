package com.wy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dream.result.Result;
import com.wy.feign.FeignService;

public class FeignServiceImpl {

	@Autowired
	private FeignService feignService;

	public Object create(Object entity) {
		return Result.ok(feignService.create(entity));
	}

	public Object remove(String id) {
		return Result.ok(feignService.remove(id));
	}

	public Object removes(List<String> ids) {
		return Result.ok(feignService.removes(ids));
	}

	public Object edit(Object entity) {
		return Result.ok(feignService.edit(entity));
	}

	public Object getById(String id) {
		return Result.ok(feignService.getById(id));
	}

	public Object getList(Object page) {
		return Result.ok(feignService.getList(page));
	}
}