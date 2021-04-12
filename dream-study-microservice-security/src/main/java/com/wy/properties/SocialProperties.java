package com.wy.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
@Configuration
@ConfigurationProperties(prefix = "user.social")
public class SocialProperties {

	private QQProperties qq = new QQProperties();
	
	private WeixinProperties wx = new WeixinProperties();
}