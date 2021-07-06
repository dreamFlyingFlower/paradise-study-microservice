package com.wy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wy.mapper.UserMapper;
import com.wy.service.UserService;

/**
 * 用户业务实现类
 * 
 * @auther 飞花梦影
 * @date 2021-07-05 20:03:53
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	/**
	 * 实现UserDetailsService中的loadUserByUsername方法,用于加载用户数据
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userMapper.selectByUsername(username);
	}
}