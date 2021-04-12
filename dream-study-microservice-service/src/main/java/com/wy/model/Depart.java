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

@ApiModel("部门表")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Depart extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private Integer departId;

    private String departName;

    private Integer parentId;

    @ApiModelProperty("排序")
    private Integer sort;
}