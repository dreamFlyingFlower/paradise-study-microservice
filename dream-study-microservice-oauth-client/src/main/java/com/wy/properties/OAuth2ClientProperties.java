package com.wy.properties;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * OAuth通用参数
 * 
 * @auther 飞花梦影
 * @date 2021-07-04 18:22:15
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
public class OAuth2ClientProperties {

	private String id;

	private String grantType;

	private String clientId;

	private String accessTokenUri;

	private List<String> scope;

	private String clientSecret;
}