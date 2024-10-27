package com.wy.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wy.convert.ResourceConvert;
import com.wy.entity.ResourceEntity;
import com.wy.mapper.ResourceMapper;
import com.wy.query.ResourceQuery;
import com.wy.service.ResourceService;
import com.wy.vo.ResourceVO;

import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;

/**
 * 资源
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Service
public class ResourceServiceImpl
		extends AbstractServiceImpl<ResourceEntity, ResourceVO, ResourceQuery, ResourceConvert, ResourceMapper>
		implements ResourceService {

	@Override
	public List<ResourceEntity> queryResourcesTree(ResourceEntity resource) {
		return baseConvert.convert(list(resource));
	}
}