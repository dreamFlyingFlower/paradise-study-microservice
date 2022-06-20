package com.wy.mapper;

import com.wy.base.BaseMapper;
import com.wy.model.Fileinfo;
import com.wy.model.FileinfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileinfoMapper extends BaseMapper<Fileinfo> {

	long countByExample(FileinfoExample example);

	int deleteByExample(FileinfoExample example);

	@Override
	int deleteByPrimaryKey(Integer fileinfoId);

	@Override
	int insert(Fileinfo record);

	@Override
	int insertSelective(Fileinfo record);

	List<Fileinfo> selectByExample(FileinfoExample example);

	@Override
	Fileinfo selectByPrimaryKey(Integer fileinfoId);

	int updateByExampleSelective(@Param("record") Fileinfo record, @Param("example") FileinfoExample example);

	int updateByExample(@Param("record") Fileinfo record, @Param("example") FileinfoExample example);

	@Override
	int updateByPrimaryKeySelective(Fileinfo record);

	@Override
	int updateByPrimaryKey(Fileinfo record);
}