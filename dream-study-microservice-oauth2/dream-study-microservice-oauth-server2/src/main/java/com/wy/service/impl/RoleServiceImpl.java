package com.wy.service.impl;

import org.springframework.stereotype.Service;

import com.wy.convert.RoleConvert;
import com.wy.entity.RoleEntity;
import com.wy.mapper.RoleMapper;
import com.wy.query.RoleQuery;
import com.wy.service.RoleService;
import com.wy.vo.RoleVO;

import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;
import lombok.AllArgsConstructor;

/**
 * 角色
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Service
@AllArgsConstructor
public class RoleServiceImpl extends AbstractServiceImpl<RoleEntity, RoleVO, RoleQuery, RoleConvert, RoleMapper>
		implements RoleService {
}