package com.wy.mapper;

import com.wy.base.BaseMapper;
import com.wy.model.User;
import com.wy.model.UserExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

	long countByExample(UserExample example);

	int deleteByExample(UserExample example);

	@Override
	int deleteByPrimaryKey(Integer userId);

	@Override
	int insert(User record);

	@Override
	int insertSelective(User record);

	List<User> selectByExample(UserExample example);

	@Override
	User selectByPrimaryKey(Integer userId);

	int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

	int updateByExample(@Param("record") User record, @Param("example") UserExample example);

	@Override
	int updateByPrimaryKeySelective(User record);

	@Override
	int updateByPrimaryKey(User record);
}