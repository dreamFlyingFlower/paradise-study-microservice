package com.wy.entity;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.wy.database.Pri;
import com.wy.database.Unique;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 角色
 * 
 * @auther 飞花梦影
 * @date 2021-06-29 00:05:13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Role implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 角色编号
	 */
	@ApiModelProperty("角色编号")
	@Pri
	private Long roleId;

	/**
	 * 角色名
	 */
	@ApiModelProperty("角色名")
	@NotBlank(message = "角色名称不能为空")
	@Size(min = 0, max = 30, message = "角色名称长度不能超过30个字符")
	private String roleName;

	/**
	 * 角色编码
	 */
	@ApiModelProperty("角色编码")
	@Unique
	private String roleCode;

	/**
	 * 角色类型:0不可见,只有超级管理员不可见;默认1可见
	 */
	@ApiModelProperty("角色类型:0不可见,只有超级管理员不可见;默认1可见")
	private Integer roleType;

	/**
	 * 角色状态:0停用;默认1正常;2逻辑删除
	 */
	@ApiModelProperty("角色状态:0停用;默认1正常;2逻辑删除")
	private Integer roleState;

	/**
	 * 角色描述
	 */
	@ApiModelProperty("角色描述")
	private String roleDesc;

	/** 非数据库字段 */
	/**
	 * 原角色编号
	 */
	@ApiModelProperty("原角色编号")
	private String oriRoleCode;
}