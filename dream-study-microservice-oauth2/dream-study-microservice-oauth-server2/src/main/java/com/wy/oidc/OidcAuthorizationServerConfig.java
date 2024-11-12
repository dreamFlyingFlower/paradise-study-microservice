package com.wy.oidc;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import com.wy.config.AuthorizationServerConfig;

import lombok.RequiredArgsConstructor;

/**
 * SpringSecurity6认证服务器OIDC配置,只展示差异配置,全部配置见{@link AuthorizationServerConfig}
 * 
 * @author 飞花梦影
 * @date 2024-09-18 22:02:11
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class OidcAuthorizationServerConfig {

	/**
	 * 配置认证相关的端点过滤器链,用于处理与协议端点相关的请求和响应
	 * 
	 * 负责处理OAuth2和OpenID Connect的协议细节,例如授权请求、令牌颁发和验证等
	 *
	 * @param http security核心配置类
	 * @return 过滤器链
	 * @throws Exception
	 */
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
			RegisteredClientRepository registeredClientRepository,
			AuthorizationServerSettings authorizationServerSettings) throws Exception {
		// 默认配置,忽略认证端点的csrf校验.如果要整合OAuth2,则需要当前方式注入相关对象
		// 将OAuth2AuthorizationServerConfigurer配置应用到HttpSecurity中
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

		// 自定义用户映射器,该方式会改变客户端调用/userinfo接口的数据
		Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper = (context) -> {
			OidcUserInfoAuthenticationToken authentication = context.getAuthentication();
			JwtAuthenticationToken principal = (JwtAuthenticationToken) authentication.getPrincipal();
			// 从数据库重新取值
			// return new
			// OidcUserInfo(JsonHelpers.parseMap(userService.loadUserByUsername(principal.getName())));
			// 直接从已登录的授权信息中获得信息
			return new OidcUserInfo(principal.getToken().getClaims());
		};

		// 获得第一步应用的OAuth2AuthorizationServerConfigurer
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
				// 开启OpenID Connect 1.0(OIDC)协议相关端点,可访问/userinfo接口
				// 如果RegisteredClient的scope中只包含openid和profile,该接口只会返回sub属性
				// 如果需要其他属性,需要在RegisteredClient中添加自定义属性名或者直接使用上方的userInfoMapper,自定义返回数据
				.oidc(Customizer.withDefaults())
				// 开启OpenID Connect 1.0协议相关端点,并使用自定义的UserInfo映射器
				.oidc((oidc) -> {
					oidc.userInfoEndpoint((userInfo) -> {
						userInfo.userInfoMapper(userInfoMapper);
					});
				});

		// 使用JWT处理令牌用于用户信息和/或客户端注册,同时将认证服务器做为一个资源服务器
		http.oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()));

		return http.build();
	}
}