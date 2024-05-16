package com.wy.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.wy.dto.AsyncRequestDTO;
import com.wy.entity.AsyncRequestEntity;

import dream.framework.web.convert.BaseConvert;

/**
 * 数据转换
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:53:39
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AsyncRequestConvert extends BaseConvert<AsyncRequestEntity, AsyncRequestDTO> {

}