package com.wy.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wy.base.BaseMapper;
import com.wy.model.Menu;
import com.wy.model.MenuExample;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

	long countByExample(MenuExample example);

	int deleteByExample(MenuExample example);

	@Override
	int deleteByPrimaryKey(Integer menuId);

	@Override
	int insert(Menu record);

	@Override
	int insertSelective(Menu record);

	List<Menu> selectByExample(MenuExample example);

	@Override
	Menu selectByPrimaryKey(Integer menuId);

	int updateByExampleSelective(@Param("record") Menu record, @Param("example") MenuExample example);

	int updateByExample(@Param("record") Menu record, @Param("example") MenuExample example);

	@Override
	int updateByPrimaryKeySelective(Menu record);

	@Override
	int updateByPrimaryKey(Menu record);
}