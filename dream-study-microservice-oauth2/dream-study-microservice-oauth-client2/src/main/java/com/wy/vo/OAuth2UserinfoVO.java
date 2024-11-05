package com.wy.vo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * oauth2登录后获取用户信息响应类
 *
 * @author 飞花梦影
 * @date 2024-11-03 23:16:40
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2UserinfoVO implements Serializable {

	private static final long serialVersionUID = 1949170895884754326L;

	/**
	 * 自增id
	 */
	private Long id;

	/**
	 * 用户账号
	 */
	private String sub;

	/**
	 * 用户名、昵称
	 */
	private String name;

	/**
	 * 账号
	 */
	private String account;

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
	 * 用户来源
	 */
	private String sourceFrom;

	/**
	 * 权限信息
	 */
	private Collection<SimpleGrantedAuthority> authorities;

	/**
	 * 地址
	 */
	private String location;

	/**
	 * 三方登录用户名
	 */
	private String clientName;

	/**
	 * 三方登录获取的认证信息
	 */
	private String credentials;

	/**
	 * 三方登录获取的认证信息的过期时间
	 */
	private Date credentialsExpiresAt;
}