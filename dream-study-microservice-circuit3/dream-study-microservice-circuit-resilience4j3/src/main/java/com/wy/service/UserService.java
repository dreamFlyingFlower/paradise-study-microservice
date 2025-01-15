package com.wy.service;

import java.util.List;

/**
 * 调用远程用户接口
 * 
 * @author 飞花梦影
 * @date 2019-08-21 21:47:45
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface UserService {

	Object create(Object entity);

	Object remove(String id);

	Object removes(List<String> ids);

	Object edit(Object entity);

	Object getById(String id);

	Object getList(Object page);

	Object checkUnique(String username);

	Object getByParams(String username, Integer age);
}