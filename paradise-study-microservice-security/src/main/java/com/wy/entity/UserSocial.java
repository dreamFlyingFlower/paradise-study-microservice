package com.wy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @apiNote social成功登录第三方授权服务器之后返回的部分信息
 * @author ParadiseWY
 * @date 2019年9月26日
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSocial {

	// 授权服务提供商
	private String providerId;
	
	// openid
	private String providerUserId;
	
	// 昵称
	private String nickname;
	
	// 头像
	private String socialimage;
}