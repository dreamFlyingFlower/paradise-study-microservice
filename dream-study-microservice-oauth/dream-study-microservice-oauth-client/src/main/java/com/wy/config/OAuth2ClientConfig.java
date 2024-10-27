package com.wy.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

/**
 * OAuth2 4种登录模式配置
 * 
 * @auther 飞花梦影
 * @date 2021-07-03 10:58:55
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SuppressWarnings("deprecation")
@Deprecated
@Configuration
@EnableOAuth2Client
public class OAuth2ClientConfig {

	/**
	 * 授权码模式资源配置详情
	 * 
	 * @return 授权码模式资源配置详情对象
	 */
	@ConfigurationProperties("config.oauth2-client-code")
	@Bean
	OAuth2ProtectedResourceDetails authorizationCodeResourceDetails() {
		return new AuthorizationCodeResourceDetails();
	}

	/**
	 * 客户端模式资源配置详情
	 * 
	 * @return 客户端模式资源配置详情对象
	 */
	@ConfigurationProperties("config.oauth2-client-credentials")
	@Bean
	OAuth2ProtectedResourceDetails clientCredentialsResourceDetails() {
		return new ClientCredentialsResourceDetails();
	}

	/**
	 * 用户名密码模式资源配置详情
	 * 
	 * @return 用户名密码模式资源配置详情对象
	 */
	@ConfigurationProperties("config.oauth2-client-password")
	@Bean
	OAuth2ProtectedResourceDetails resourceOwnerPasswordResourceDetails() {
		return new ResourceOwnerPasswordResourceDetails();
	}

	/**
	 * 授权码模式restTeampte
	 * 
	 * @param resourceDetails 客户端请求资源详情
	 * @param oauth2ClientContext 客户端上下文环境
	 * @return OAuth2RestTemplate
	 */
	@Bean
	OAuth2RestTemplate oauth2ClientCodeRestTemplate(
			@Qualifier("authorizationCodeResourceDetails") OAuth2ProtectedResourceDetails resourceDetails,
			OAuth2ClientContext oauth2ClientContext) {
		return new OAuth2RestTemplate(resourceDetails, oauth2ClientContext);
	}

	/**
	 * 客户端模式restTeampte
	 * 
	 * @param resourceDetails 客户端请求资源详情
	 * @return OAuth2RestTemplate
	 */
	@Bean
	OAuth2RestTemplate oauth2ClientCredsRestTemplate(
			@Qualifier("clientCredentialsResourceDetails") OAuth2ProtectedResourceDetails resourceDetails) {
		return new OAuth2RestTemplate(resourceDetails);
	}

	/**
	 * 用户名密码模式restTeampte
	 * 
	 * @param resourceDetails 客户端请求资源详情
	 * @return OAuth2RestTemplate
	 */
	@Bean
	OAuth2RestTemplate oauth2ClientPasswordRestTemplate(
			@Qualifier("resourceOwnerPasswordResourceDetails") OAuth2ProtectedResourceDetails resourceDetails) {
		return new OAuth2RestTemplate(resourceDetails);
	}
}