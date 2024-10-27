package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OAuth2:允许用户授权第三方应用访问用户存储在其他服务提供者上的信息,而不需要提供用户名和密码给第三方应用
 * eg:登录A网站,可以用QQ登录,此时QQ就是服务提供者,A网站就是第三方应用.在QQ上登录后就授权A网站使用用户信息
 * 
 * 搭建第三方应用登录的方式:SpringSocial和SpringOAuth都已经过时,现在直接使用SpringSecurity即可.
 * 
 * SpringSocial已经不维护了,只能直接使用SpringSecurity进行Social相关操作,详见
 * 
 * @see <a href=
 *      "https://spring.io/blog/2018/03/06/using-spring-security-5-to-integrate-with-oauth-2-secured-services-such-as-facebook-and-github"
 *      ></a>
 * 
 * @author 飞花梦影
 * @date 2021-04-09 11:04:34
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Deprecated
@SpringBootApplication
public class OAuthClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(OAuthClientApplication.class, args);
	}
}