package com.wy.mapper;

import com.wy.base.BaseMapper;
import com.wy.model.Role;
import com.wy.model.RoleExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

	long countByExample(RoleExample example);

	int deleteByExample(RoleExample example);

	@Override
	int deleteByPrimaryKey(Integer roleId);

	@Override
	int insert(Role record);

	@Override
	int insertSelective(Role record);

	List<Role> selectByExample(RoleExample example);

	@Override
	Role selectByPrimaryKey(Integer roleId);

	int updateByExampleSelective(@Param("record") Role record, @Param("example") RoleExample example);

	int updateByExample(@Param("record") Role record, @Param("example") RoleExample example);

	@Override
	int updateByPrimaryKeySelective(Role record);

	@Override
	int updateByPrimaryKey(Role record);
}