package com.wy.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.wy.entity.RoleEntity;
import com.wy.entity.RoleMemberEntity;
import com.wy.query.RoleMemberQuery;
import com.wy.vo.RoleMemberVO;
import com.wy.vo.UserVO;

import dream.flying.flower.framework.mybatis.plus.mapper.BaseMappers;

/**
 * 角色成员
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper
public interface RoleMemberMapper extends BaseMappers<RoleMemberEntity, RoleMemberVO, RoleMemberQuery> {

	List<UserVO> memberInRole(RoleMemberQuery query);

	int addDynamicRoleMember(RoleEntity dynamicRole);

	int deleteDynamicRoleMember(RoleEntity dynamicRole);
}