package com.wy.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wy.convert.RolePrivilegeConvert;
import com.wy.entity.RolePrivilegeEntity;
import com.wy.mapper.RolePrivilegeMapper;
import com.wy.query.RolePrivilegeQuery;
import com.wy.service.RolePrivilegeService;
import com.wy.vo.RolePrivilegeVO;

import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;

/**
 * 角色权限
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Service
public class RolePrivilegeServiceImpl extends AbstractServiceImpl<RolePrivilegeEntity, RolePrivilegeVO,
		RolePrivilegeQuery, RolePrivilegeConvert, RolePrivilegeMapper> implements RolePrivilegeService {

	@Override
	public boolean insertRolePrivileges(List<RolePrivilegeEntity> rolePermissionsList) {
		return baseMapper.insertRolePrivileges(rolePermissionsList) > 0;
	}

	@Override
	public boolean deleteRolePrivileges(List<RolePrivilegeEntity> rolePermissionsList) {
		return baseMapper.deleteRolePrivileges(rolePermissionsList) >= 0;
	}

	@Override
	public List<RolePrivilegeEntity> queryRolePrivileges(RolePrivilegeEntity rolePermissions) {
		return baseMapper.queryRolePrivileges(rolePermissions);
	}
}