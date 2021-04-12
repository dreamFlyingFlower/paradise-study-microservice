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

    int deleteByPrimaryKey(Integer fileinfoId);

    int insert(Fileinfo record);

    int insertSelective(Fileinfo record);

    List<Fileinfo> selectByExample(FileinfoExample example);

    Fileinfo selectByPrimaryKey(Integer fileinfoId);

    int updateByExampleSelective(@Param("record") Fileinfo record, @Param("example") FileinfoExample example);

    int updateByExample(@Param("record") Fileinfo record, @Param("example") FileinfoExample example);

    int updateByPrimaryKeySelective(Fileinfo record);

    int updateByPrimaryKey(Fileinfo record);
}