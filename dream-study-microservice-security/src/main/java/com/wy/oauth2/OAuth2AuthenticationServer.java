package com.wy.oauth2;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

/**
 * OAuth2认证服务器
 * 
 * {@link EnableAuthorizationServer}:添加该注解即可启动认证服务器,实现4种认证模式:授权码模式;密码模式;简化模式;客户端模式
 * 
 * 启动项目之后,可使用get请求默认访问/oauth/authorize?response_type=&client_id=&redirect_url=&scope,
 * 其中response_type为4中认证模式中的标识符,可从官网查看;client_id为分配给第三方服务的标识;
 * redirect_url为认证服务器返回给第三方服务器的授权码地址,授权码会拼接在该地址上;scope为权限,由服务提供商定义.
 * 访问该地址时会填一个用户名和密码,该用户名和密码就是相当于用户登录,同时需要给用户一个ROLE_USER的权限,否则登录失败
 * 
 * 获得授权码之后,再用post请求访问/oauth/token获得令牌信息,参数为grant_type,client_id,code,redirect_url,scope,见OAuth2官网
 * 
 * SpringOAuth获取令牌请求(/oauth/token)的核心流程:
 * 
 * <pre>
 * ->{@link TokenEndpoint}:
 * ->{@link ClientDetailsService}:
 * -->{@link InMemoryClientDetailsService}:
 * ->{@link ClientDetails}:
 * ->{@link TokenRequest}:
 * ->{@link TokenGranter}:
 * ->{@linkplain CompositeTokenGranter}:
 * -->{@link OAuth2Request}
 * -->{@link Authentication}:
 * ->{@link OAuth2Authentication}:
 * ->{@link AuthorizationServerTokenServices}:
 * -->{@link DefaultTokenServices}:
 * ->{@link OAuth2AccessToken}:
 * </pre>
 * 
 * @auther 飞花梦影
 * @date 2019-09-26 22:42:52
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthenticationServer {

}