package com.wy.mapper;

import com.wy.base.BaseMapper;
import com.wy.model.Button;
import com.wy.model.ButtonExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ButtonMapper extends BaseMapper<Button> {
    long countByExample(ButtonExample example);

    int deleteByExample(ButtonExample example);

    int deleteByPrimaryKey(Integer buttonId);

    int insert(Button record);

    int insertSelective(Button record);

    List<Button> selectByExample(ButtonExample example);

    Button selectByPrimaryKey(Integer buttonId);

    int updateByExampleSelective(@Param("record") Button record, @Param("example") ButtonExample example);

    int updateByExample(@Param("record") Button record, @Param("example") ButtonExample example);

    int updateByPrimaryKeySelective(Button record);

    int updateByPrimaryKey(Button record);
}