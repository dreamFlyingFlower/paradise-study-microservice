package com.wy.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 权限对象
 * 
 * @author 飞花梦影
 * @date 2021-01-20 23:22:51
 * @git {@link https://github.com/mygodness100}
 */
@ApiModel("权限对象")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PermissionVo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 角色编号
	 */
	@ApiModelProperty("角色编号")
	private Long roleId;

	/**
	 * 角色编码
	 */
	@ApiModelProperty("角色编码")
	private String roleCode;

	/**
	 * 菜单编号
	 */
	@ApiModelProperty("菜单编号")
	private Long menuId;

	/**
	 * 访问的url
	 */
	@ApiModelProperty("访问的url")
	private String url;

	/**
	 * 访问的方法
	 */
	@ApiModelProperty("访问的方法")
	private String method;

	/**
	 * 可访问的角色
	 */
	@ApiModelProperty("可访问的角色")
	private String roles;

	/**
	 * 权限字符串
	 */
	@ApiModelProperty("权限字符串")
	private String permissions;
}