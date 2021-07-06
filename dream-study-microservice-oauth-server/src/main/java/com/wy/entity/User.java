package com.wy.entity;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.wy.entity.vo.PermissionVo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 配合security登录,并且使用数据库的数据,必须实现UserDetails接口
 * 
 * @auther 飞花梦影
 * @date 2021-06-29 00:08:06
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@ApiModel(description = "用户表 ts_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Integer userId;

	private String username;

	private String password;

	private String realname;

	private Integer departId;

	private String idcard;

	private Date birthday;

	private Integer age;

	private Character sex;

	private String address;

	private String email;

	private String tel;

	private String salary;

	private Integer state;

	private String userIcon;

	private Date createtime;

	private Date updatetime;

	private String role;

	private List<Role> roles;

	private List<PermissionVo> permissions;

	/**
	 * 设置角色权限,可以设置多个,用逗号隔开.角色前需要加上ROLE_,权限可以不加
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// 此处测试使用,真实需要通过role进行相关设置
		return AuthorityUtils.commaSeparatedStringToAuthorityList("USER");
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	/**
	 * 账户是否过期
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * 账户是否锁定
	 */
	@Override
	public boolean isAccountNonLocked() {
		return state == 2;
	}

	/**
	 * 证书是否过期
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * 账号是否有效
	 */
	@Override
	public boolean isEnabled() {
		return state == 1;
	}
}