package com.wy.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * 简单模式参数.适用于手机调用第三方应用,access_token永不过期
 * 
 * @auther 飞花梦影
 * @date 2021-07-04 18:30:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
public class OAuth2ClientImplicitProperties extends OAuth2ClientProperties {

	private String grantType = "implicit";
}