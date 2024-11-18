package com.wy.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.wy.entity.UserEntity;
import com.wy.query.UserQuery;
import com.wy.vo.ThirdUserVO;
import com.wy.vo.OAuth2UserinfoVO;
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

	/**
	 * 生成用户信息
	 *
	 * @param thirdAccount 三方用户信息
	 * @return 用户id
	 */
	Long saveByThirdAccount(ThirdUserVO thirdAccount);

	/**
	 * 获取当前登录用户的信息
	 *
	 * @return 用户信息
	 */
	OAuth2UserinfoVO getLoginUserInfo();
}