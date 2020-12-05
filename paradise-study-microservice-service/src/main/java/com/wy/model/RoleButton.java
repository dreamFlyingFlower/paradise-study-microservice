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

@ApiModel("角色按钮中间表")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleButton extends AbstractModel {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("角色编号")
    private Integer roleId;

    @ApiModelProperty("按钮编号")
    private Integer buttonId;
}