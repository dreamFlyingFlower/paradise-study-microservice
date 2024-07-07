package com.wy.mapper;

import java.util.List;

import com.wy.entity.PermissionVo;

/**
 * 权限数据层
 * 
 * @author 飞花梦影
 * @date 2021-01-21 11:14:19
 * @git {@link https://github.com/mygodness100}
 */
public interface PermissionMapper {

	List<PermissionVo> selectPermissions();

	List<PermissionVo> selectPublicPermissions();
}