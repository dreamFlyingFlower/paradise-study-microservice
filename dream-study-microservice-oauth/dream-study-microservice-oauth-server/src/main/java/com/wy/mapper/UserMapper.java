package com.wy.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.wy.entity.UserEntity;
import com.wy.query.UserQuery;
import com.wy.vo.UserVO;

import dream.flying.flower.framework.mybatis.plus.mapper.BaseMappers;

/**
 * 用户信息
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper
public interface UserMapper extends BaseMappers<UserEntity, UserVO, UserQuery> {

	UserVO getUserByPhone(String phone);

	UserVO getUserByUserName(String username);
}