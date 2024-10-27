package com.wy.service.impl;

import org.springframework.stereotype.Service;

import com.wy.convert.RoleMemberConvert;
import com.wy.entity.RoleMemberEntity;
import com.wy.mapper.RoleMemberMapper;
import com.wy.query.RoleMemberQuery;
import com.wy.service.RoleMemberService;
import com.wy.vo.RoleMemberVO;

import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;
import lombok.AllArgsConstructor;

/**
 * 角色成员
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Service
@AllArgsConstructor
public class RoleMemberServiceImpl extends
		AbstractServiceImpl<RoleMemberEntity, RoleMemberVO, RoleMemberQuery, RoleMemberConvert, RoleMemberMapper>
		implements RoleMemberService {
}