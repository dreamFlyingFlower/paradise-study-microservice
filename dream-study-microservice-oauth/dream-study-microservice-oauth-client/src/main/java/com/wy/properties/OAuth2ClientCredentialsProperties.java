package com.wy.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * 客户端模式参数.适用于第三方应用直接和服务提供商直接调用
 * 
 * @auther 飞花梦影
 * @date 2021-07-04 18:30:34
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
public class OAuth2ClientCredentialsProperties extends OAuth2ClientProperties {

	private String grantType = "client_credentials";
}