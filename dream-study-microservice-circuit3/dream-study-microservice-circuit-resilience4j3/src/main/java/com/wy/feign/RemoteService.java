package com.wy.feign;

import java.util.List;

public interface RemoteService {

	Object create(Object entity);

	Object remove(String id);

	Object removes(List<String> ids);

	Object edit(Object entity);

	Object getById(String id);

	Object getList(Object page);
}