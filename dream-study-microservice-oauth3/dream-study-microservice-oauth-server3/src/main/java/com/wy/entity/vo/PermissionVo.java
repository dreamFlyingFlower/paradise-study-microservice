package com.wy.entity.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "权限对象")
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
	@Schema(description = "角色编号")
	private Long roleId;

	/**
	 * 角色编码
	 */
	@Schema(description = "角色编码")
	private String roleCode;

	/**
	 * 菜单编号
	 */
	@Schema(description = "菜单编号")
	private Long menuId;

	/**
	 * 访问的url
	 */
	@Schema(description = "访问的url")
	private String url;

	/**
	 * 访问的方法
	 */
	@Schema(description = "访问的方法")
	private String method;

	/**
	 * 可访问的角色
	 */
	@Schema(description = "可访问的角色")
	private String roles;

	/**
	 * 权限字符串
	 */
	@Schema(description = "权限字符串")
	private String permissions;
}