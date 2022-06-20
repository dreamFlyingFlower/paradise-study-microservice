package com.wy.mapper;

import com.wy.base.BaseMapper;
import com.wy.model.Depart;
import com.wy.model.DepartExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DepartMapper extends BaseMapper<Depart> {

	long countByExample(DepartExample example);

	int deleteByExample(DepartExample example);

	@Override
	int deleteByPrimaryKey(Integer departId);

	@Override
	int insert(Depart record);

	@Override
	int insertSelective(Depart record);

	List<Depart> selectByExample(DepartExample example);

	@Override
	Depart selectByPrimaryKey(Integer departId);

	int updateByExampleSelective(@Param("record") Depart record, @Param("example") DepartExample example);

	int updateByExample(@Param("record") Depart record, @Param("example") DepartExample example);

	@Override
	int updateByPrimaryKeySelective(Depart record);

	@Override
	int updateByPrimaryKey(Depart record);
}