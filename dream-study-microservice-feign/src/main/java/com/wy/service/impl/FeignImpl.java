package com.wy.service.impl;

import java.util.List;

import com.wy.result.Result;
import com.wy.service.FeignService;

public class FeignImpl implements FeignService{

	@Override
	public Object create(Object entity) {
		return Result.error("新增数据失败,请重试!");
	}

	@Override
	public Object remove(String id) {
		return Result.error("删除数据失败,请重试!");
	}

	@Override
	public Object removes(List<String> ids) {
		return Result.error("批量删除数据失败,请重试!");
	}

	@Override
	public Object edit(Object entity) {
		return Result.error("修改数据失败,请重试!");
	}

	@Override
	public Object getById(String id) {
		return Result.error("获取数据详情失败,请重试!");
	}

	@Override
	public Object getList(Object page) {
		return Result.error("获取分页数据失败,请重试!");
	}
}