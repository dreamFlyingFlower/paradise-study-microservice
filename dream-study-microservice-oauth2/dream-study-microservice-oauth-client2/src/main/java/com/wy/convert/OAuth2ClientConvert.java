package com.wy.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.wy.entity.OAuth2ClientEntity;
import com.wy.vo.OAuth2ClientVO;

import dream.flying.flower.framework.web.convert.BaseConvert;

/**
 * 注册到其他认证服务器的信息
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OAuth2ClientConvert extends BaseConvert<OAuth2ClientEntity, OAuth2ClientVO> {

	OAuth2ClientConvert INSTANCE = Mappers.getMapper(OAuth2ClientConvert.class);
}