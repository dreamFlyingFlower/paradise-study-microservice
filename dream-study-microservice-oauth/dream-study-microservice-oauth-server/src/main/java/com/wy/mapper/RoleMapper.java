package com.wy.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.wy.entity.RoleEntity;
import com.wy.query.RoleQuery;
import com.wy.vo.RoleVO;

import dream.flying.flower.framework.mybatis.plus.mapper.BaseMappers;

/**
 * 角色
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper
public interface RoleMapper extends BaseMappers<RoleEntity, RoleVO, RoleQuery> {

	List<RoleEntity> queryRolesByUserId(String userId);

}