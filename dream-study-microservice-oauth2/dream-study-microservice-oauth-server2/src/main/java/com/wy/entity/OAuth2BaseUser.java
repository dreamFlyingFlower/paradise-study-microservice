package com.wy.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * OAuth2基础用户信息
 *
 * @author 飞花梦影
 * @date 2024-11-05 23:29:49
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@JsonSerialize
@TableName("oauth2_basic_user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuth2BaseUser implements UserDetails, Serializable {

	private static final long serialVersionUID = 4286563182446902399L;

	/**
	 * 自增id
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	/**
	 * 用户名、昵称
	 */
	private String name;

	/**
	 * 账号
	 */
	private String account;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 手机号
	 */
	private String mobile;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 头像地址
	 */
	private String avatarUrl;

	/**
	 * 是否已删除
	 */
	private Boolean deleted;

	/**
	 * 用户来源
	 */
	private String sourceFrom;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	/**
	 * 修改时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

	/**
	 * 权限信息 非数据库字段
	 */
	@TableField(exist = false)
	private Collection<SimpleGrantedAuthority> authorities;

	@Override
	public Collection<SimpleGrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getUsername() {
		return this.account;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return !this.deleted;
	}

}