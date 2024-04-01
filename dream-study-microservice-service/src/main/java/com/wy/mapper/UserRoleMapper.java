package com.wy.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wy.base.BaseMapper;
import com.wy.model.UserRole;
import com.wy.model.UserRoleExample;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

	long countByExample(UserRoleExample example);

	int deleteByExample(UserRoleExample example);

	int deleteByPrimaryKey(@Param("userId") Integer userId, @Param("roleId") Integer roleId);

	@Override
	int insert(UserRole record);

	@Override
	int insertSelective(UserRole record);

	List<UserRole> selectByExample(UserRoleExample example);

	int updateByExampleSelective(@Param("record") UserRole record, @Param("example") UserRoleExample example);

	int updateByExample(@Param("record") UserRole record, @Param("example") UserRoleExample example);
}