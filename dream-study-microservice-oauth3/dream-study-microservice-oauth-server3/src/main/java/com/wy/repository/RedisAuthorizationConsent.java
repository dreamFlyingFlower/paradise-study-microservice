package com.wy.repository;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.Data;

/**
 * 基于redis的授权确认存储实体
 * 
 * @author 飞花梦影
 * @date 2024-11-07 10:15:20
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@RedisHash(value = "authorizationConsent")
public class RedisAuthorizationConsent implements Serializable {

	private static final long serialVersionUID = 3361457844960247740L;

	/**
	 * 额外提供的主键
	 */
	@Id
	private String id;

	/**
	 * 当前授权确认的客户端id
	 */
	@Indexed
	private String registeredClientId;

	/**
	 * 当前授权确认用户的 username
	 */
	@Indexed
	private String principalName;

	/**
	 * 授权确认的scope
	 */
	private String authorities;
}