package com.wy.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.wy.entity.ThirdUserEntity;
import com.wy.vo.ThirdUserVO;

import dream.flying.flower.framework.web.convert.BaseConvert;

/**
 * 第三方认证服务用户表
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ThirdUserConvert extends BaseConvert<ThirdUserEntity, ThirdUserVO> {

	ThirdUserConvert INSTANCE = Mappers.getMapper(ThirdUserConvert.class);
}