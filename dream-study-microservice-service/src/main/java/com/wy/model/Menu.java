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

@ApiModel("菜单表,根菜单必须手动添加,不可通过程序添加")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu extends AbstractModel {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("菜单编号")
    private Integer menuId;

    @ApiModelProperty("菜单名")
    private String menuName;

    @ApiModelProperty("上级菜单编号")
    private Integer pid;

    @ApiModelProperty("菜单地址")
    private String menuUrl;

    @ApiModelProperty("菜单图标,必填,默认star.svg")
    private String menuIcon;

    @ApiModelProperty("菜单是否展示:默认1展示,0隐藏")
    private Integer show;

    @ApiModelProperty("排序")
    private Integer sortIndex;
}