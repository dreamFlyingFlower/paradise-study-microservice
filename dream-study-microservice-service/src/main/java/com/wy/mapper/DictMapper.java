package com.wy.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wy.base.BaseMapper;
import com.wy.model.Dict;
import com.wy.model.DictExample;

@Mapper
public interface DictMapper extends BaseMapper<Dict> {

	long countByExample(DictExample example);

	int deleteByExample(DictExample example);

	@Override
	int deleteByPrimaryKey(Integer dictId);

	@Override
	int insert(Dict record);

	@Override
	int insertSelective(Dict record);

	List<Dict> selectByExample(DictExample example);

	@Override
	Dict selectByPrimaryKey(Integer dictId);

	int updateByExampleSelective(@Param("record") Dict record, @Param("example") DictExample example);

	int updateByExample(@Param("record") Dict record, @Param("example") DictExample example);

	@Override
	int updateByPrimaryKeySelective(Dict record);

	@Override
	int updateByPrimaryKey(Dict record);
}