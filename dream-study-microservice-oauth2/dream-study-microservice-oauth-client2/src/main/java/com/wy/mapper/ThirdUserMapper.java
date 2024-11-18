package com.wy.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.wy.entity.ThirdUserEntity;
import com.wy.query.ThirdUserQuery;
import com.wy.vo.ThirdUserVO;

import dream.flying.flower.framework.mybatis.plus.mapper.BaseMappers;

/**
 * 第三方认证服务用户表
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper
public interface ThirdUserMapper extends BaseMappers<ThirdUserEntity, ThirdUserVO, ThirdUserQuery> {

}