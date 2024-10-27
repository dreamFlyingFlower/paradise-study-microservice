package com.wy.mapper;

import com.wy.entity.User;

/**
 * 用户数据层
 * 
 * @auther 飞花梦影
 * @date 2021-07-05 20:19:36
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface UserMapper {

	/**
	 * 根据用户民查询用户数据
	 * 
	 * @param username 用户名
	 * @return 用户
	 */
	User selectByUsername(String username);
}