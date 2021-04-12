package com.wy.entity;

import org.springframework.social.UserIdSource;

import lombok.Getter;
import lombok.Setter;

/**
 * @apiNote social需要作为标识的用户编号
 * @author ParadiseWY
 * @date 2019年9月29日
 */
@Getter
@Setter
public class SocialUser implements UserIdSource {

	private String userId;

	@Override
	public String getUserId() {
		return userId;
	}
}