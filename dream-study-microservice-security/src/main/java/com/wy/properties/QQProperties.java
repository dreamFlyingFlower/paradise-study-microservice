package com.wy.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * QQ应用信息,此处可能需要继承#org.springframework.boot.autoconfigure.social.SocialProperties,需要降低Spring版本
 * 
 * @auther 飞花梦影
 * @date 2019-09-25 01:04:59
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
public class QQProperties {

	private String providerId = "qq";

	private String appId;

	private String appSecret;
}