package com.wy.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.wy.entity.ResourceEntity;
import com.wy.query.ResourceQuery;
import com.wy.vo.ResourceVO;

import dream.flying.flower.framework.mybatis.plus.mapper.BaseMappers;

/**
 * 资源
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper
public interface ResourceMapper extends BaseMappers<ResourceEntity, ResourceVO, ResourceQuery> {

}