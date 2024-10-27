package com.wy.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.wy.entity.UserEntity;
import com.wy.query.UserQuery;
import com.wy.vo.UserVO;

import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * 用户信息
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface UserService extends BaseServices<UserEntity, UserVO, UserQuery>, UserDetailsService {

}