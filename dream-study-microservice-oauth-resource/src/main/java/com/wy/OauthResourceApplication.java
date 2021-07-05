package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuth2认证服务器和资源服务器,最好不要将认证服务器和资源服务器放一起,会有各种问题需要解决
 * 
 * OAuth2.0授权码模式发放授权码,令牌流程:
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
 * 认证服务器和资源服务器,可以时同一个类:
 * 
 * <pre>
 * {@link EnableAuthorizationServer}:在类上只需要添加该注解即可注册为认证服务器
 * {@link EnableResourceServer}:在类上只需要添加该注解即可注册为资源服务器
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2021-04-09 11:04:34
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
public class OauthResourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OauthResourceApplication.class, args);
	}
}