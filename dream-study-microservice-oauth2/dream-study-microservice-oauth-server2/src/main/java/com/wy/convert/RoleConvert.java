package com.wy.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.wy.entity.RoleEntity;
import com.wy.vo.RoleVO;

import dream.flying.flower.framework.web.convert.BaseConvert;

/**
 * 角色
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleConvert extends BaseConvert<RoleEntity, RoleVO> {

	RoleConvert INSTANCE = Mappers.getMapper(RoleConvert.class);
}