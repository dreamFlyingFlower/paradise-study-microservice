package com.wy.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

/**
 * 基于redis的授权确认repository
 *
 * @author 飞花梦影
 * @date 2024-11-07 09:33:55
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface RedisAuthorizationConsentRepository extends CrudRepository<RedisAuthorizationConsent, String> {

	/**
	 * 根据客户端id和授权确认用户的 username 查询授权确认信息
	 *
	 * @param registeredClientId 客户端id
	 * @param principalName 授权确认用户的 username
	 * @return 授权确认记录
	 */
	Optional<RedisAuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId,
			String principalName);
}