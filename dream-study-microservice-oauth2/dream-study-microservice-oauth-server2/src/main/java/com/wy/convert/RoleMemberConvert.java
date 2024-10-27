package com.wy.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.wy.entity.RoleMemberEntity;
import com.wy.vo.RoleMemberVO;

import dream.flying.flower.framework.web.convert.BaseConvert;

/**
 * 角色成员
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMemberConvert extends BaseConvert<RoleMemberEntity, RoleMemberVO> {

	RoleMemberConvert INSTANCE = Mappers.getMapper(RoleMemberConvert.class);
}