package com.wy.properties;

import lombok.Setter;

import lombok.Getter;

/**
 * 授权码验证参数.适用于非第三方应用用户授权第三方应用使用授权码请求服务提供商使用用户信息
 * 
 * @auther 飞花梦影
 * @date 2021-07-04 18:23:50
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
public class OAuth2ClientCodeProperties extends OAuth2ClientProperties {

	private String grantType = "authorization_code";

	private String preEstablishedRedirectUri;

	private String userAuthorizationUri;

	private boolean useCurrentUri = true;
}