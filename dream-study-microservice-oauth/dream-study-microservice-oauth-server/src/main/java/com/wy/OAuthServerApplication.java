package com.wy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@link Deprecated}:在SpringSecurity5.7以上版本中,认证配置方式以及资源服务配置方式被废弃
 * 
 * OAuth2认证服务器
 * 
 * OAuth2规范有4种授权模式:授权码模式,用户名密码模式,简单模式,客户端模式,详情见notes/SpringCloud/OAuth2.0官方文档.pdf
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
 * 固定API:
 * 
 * <pre>
 *	/oauth/authorize:授权端点
 * /oauth/token:令牌端点
 * /oauth/confirm_access:用户确认授权提交端点
 * /oauth/error:授权服务错误信息端点
 * /oauth/check_token:用于资源服务访问的令牌解析端点,检查token是否失效.eg:http://ip:55100/oauthServer/oauth/check_token?token=
 * /oauth/token_key:提供公有密匙的端点,如果使用JWT令牌的话
 * </pre>
 * 
 * OAuth2.0授权码模式发放授权码,令牌流程:
 * 
 * <pre>
 * 1./oauth/authorize:固定API,GET/POST,Client通过此请求获取授权码(code),此时会让用户授权登录,需要给用户一个ROLE_USER的权限,否则登录失败
 * ->{@link AuthorizationEndpoint#authorize}:处理API.URL不可配置,但可以使用{@link RestController}将请求地址重写
 * ->eg:http://ip:port/{server.port}/oauth/authorize?response_type=code&state=123456&client_id=client_id&scope=all&redirect_uri=http://otherappurl
 * -->response_type:响应模式,有2种,在授权码模式下固定为code,还有token
 * -->client_id:由认证服务器发给Client的标识,唯一
 * -->client_secret:由认证服务器发放给Client的密钥
 * -->redirect_uri:用户授权通过,Client取得授权码code的页面跳转地址,code会拼接在地址上.系统内置了http://example.com.
 * 		配置security.oauth2.client.registered-redirect-uri可自定义该地址,
 * 		不同版本不配置不一样,当前版本可直接设置在{@link AuthorizationCodeResourceDetails}.
 * 		上述地址为http://otherappurl?code=ycjU3F&state=123456,可以拿到code为ycjU3F
 * -->scope:认证服务器给第三方的权限,可自定义
 * 
 * 2./oauth/authorize:URL同1,固定API,只能是POST,且参数中必须带{@link OAuth2Utils#USER_OAUTH_APPROVAL}.用户是否授权给Client.
 * ->{@link AuthorizationEndpoint#approveOrDeny}:处理API.用户登录之后会跳到授权页面,授权成功则返回code给Client.如果权限配置(autoApprove)为false,则不跳转该页面.
 * 		该授权页面由授权服务器内部重定向跳转
 * 
 * 3./oauth/token:固定API,POST,Client通过跳转地址获得code后,再向认证服务器发送请求,获得令牌(access_token)
 * ->{@link TokenEndpoint}:处理API
 * ->eg:http://ip:port/{server.port}/oauth/token?client_id=client_id&client_secret=guest&grant_type=authorization_code&code=ycjU3F&redirect_uri=http://otherappurl
 * -->grant_type:在授权码模式下固定为authorization_code,当上一个access_token过期需要请求新的时,该值为refresh_token
 * -->client_id:由认证服务器发给第三方的标识,唯一
 * -->client_secret:由认证服务器发放给第三方的密码
 * -->code:上一步中得到的授权码code,只能使用一次
 * -->redirect_uri:同/oauth/authorize中的参数
 * -->scope:同/oauth/authorize中的参数
 * -->refresh_token:当上一个access_token过期之后,可使用上一次的refresh_token请求该地址获得新的access_token
 * -->认证服务器返回令牌(access_token)
 * 
 * 4.第三方从认证服务器获得令牌后,可用令牌请求资源服务器获得用户相关信息,令牌有时限,由认证服务器控制
 * 
 * 5./oauth/token:固定API,POST,重新获取令牌.grant_type换成refresh_token,同时带上第一次获取到的refresh_token,用户名和密码也不需要
 * ->eg:http://ip:55100/oauthServer/oauth/token?client_id=client_id&client_secret=guest&grant_type=refresh_token&refresh_token=
 * </pre>
 * 
 * 
 * OAuth2.0用户名密码认证服务
 * 
 * <pre>
 * 1./oauth/token:固定API,获取token
 * ->eg:http://ip:55100/oauthServer/oauth/token?client_id=client_id&client_secret=guest&grant_type=password&username=guest&password=123456
 * -->client_id:第三方客户端client_id,
 * -->client_secret:第三方客户端密码,如果服务器没有做特殊处理,该值不能加密或编码
 * -->grant_type:第三方客户端访问OAuth2认证服务器的方式
 * -->username:登录SpringSecurity服务的用户名和密码,实际情况下不应该有该参数.正常情况下应该先登录到本系统获得认证的token,
 * 		之后请求头中携带认证的token才能继续访问OAuth2认证服务器.或者SpringSecurity对所有的第三方请求都无需认证,则可不带该参数
 * -->password:同username,如果Security没有做任何密码的其他操作,传参时不能加密,要原文传输
 * 
 * -->access_token:返回值.OAuth2认证服务器返回的token,以后访问所有请求都要携带该token,否则无法访问
 * -->token_tpye:返回值.令牌类型
 * -->refresh_token:返回值.access_token到期时获取下一次access_token时的刷新token
 * -->expires_in:返回值.access_token过期时间
 * -->scope:返回值.权限域
 * </pre>
 * 
 * 认证服务器和资源服务器,最好不要在同一个类或同一个工程中,可能会出现无法解决的问题:
 * 
 * <pre>
 * {@link EnableAuthorizationServer}:在SpringSecurity5.7以上版本中,该方式被废弃.
 * 		类上添加该注解并继承{@link AuthorizationServerConfigurerAdapter}即可注册为认证服务器
 * ->{@link AuthorizationServerEndpointsConfiguration}:用来为授权请求提供服务,默认UR 是/oauth/authorize
 * ->{@link AuthorizationServerSecurityConfiguration}:
 * {@link AuthorizationServerConfigurerAdapter}:继承该类可以对OAuth2登录做些自定义的配置
 * ->{@link AuthorizationServerEndpointsConfigurer}:SpringOAuth其他入口点配置,如TokenEndpoint
 * {@link AuthorizationEndpoint}:用来为授权请求提供服务,默认URL是/oauth/authorize
 * {@link TokenEndpoint}:用来为访问令牌请求提供服务,默认URL是/oauth/token
 * </pre>
 * 
 * 搭建自己的认证服务器,表结构如下,具体表字段参照{@link JdbcClientDetailsService}:
 * 
 * <pre>
 * oauth_client:id,client_id,client_secret,redirect_uri,createtime,updatetime
 * oauth_scope:id,scope,default_scope,remark,createtime,createtime
 * oauth_access_token:id,client_id,user_id,token,expiretime,scope,createtime,updatetime
 * oauth_refresh_token:id,client_id,user_id,token,expiretime,scope,createtime,updatetime
 * oauth_authorization_code:id,client_id,user_id,code,redirect_uri,expiretime,scope,createtime,updatetime
 * </pre>
 * 
 * {@link TokenStore}:Token存储方式接口
 * 
 * <pre>
 * {@link InMemoryTokenStore}:内存存储,重启失效
 * {@link JdbcTokenStore}:数据库存储
 * {@link JwtTokenStore}:JWT存储
 * {@link JwkTokenStore}:JWK存储,JWT的密钥或者密钥对
 * {@link RedisTokenStore}:Redis存储
 * </pre>
 * 
 * OAuth获取令牌请求(/oauth/token)的核心流程:
 * 
 * <pre>
 * {@link AuthorizationEndpoint}:/oauth/authorize请求实现类,第三方请求登录认证
 * {@link TokenEndpoint}:/oauth/token请求的入口点,get和post请求都可以,必须已经请求了/oauth/authorize
 * {@link ClientDetailsService}:读取第三方应用信息
 * ->{@link InMemoryClientDetailsService}:默认实现类,读取第三方应用信息
 * ->{@link JdbcClientDetailsService}:自定义数据库实现类,从数据库读取第三方应用
 * {@link ClientDetails}:根据clientId读取相应的信息,类似于UserDetails,封装第三方应用信息
 * {@link TokenRequest}:封装请求中的其他信息,如用户名,密码等
 * {@link TokenGranter}:4种不同授权模式的实现,根据请求中的grant_type走不同的流程
 * ->{@link CompositeTokenGranter#grant}:集合了4种授权模式的类,并进行验证
 * ->{@link AuthorizationCodeTokenGranter#grant}:授权码模式具体实现类,由CompositeTokenGranter统一调用
 * -->{@link AuthorizationServerTokenServices}:生成OAuth2AccessToken接口
 * --->{@link DefaultTokenServices#createAccessToken()}:根据上一步得到的认证信息,生成OAuth2AccessToken
 * ---->{@link TokenStore}:令牌的存储
 * ---->{@link TokenEnhancer}:令牌增强器,当临牌生成之后,可以对令牌信息进行改造
 * {@link OAuth2AccessToken}:最终的令牌,scope等信息的集合
 * {@link JwtAccessTokenConverter}:JWT令牌生成器,里面的信息默认使用{@link DefaultAccessTokenConverter}生成
 * {@link DefaultAccessTokenConverter#convertAccessToken()}:JWT令牌中的默认信息生成器,重写该方法可自定义令牌传输信息
 * </pre>
 * 
 * OAuth2主要拦截器:
 * 
 * <pre>
 * {@link SecurityContextPersistenceFilter}:SpringSecurity主要拦截器
 * {@link OAuth2ClientContextFilter}
 * {@link OAuth2RestTemplate}
 * {@link AbstractAuthenticationProcessingFilter}:处理所有与认证相关的拦截器
 * {@link SecurityContextPersistenceFilter}:过滤保存Authentication到HttpSession中
 * </pre>
 * 
 * 使用SpringOAuth2搭建:
 * 
 * <pre>
 * dream-study-microservice-oauth-server:认证服务器
 * dream-study-microservice-oauth-client:客户端调用
 * dream-study-microservice-oauth-resource:资源服务器
 * </pre>
 * 
 * SpringSecurity搭建:
 * 
 * <pre>
 * dream-study-microservice-oauth-server:认证服务器,可直接使用作为认证服务器
 * dream-study-microservice-security-client:客户端调用,和OAuth2略有不同,更简洁,详见代码
 * dream-study-microservice-security-resource:资源服务器,和OAuth2搭建略有不同,更简洁,详见代码
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2021-04-09 11:04:34
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Deprecated
@SpringBootApplication
@MapperScan(basePackages = "com.wy.mapper")
public class OAuthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OAuthServerApplication.class, args);
	}
}