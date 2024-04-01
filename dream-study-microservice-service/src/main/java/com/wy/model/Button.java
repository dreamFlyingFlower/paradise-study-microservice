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

@ApiModel("菜单按键表")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Button extends AbstractModel {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("按钮编号")
    private Integer buttonId;

    @ApiModelProperty("按钮名称")
    private String buttonName;

    @ApiModelProperty("菜单编号")
    private Integer menuId;
}