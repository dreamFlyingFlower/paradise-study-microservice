package com.wy.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * OAuth2相关配置
 *
 * @author 飞花梦影
 * @date 2021-07-02 16:53:24
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Getter
@Setter
public class OAuth2Properties {

	/** 第三方游客登录本系统的clientId */
	private String clientIdGuest = "guest";

	/** 第三方游客登录本系统的clientSecret */
	private String clientSecretGuest = "guest";

	/** 第三方游客登录本系统的授权模式 */
	private String[] grantTypes = { "authorization_code", "refresh_token", "password" };

	/** 第三方游客登录本系统的访问权限 */
	private String[] scopes = { "guest", "openId" };

	/** token令牌存储方式 */
	private String storeType = "jwt";
}