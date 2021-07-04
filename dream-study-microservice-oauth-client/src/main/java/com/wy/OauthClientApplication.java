package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuth2:允许用户授权第三方应用访问用户存储在其他服务提供者上的信息,而不需要提供用户名和密码给第三方应用
 * eg:登录A网站,可以用QQ登录,此时QQ就是服务提供者,A网站就是第三方应用.在QQ上登录后就授权A网站使用用户信息
 * 
 * OAuth2请求其他服务器资源中的角色:
 * 
 * <pre>
 * ->资源拥有者:通常为用户,也可以使应用程序
 * ->第三方应用:本身不存储资源,需要通过资源拥有者去请求资源服务器的资源
 * ->授权服务器:也叫认证服务器,用于对资源拥有者身份认证,访问资源授权等,认证成功后发放授权码(access_token)给第三方
 * ->资源服务器:存储拥有者资源的服务器,会给第三方应用一个客户端标识(client_id)和秘钥(client_secret),标识第三方的身份
 * </pre>
 * 
 * OAuth2主要拦截器:
 * 
 * <pre>
 * ->{@link SecurityContextPersistenceFilter}
 * ->{@link AbstractAuthenticationProcessingFilter}
 * ->{@link OAuth2ClientContextFilter}
 * ->{@link OAuth2RestTemplate}
 * ->{@link SecurityContextPersistenceFilter}
 * </pre>
 * 
 * OAuth2有4种授权模式:授权码模式,用户名密码模式,简单模式,客户端模式,详情见notes/Security/OAuth2.0官方文档.pdf
 * 
 * OAuth2授权码模式请求服务提供商资源流程:
 * 
 * <pre>
 * -> 用户在第三方客户端请求认证服务器服务授权
 * -> 证服服务同意给第三方授权
 * -> 第三方服务获得授权码
 * -> 第三方服务利用授权码从认证服务器申请令牌
 * -> 第三方服务获得证服服务器的响应令牌
 * -> 第三方服务携带令牌从资源服务器获得用户资源
 * -> 资源服务器返回用户信息
 * </pre>
 * 
 * OAuth2.0授权码模式认证服务器原始方式发放授权码,令牌流程:
 * 
 * <pre>
 * ->{@link AuthorizationEndpoint#authorize}:固定请求/oauth/authorize,客户端获取授权码(code),此时会让用户授权登录.
 * 		URL不可配置,但可以使用{@link RestController}将请求地址重写,get或post请求都可,请求参数如下:
 * -->response_type:认证模式,有4种,见官网,在授权码模式下固定为code
 * -->client_id:由认证服务器发给第三方的标识,唯一
 * -->client_secret:由认证服务器发放给第三方的密码
 * -->redirect_uri:用户授权通过,第三方验证通过之后取得授权码code的页面跳转地址,系统内置了http://example.com,也可以自定义,
 * 		需要在配置文件中配置security.oauth2.client.registered-redirect-uri,
 * 		不同版本不配置不一样,当前版本可直接设置在{@link AuthorizationCodeResourceDetails}
 * -->scope:认证服务器给第三方的权限,可自定义
 * 
 * ->{@link AuthorizationEndpoint#approveOrDeny}:用户登录之后会跳到授权页面,授权成功则返回code给第三方应用
 * 
 * ->{@link TokenEndpoint}:固定请求/oauth/token,第三方通过跳转地址获得code后,再向认证服务器请求获得令牌(access_token).
 * 		URL不可配置,但可以使用{@link RestController}将请求地址重写,post请求,请求参数如下:
 * -->grant_type:在授权码模式下固定为authorization_code,当上一个access_token过期需要请求新的时,该值为refresh_token
 * -->client_id:由认证服务器发给第三方的标识,唯一
 * -->client_secret:由认证服务器发放给第三方的密码
 * -->code:上一步中得到的授权码code,只能使用一次
 * -->redirect_uri:同/oauth/authorize中的参数
 * -->scope:同/oauth/authorize中的参数
 * -->refresh_token:当上一个access_token过期之后,可使用上一次的refresh_token请求该地址获得新的access_token
 * -->认证服务器返回令牌(access_token)
 * 
 * ->第三方从认证服务器获得令牌后,可用令牌请求资源服务器获得用户相关信息,令牌有时限,由认证服务器控制
 * </pre>
 * 
 * SpringSocial已经不维护了,只能直接使用SpringSecurity进行Social相关操作,详见
 * 
 * @see <a
 *      href="https://spring.io/blog/2018/03/06/using-spring-security-5-to-integrate-with-oauth-2-secured-services-such-as-facebook-and-github"
 * @author 飞花梦影
 * @date 2021-04-09 11:04:34
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
public class OauthClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(OauthClientApplication.class, args);
	}
}