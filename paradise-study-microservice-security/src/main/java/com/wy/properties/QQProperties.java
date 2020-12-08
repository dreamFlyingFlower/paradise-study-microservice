package com.wy.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @apiNote oauth2登录第三方的信息,此处可能需要继承一个
 *          org.springframework.boot.autoconfigure.social.SocialProperties 的类
 * @author ParadiseWY
 * @date 2019年9月25日
 */
@Getter
@Setter
public class QQProperties {

	private String providerId = "qq";

	private String appId;

	private String appSecret;
}