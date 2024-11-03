package com.wy.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;

import dream.flying.flower.framework.mybatis.plus.entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 注册到其他认证服务器的信息
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("oauth2_client")
public class OAuth2ClientEntity extends AbstractEntity {

	private static final long serialVersionUID = -3601222345418102072L;

	/**
	 * 用户表标识
	 */
	private Long userId;

	/**
	 * 三方登录唯一标识
	 */
	private String uniqueId;

	/**
	 * 三方用户的账号
	 */
	private String clientName;

	/**
	 * 三方登录获取的认证信息(token)
	 */
	private String credentials;

	/**
	 * 三方登录获取的认证信息过期时间
	 */
	private Date credentialsExpiresAt;

	/**
	 * 认证方法
	 */
	private String clientAuthenticationMethods;

	/**
	 * 认证模式
	 */
	private String authorizationGrantTypes;

	/**
	 * 博客
	 */
	private String blog;

	/**
	 * 地址
	 */
	private String location;
}