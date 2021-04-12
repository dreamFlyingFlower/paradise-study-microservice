package com.wy.service;

import java.util.List;

public interface FeignService {

	Object create(Object entity);

	Object remove(String id);

	Object removes(List<String> ids);

	Object edit(Object entity);

	Object getById(String id);

	Object getList(Object page);
}