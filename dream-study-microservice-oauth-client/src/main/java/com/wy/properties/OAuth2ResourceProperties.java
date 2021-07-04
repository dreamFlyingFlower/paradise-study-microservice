package com.wy.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * 资源服务器配置
 * 
 * @auther 飞花梦影
 * @date 2021-07-04 20:19:51
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
public class OAuth2ResourceProperties {

	private String ownResourceHost = "http://127.0.0.1:55100/";
	
	private String urlMessageResource = ownResourceHost + "oauthServer/test/messages"; 
}