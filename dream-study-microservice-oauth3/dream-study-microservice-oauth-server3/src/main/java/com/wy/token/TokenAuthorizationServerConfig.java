package com.wy.token;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.wy.config.AuthorizationServerConfig;
import com.wy.oidc.OidcUserInfoService;

import dream.flying.flower.framework.security.constant.ConstSecurity;
import lombok.RequiredArgsConstructor;

/**
 * SpringSecurity6认证服务器Token配置,只展示差异配置,主要配置见{@link AuthorizationServerConfig}
 * 
 * @author 飞花梦影
 * @date 2024-09-18 22:02:11
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class TokenAuthorizationServerConfig {

	/**
	 * 第一种:自定义JWT和IdToken.当前传入参数OidcUserInfoService,自由度更高
	 * 
	 * @param userInfoService OidcUserInfoService
	 * @return OAuth2TokenCustomizer的实例
	 */
	@Bean
	OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(OidcUserInfoService userInfoService) {
		return (context) -> {
			if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
				OidcUserInfo userInfo = userInfoService.loadUser(context.getPrincipal().getName());
				context.getClaims().claims(claims -> claims.putAll(userInfo.getClaims()));
			}
		};
	}

	/**
	 * 同上.自定义JWT和IdToken,自定义的数据可以从Authentication中获取.2种只需注入一个
	 * 
	 * {@link SecurityContextHolder#getContext()}->{@link SecurityContext#getAuthentication()}
	 * 
	 * @return OAuth2TokenCustomizer的实例
	 */
	@Bean
	OAuth2TokenCustomizer<JwtEncodingContext> oauth2TokenCustomizer() {
		return new FederatedIdentityIdTokenCustomizer();
	}

	/**
	 * 如果需要自定义tokenGenerator,必须手动设置oauth2TokenCustomizer(),否则自定义的属性无法添加到token中,
	 * 即使FederatedIdentityIdTokenCustomizer已经注入到Spring容器中
	 * 
	 * 该方式对全局的JWT有效
	 * 
	 * jwtGenerator必须注入,否则无法使用授权码类型,除非自定义
	 * 
	 * @param jwkSource JWKSource
	 * @return OAuth2TokenGenerator
	 */
	@Bean
	OAuth2TokenGenerator<?> tokenGenerator1(JWKSource<SecurityContext> jwkSource) {
		JwtGenerator jwtGenerator = new JwtGenerator(new NimbusJwtEncoder(jwkSource));
		// 如果不设置自定义token生成器,则无法在token中添加自定义的属性,即使将bean输入到spring中也不行
		jwtGenerator.setJwtCustomizer(oauth2TokenCustomizer());
		OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
		OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
		return new DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
	}

	/**
	 * 该方式只对access_token方式有效,对authorization_code无效
	 * 
	 * {@link SecurityContextHolder#getContext()}->{@link SecurityContext#getAuthentication()}
	 * 
	 * @return OAuth2TokenCustomizer
	 */
	@Bean
	OAuth2TokenCustomizer<OAuth2TokenClaimsContext> tokenCustomizer2() {
		return context -> {
			OAuth2TokenClaimsSet.Builder claims = context.getClaims();
			// 将权限信息或其他信息放入jwt的claims中,可以从context中拿到client_id
			claims.claim(ConstSecurity.AUTHORITIES_KEY, "自定义参数");
		};
	}

	/**
	 * 当前方式自定义token,只对access_token方式有效,对authorization_code和其他类型无效
	 * 
	 * jwtGenerator必须注入,否则无法使用授权码类型,除非自定义
	 * 
	 * @param jwkSource JWKSource
	 * @return OAuth2TokenGenerator
	 */
	@Bean
	OAuth2TokenGenerator<?> tokenGenerator2(JWKSource<SecurityContext> jwkSource) {
		JwtGenerator jwtGenerator = new JwtGenerator(new NimbusJwtEncoder(jwkSource));
		OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
		accessTokenGenerator.setAccessTokenCustomizer(tokenCustomizer2());
		OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
		return new DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
	}
}