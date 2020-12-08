package com.wy.oauth2;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * @apiNote OAuth2认证服务器,只需要@EnableAuthorizationServer即可实现认证服务器,实现4种认证模式:
 *          授权码模式;简化模式;密码模式;客户端模式.常用的是授权码模式和密码模式
 *          @apiNote 
 * @author ParadiseWY
 * @date 2019年9月26日
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthenticationServer {

}