package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;

/**
 * OAuth2.0:允许用户授权第三方应用访问用户存储在其他服务提供者上的信息,而不需要提供用户名和密码给第三方服务
 * eg:登录A网站,可以用QQ登录,此时QQ就是其他存储服务,A网站就是第三方.在QQ上登录后就授权A网站使用用户信息
 * 
 * OAuth2.0请求其他服务器资源中的角色:<br>
 * ->资源拥有者:通常为用户,也可以使应用程序<br>
 * ->第三方应用:本身不存储资源,需要通过资源拥有者去请求资源服务器的资源<br>
 * ->授权服务器:也叫认证服务器,用于对资源拥有者身份认证,访问资源授权等,认证成功后发放授权码(access_token)给第三方
 * ->资源服务器:存储拥有者资源的服务器,会给第三方应用一个客户端标识(client_id)和秘钥(client_secret),标识第三方的身份
 * 
 * OAuth2.0请求其他服务器资源中的流程:<br>
 * -> 用户在第三方客户端请求认证服务器服务授权 -> 证服服务同意给第三方授权 -> 第三方服务获得授权码<br>
 * -> 第三方服务利用授权码从认证服务器申请令牌 -> 第三方服务获得证服服务器的响应令牌<br>
 * -> 第三方服务携带令牌从资源服务器获得用户资源 -> 资源服务器返回用户信息
 * 
 * OAuth2.0授权码模式原始方式获得授权码,令牌流程:<br>
 * 
 * <pre>
 * ->{@link AuthorizationEndpoint#authorize},{@link AuthorizationEndpoint#approveOrDeny}:
 * -->固定请求地址/oauth/authorize,不可配置,get或post请求都可,请求参数如下:
 * --->response_type:认证模式,有4种,见官网,在授权码模式下固定为code
 * --->client_id:由认证服务器发给第三方的标识
 * --->redirect_uri:第三方验证通过之后取得授权码code的页面跳转地址,系统内置了http://example.com,也可以自定义,
 * 需要在配置文件中配置security.oauth2.client.registered-redirect-uri<br>
 * ---->scope:第三方权限,可自定义
 * 
 * -->第三方获得授权码code,再向认证服务器请求获得令牌(access_token),{@link TokenEndpoint}:
 * -->固定请求地址/oauth/token,不可配置,post请求,请求参数如下:
 * --->grant_type:在授权码模式下固定为authorization_code
 * --->client_id:由认证服务器发给第三方的标识
 * --->client_secret:由认证服务器发给第三方的秘钥
 * --->code:上一步中得到的授权码code,只能使用一次
 * --->redirect_uri:同/oauth/authorize中的参数
 * --->scope:同/oauth/authorize中的参数
 * 
 * -->认证服务器返回令牌(access_token)
 * </pre>
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