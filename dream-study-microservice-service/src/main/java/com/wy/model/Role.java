package com.wy.model;

import com.wy.base.AbstractModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private Integer roleId;

    private String roleName;

    @ApiModelProperty("角色状态0不可见,只有超级管理员不可见,1可见")
    private Byte roleState;
}