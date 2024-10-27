package com.wy.service;

import com.wy.entity.RoleEntity;
import com.wy.query.RoleQuery;
import com.wy.vo.RoleVO;

import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * 角色
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface RoleService extends BaseServices<RoleEntity, RoleVO, RoleQuery> {
}