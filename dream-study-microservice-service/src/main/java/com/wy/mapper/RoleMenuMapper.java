package com.wy.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wy.base.BaseMapper;
import com.wy.model.RoleMenu;
import com.wy.model.RoleMenuExample;

@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

	long countByExample(RoleMenuExample example);

	int deleteByExample(RoleMenuExample example);

	@Override
	int insert(RoleMenu record);

	@Override
	int insertSelective(RoleMenu record);

	List<RoleMenu> selectByExample(RoleMenuExample example);

	int updateByExampleSelective(@Param("record") RoleMenu record, @Param("example") RoleMenuExample example);

	int updateByExample(@Param("record") RoleMenu record, @Param("example") RoleMenuExample example);
}