package com.wy.model;

import com.wy.base.AbstractModel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel("字典表")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dict extends AbstractModel {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("字典编号")
    private Integer dictId;

    @ApiModelProperty("字典名")
    private String dictName;

    @ApiModelProperty("唯一标识符,不可重复")
    private String dictCode;

    @ApiModelProperty("字典值,可为null")
    private Integer dictVal;

    @ApiModelProperty("上级字典编号")
    private Integer pid;

    @ApiModelProperty("排序")
    private Integer sortIndex;
}