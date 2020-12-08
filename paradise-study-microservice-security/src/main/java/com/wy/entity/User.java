package com.wy.entity;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

/**
 * 配合security登录,并且使用数据库的数据,必须实现UserDetails接口
 * @author paradiseWy
 */
@Getter
@Setter
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
	private String role = "ROLE_USER";

	private boolean accountNonExpired;// 帐号失效,超时
	private boolean accountNonLocked;// 帐号是否锁定
	private boolean credentialsNonExpired;
	private boolean enabled;

	@Override
	public String toString() {
		return "User [userId=" + userId + ", username=" + username + ", password=" + password
				+ ", realname=" + realname + ", departId=" + departId + ", idcard=" + idcard
				+ ", birthday=" + birthday + ", age=" + age + ", sex=" + sex + ", address="
				+ address + ", email=" + email + ", tel=" + tel + ", salary=" + salary + ", state="
				+ state + ", userIcon=" + userIcon + ", createtime=" + createtime + ", updatetime="
				+ updatetime + ", role=" + role + ", accountNonExpired=" + accountNonExpired
				+ ", accountNonLocked=" + accountNonLocked + ", credentialsNonExpired="
				+ credentialsNonExpired + ", enabled=" + enabled + "]";
	}

	// 这是权限
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.commaSeparatedStringToAuthorityList(role);
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}