package com.wy.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wy.base.BaseMapper;
import com.wy.model.RoleButton;
import com.wy.model.RoleButtonExample;

@Mapper
public interface RoleButtonMapper extends BaseMapper<RoleButton> {

	long countByExample(RoleButtonExample example);

	int deleteByExample(RoleButtonExample example);

	int deleteByPrimaryKey(@Param("roleId") Integer roleId, @Param("buttonId") Integer buttonId);

	@Override
	int insert(RoleButton record);

	@Override
	int insertSelective(RoleButton record);

	List<RoleButton> selectByExample(RoleButtonExample example);

	int updateByExampleSelective(@Param("record") RoleButton record, @Param("example") RoleButtonExample example);

	int updateByExample(@Param("record") RoleButton record, @Param("example") RoleButtonExample example);
}