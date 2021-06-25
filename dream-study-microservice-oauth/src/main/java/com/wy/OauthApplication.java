package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.social.ApiBinding;
import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.provider.SocialAuthenticationService;

/**
 * OAuth2.0:允许用户授权第三方应用访问用户存储在其他服务提供者上的信息,而不需要提供用户名和密码给第三方服务
 * eg:登录A网站,可以用QQ登录,此时QQ就是其他存储服务,A网站就是第三方.在QQ上登录后就授权A网站使用用户信息
 * 
 * OAuth2.0请求其他服务器资源中的角色:
 * 
 * <pre>
 * ->资源拥有者:通常为用户,也可以使应用程序
 * ->第三方应用:本身不存储资源,需要通过资源拥有者去请求资源服务器的资源
 * ->授权服务器:也叫认证服务器,用于对资源拥有者身份认证,访问资源授权等,认证成功后发放授权码(access_token)给第三方
 * ->资源服务器:存储拥有者资源的服务器,会给第三方应用一个客户端标识(client_id)和秘钥(client_secret),标识第三方的身份
 * </pre>
 * 
 * OAuth2.0请求其他服务器资源中的流程:
 * 
 * <pre>
 * -> 用户在第三方客户端请求认证服务器服务授权 -> 证服服务同意给第三方授权 -> 第三方服务获得授权码<br>
 * -> 第三方服务利用授权码从认证服务器申请令牌 -> 第三方服务获得证服服务器的响应令牌<br>
 * -> 第三方服务携带令牌从资源服务器获得用户资源 -> 资源服务器返回用户信息
 * </pre>
 * 
 * OAuth2.0授权码模式原始方式获得授权码,令牌流程:
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
 * 从服务提供商获得需要登录的用户信息,登录拦截social的流程:
 * 
 * <pre>
 * ->{@link SecurityContextPersistenceFilter}:通过请求获得用户登录信息,并在拦截器离开时将登录信息再次存入到session中
 * ->{@link UsernamePasswordAuthenticationFilter}:真正的登录请求拦截与验证
 * ->{@link SocialAuthenticationFilter}:第三方登录请求验证
 * ->{@link SocialAuthenticationService (OAuth2AuthenticationService)}:
 * ->{@link Authentication(SocialAuthenticationToken)}
 * ->{@link AuthenticationManager(ProviderManager)}
 * ->{@link AuthenticationProvider(SocialAuthenticationProvider)}
 * ->{@link UsersConnectionRepository(JdbcUsersConnectionRepository)}
 * ->{@link SocialUserDetailsService}
 * ->{@link SocialUserDetails}
 * ->{@link Authentication(SocialAuthenticationToken)}
 * </pre>
 * 
 * Spring Social相关接口,抽象类,类:
 * 
 * <pre>
 * {@link ServiceProvider}:服务器提供者需要实现的接口
 * {@link AbstractOAuth2ServiceProvider}:ServiceProvider的抽象实现,自定义类可以继承该抽象类,实现服务提供商
 * {@link OAuth2Operations}:封装了从用户请求第三方应用,到第三方应用请求服务提供商获得令牌的全部操作
 * {@link OAuth2Template}:完成OAuth2协议执行的流程
 * {@link ApiBinding}:SpringSocial提供的第三用应用获取服务提供商数据的接口
 * {@link AbstractOAuth2ApiBinding}:ApiBinding的抽象实现,自定义类可以继承该抽象类,实现对服务提供商数据的调用
 * ->{@link AbstractOAuth2ApiBinding#accessToken}:验证完成后由服务提供商发放的令牌,实现该抽象类的类不能是单例
 * {@link Connection}:非数据库链接,是封装第三方应用从服务提供商获取的用户信息
 * {@link ConnectionFactory}:创建Connection的链接工厂,包含ServiceProvider的实例
 * {@link OAuth2Connection}:Connection的抽象实现类
 * {@link OAuth2ConnectionFactory}:ConnectionFactory的抽象实现
 * {@link ApiAdapter}:在Connection和ServiceProvider之间做适配,用来自定义服务提供商返回的用户信息对象
 * {@link UsersConnectionRepository}:用来在Connection和第三方(本地)数据库进行交互的接口
 * {@link JdbcUsersConnectionRepository}:UsersConnectionRepository的实现类
 * </pre>
 * 
 * SpringSocial已经不维护了,只能直接使用SpringSecurity进行Social相关操作,详见
 * 
 * @see <a
 *      href="https://spring.io/blog/2018/03/06/using-spring-security-5-to-integrate-with-oauth-2-secured-services-such-as-facebook-and-github"
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