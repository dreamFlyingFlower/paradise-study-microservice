package com.wy.oauth.grant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * 自定义授权类型.Grant配置需要在AuthorizationServerConfigurerAdapter配置类进行配置
 * 
 * 主要将原有授权模式类和自定义授权模式类添加到一个集合,然后用该集合为入参创建一个CompositeTokenGranter组合类,最后在tokenGranter设置CompositeTokenGranter进去
 * 
 * CompositeTokenGranter是一个组合类,它可以将多个TokenGranter实现组合起来,以便在处理OAuth2令牌授权请求时使用。
 *
 * @author 飞花梦影
 * @date 2024-10-24 15:07:07
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Deprecated
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	AuthenticationManager authenticationManager;

	/**
	 * JwtTokenStore存取令牌
	 * 
	 * @return TokenStore
	 */
	@Bean
	TokenStore jwtTokenStore() {
		return new JwtTokenStore(jwtAccessTokenConverter());
	}

	/**
	 * 管理JWT的生成规则,管理等
	 * 
	 * @return JwtAccessTokenConverter
	 */
	@Bean
	JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
		jwtAccessTokenConverter.setSigningKey("密钥,可配置");
		return jwtAccessTokenConverter;
	}

	/**
	 * 密码模式需要注入authenticationManager
	 * 
	 * @param endpoints
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		// 获取原有默认授权模式(授权码模式、密码模式、客户端模式、简化模式)的授权者,用于支持原有授权模式
		List<TokenGranter> tokenGranters = new ArrayList<>(Collections.singletonList(endpoints.getTokenGranter()));
		// 添加自定义TokenGranter到集合
		tokenGranters.add(new SmsTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(),
				endpoints.getOAuth2RequestFactory(), authenticationManager));
		// CompositeTokenGranter是一个TokenGranter组合类
		CompositeTokenGranter compositeTokenGranter = new CompositeTokenGranter(tokenGranters);

		endpoints.authenticationManager(authenticationManager)
				.tokenStore(jwtTokenStore())
				.accessTokenConverter(jwtAccessTokenConverter())
				// 将组合类设置进AuthorizationServerEndpointsConfigurer
				.tokenGranter(compositeTokenGranter);
	}

	/**
	 * 最好还需要配置客户端信息,在客户端支持的授权模式中添加上自定义的授权模式,即phonecode
	 * 
	 * @param clients
	 * @throws Exception
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				.withClient("admin")
				.authorizedGrantTypes("authorization_code", "password", "implicit", "client_credentials",
						"refresh_token", SmsTokenGranter.GRANT_TYPE);
	}
}