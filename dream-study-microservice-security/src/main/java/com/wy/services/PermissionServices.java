package com.wy.services;

import java.util.List;
import java.util.Objects;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.wy.collection.ListTool;
import com.wy.common.AuthException;
import com.wy.common.Constants;
import com.wy.entity.PermissionVo;
import com.wy.entity.Role;
import com.wy.entity.User;
import com.wy.enums.Permission;
import com.wy.enums.TipEnum;
import com.wy.lang.StrTool;
import com.wy.util.SecurityUtils;

/**
 * 利用{@link PreAuthorize}的SpringEl表达式自定义权限实现
 * 
 * 自定义表示构成:角色1,角色2:权限,中间用:隔开;多个角色用逗号分开,权限只能是一种;如ADMIN,MANAGER:DELETE
 * 
 * @author 飞花梦影
 * @date 2021-01-20 20:06:26
 * @git {@link https://github.com/mygodness100}
 */
@Component("permissionServices")
public class PermissionServices {

	/**
	 * 获得用户信息
	 * 
	 * @param param 参数,可以是角色或权限
	 * @return 用户信息
	 */
	private User getLoginUser(String... param) {
		if (StrTool.isBlank(param)) {
			return null;
		}
		User loginUser = SecurityUtils.getLoginUser();
		if (Objects.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getRoles())) {
			return null;
		}
		return loginUser;
	}

	/**
	 * 判断是否为超级管理员
	 * 
	 * @param roles 角色列表
	 * @return true or false
	 */
	private boolean assertAdmin(List<Role> roles) {
		if (ListTool.isEmpty(roles)) {
			throw new AuthException(TipEnum.TIP_USER_NOT_DISTRIBUTE_ROLE);
		}
		for (Role role : roles) {
			if (0 == role.getRoleType()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 验证用户是否具备个权限
	 * 
	 * @param permission 权限字符串
	 * @return 用户是否具备某权限
	 */
	public boolean hasAuthority(String permission) {
		return hasAnyAuthority(permission);
	}

	/**
	 * 验证用户是否不具备某权限,与hasAuthority相反
	 * 
	 * @param permission 权限字符串
	 * @return 用户是否不具备某权限
	 */
	public boolean hasNotAuthority(String permission) {
		return !hasAuthority(permission);
	}

	/**
	 * 验证用户是否具有以下任意一个权限
	 * 
	 * @param permissions 权限列表
	 * @return 用户是否具有以下任意一个权限
	 */
	public boolean hasAnyAuthority(String... permissions) {
		User loginUser = getLoginUser(permissions);
		if (Objects.isNull(loginUser)) {
			return false;
		}
		boolean admin = assertAdmin(loginUser.getRoles());
		if (admin) {
			return true;
		}
		List<PermissionVo> permissionVos = loginUser.getPermissions();
		if (ListTool.isEmpty(permissionVos)) {
			return false;
		}
		for (String permission : permissions) {
			if (hasPermissions(permissionVos, permission)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否包含权限
	 * 
	 * @param permissions 用户权限列表
	 * @param permission 权限字符串
	 * @return 用户是否具备某权限
	 */
	private boolean hasPermissions(List<PermissionVo> permissions, String permission) {
		String[] roleAndPermissions = permission.split(":");
		if (StrTool.isBlank(roleAndPermissions) || roleAndPermissions.length != 2) {
			return false;
		}
		if (StrTool.isBlank(roleAndPermissions[0]) || StrTool.isBlank(roleAndPermissions[1])) {
			return false;
		}
		// 权限数组
		String[] roleArray = roleAndPermissions[0].split(",");
		User loginUser = SecurityUtils.getLoginUser();
		if (loginUser.getRoles().get(0).getRoleCode().equalsIgnoreCase(Constants.SUPER_ADMIN)) {
			return true;
		}
		for (PermissionVo permissionVo : permissions) {
			for (String role : roleArray) {
				if (permissionVo.getRoleCode().equalsIgnoreCase(role) && (permissionVo.getPermissions().toLowerCase()
						.contains(Permission.ALL.name().toLowerCase())
						|| permissionVo.getPermissions().toLowerCase().contains(roleAndPermissions[1].toLowerCase()))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断用户是否拥有某个角色
	 * 
	 * @param roleCode 角色编码
	 * @return 用户是否具备某角色
	 */
	public boolean hasRole(String roleCode) {
		return hasAnyRole(roleCode);
	}

	/**
	 * 验证用户是否不具备某角色,与{@link #hasRole}逻辑相反
	 * 
	 * @param roleCode 角色编码
	 * @return 用户是否不具备某角色
	 */
	public boolean hasNotRole(String roleCode) {
		return !hasRole(roleCode);
	}

	/**
	 * 验证用户是否具有以下任意一个角色
	 * 
	 * @param roleCodes 角色列表
	 * @return 用户是否具有以下任意一个角色
	 */
	public boolean hasAnyRole(String... roleCodes) {
		User loginUser = getLoginUser(roleCodes);
		if (Objects.isNull(loginUser)) {
			return false;
		}
		boolean admin = assertAdmin(loginUser.getRoles());
		if (admin) {
			return true;
		}
		for (String roleCode : roleCodes) {
			for (Role sysRole : loginUser.getRoles()) {
				if (roleCode.equalsIgnoreCase(sysRole.getRoleCode())) {
					return true;
				}
			}
		}
		return false;
	}
}