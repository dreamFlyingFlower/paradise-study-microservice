package com.wy.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户名密码模式参数.适用于非第三方应用用户授权第三用应用使用用户名和密码从服务提供商获取用户信息使用
 * 
 * @auther 飞花梦影
 * @date 2021-07-04 18:30:44
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
public class OAuth2ClientPasswordProperties extends OAuth2ClientProperties {

	private String grantType = "password";

	private String username;

	private String password;
}