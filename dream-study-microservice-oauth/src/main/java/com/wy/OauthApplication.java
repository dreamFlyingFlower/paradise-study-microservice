package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;

/**
 * OAuth2.0:允许用户授权第三方应用访问用户存储在其他服务提供者上的信息,而不需要提供用户名和密码给第三方服务
 * eg:登录A网站,可以用QQ登录,此时QQ就是其他存储服务,A网站就是第三方.在QQ上登录后就授权A网站使用用户信息
 * 
 * OAuth2.0请求其他服务器资源流程:包括用户,第三方服务,其他服务的认证服务器,其他服务的资源服务器<br>
 * -> 用户在第三方客户端请求其他服务授权 -> 其他服务同意给第三方授权 -> 第三方服务获得授权码<br>
 * -> 第三方服务利用授权码从其他服务的认证服务器申请令牌 -> 第三方服务获得其他服务的响应令牌<br>
 * -> 第三方服务携带令牌从其他服务的资源服务器获得用户资源 -> 其他服务返回用户信息
 * 
 * 获得需要登录的第三方的用户信息,登录拦截social的流程:<br>
 * {@link SocialAuthenticationFilter}
 * ->{@link SocialAuthenticationService(OAuth2AuthenticationService)} ->{@link ConnectionFactory}
 * ->{@link Authentication(SocialAuthenticationToken)}
 * ->{@link AuthenticationManager(ProviderManager)}
 * ->{@link AuthenticationProvider(SocialAuthenticationProvider)}
 * ->{@link UsersConnectionRepository(JdbcUsersConnectionRepository)}
 * ->{@link SocialUserDetailsService}->{@link SocialUserDetails}->{@link Authentication(SocialAuthenticationToken)}
 * 
 * @author 飞花梦影
 * @date 2021-04-09 11:04:34
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
public class OauthApplication {

	public static void main(String[] args) {
		SpringApplication.run(OauthApplication.class, args);
	}
}