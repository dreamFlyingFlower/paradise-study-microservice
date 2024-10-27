package com.wy.service;

import java.util.List;

import com.wy.entity.ResourceEntity;
import com.wy.query.ResourceQuery;
import com.wy.vo.ResourceVO;

import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * 资源
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface ResourceService extends BaseServices<ResourceEntity, ResourceVO, ResourceQuery> {

	List<ResourceEntity> queryResourcesTree(ResourceEntity resource);
}