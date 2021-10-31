package com.wy.service.impl;

import com.wy.service.UserService;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.spring.annotation.GlobalTransactional;

/**
 * Seata的XA事务
 * 
 * {@link GlobalTransactional}:在类上使用该注解即可使用XA类型事务
 * 
 * @author 飞花梦影
 * @date 2021-10-30 16:22:41
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class UserServiceImpl implements UserService {

	@Override
	public String testUser() {
		return null;
	}

	@Override
	public boolean testCommit(BusinessActionContext businessActionContext) {
		return false;
	}

	@Override
	public boolean testCancel(BusinessActionContext businessActionContext) {
		return false;
	}

	@GlobalTransactional
	public void testXa() {

	}
}