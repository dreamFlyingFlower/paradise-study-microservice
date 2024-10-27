package com.wy.service;

import com.wy.entity.RoleMemberEntity;
import com.wy.query.RoleMemberQuery;
import com.wy.vo.RoleMemberVO;

import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * 角色成员
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface RoleMemberService extends BaseServices<RoleMemberEntity, RoleMemberVO, RoleMemberQuery> {
}