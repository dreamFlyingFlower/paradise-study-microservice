package com.wy.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * oauth2登录第三方的信息,此处可能需要继承一个
 * 
 * @author 飞花梦影
 * @date 2019-09-25 23:38:24
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
public class SocialProperties {

	private QQProperties qq = new QQProperties();

	private WeixinProperties wx = new WeixinProperties();
}