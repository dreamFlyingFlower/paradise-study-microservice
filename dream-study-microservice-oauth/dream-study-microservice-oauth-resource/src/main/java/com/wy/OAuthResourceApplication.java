package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OAuth2资源服务器,最好不要将认证服务器和资源服务器放一起,会有各种问题需要解决
 * 
 * @author 飞花梦影
 * @date 2021-04-09 11:04:34
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
public class OAuthResourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OAuthResourceApplication.class, args);
	}
}