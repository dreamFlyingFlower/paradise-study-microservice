package com.wy.config;

import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

/**
 * OAuth2客户端配置
 *
 * @author 飞花梦影
 * @date 2021-07-06 14:50:32
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
public class OAuthClientConfig {

	@Bean
	public WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
		ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
				new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
		return WebClient.builder().apply(oauth2Client.oauth2Configuration()).build();
	}

	@SuppressWarnings("deprecation")
	@Bean
	public OAuth2AuthorizedClientManager authorizedClientManager(
			ClientRegistrationRepository clientRegistrationRepository,
			OAuth2AuthorizedClientRepository authorizedClientRepository) {
		OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
				.authorizationCode()
				.refreshToken()
				.clientCredentials()
				// 不安全,但可以用
				.password()
				.build();
		DefaultOAuth2AuthorizedClientManager authorizedClientManager =
				new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
		authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
		// For the `password` grant, the `username` and `password` are supplied via
		// request parameters,
		// so map it to `OAuth2AuthorizationContext.getAttributes()`.
		authorizedClientManager.setContextAttributesMapper(contextAttributesMapper());
		return authorizedClientManager;
	}

	private Function<OAuth2AuthorizeRequest, Map<String, Object>> contextAttributesMapper() {
		return authorizeRequest -> {
			Map<String, Object> contextAttributes = Collections.emptyMap();
			HttpServletRequest servletRequest = authorizeRequest.getAttribute(HttpServletRequest.class.getName());
			String username = servletRequest.getParameter(OAuth2ParameterNames.USERNAME);
			String password = servletRequest.getParameter(OAuth2ParameterNames.PASSWORD);
			if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
				contextAttributes = new HashMap<>();
				// `PasswordOAuth2AuthorizedClientProvider` requires both attributes
				contextAttributes.put(OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, username);
				contextAttributes.put(OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, password);
			}
			return contextAttributes;
		};
	}

	/**
	 * 实现客户端登录后获取权限信息
	 * 
	 * OidcUser是Spring Security框架中的一个接口,用于表示OpenID Connect(OIDC)认证成功后的用户信息.
	 * 在OAuth2.0和OIDC授权流程中,用户通过认证服务器进行身份验证,并在认证成功后,认证服务器会返回一个包含用户信息的JWT令牌,OidcUser接口用于表示这个JWT令牌中包含的用户信息.
	 * 这里同样会将用户信息存入Authentication中,因为用户信息是在oauth2UserService()中被处理的,最终会被封装成一个OidcUser对象.
	 * OidcUser实现了SpringSecurity的Authentication接口,因此可以将其作为认证信息存储在SecurityContext中,用于后续的授权访问
	 * 
	 * @return OAuth2UserService
	 */
	@Bean
	OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
		final OidcUserService delegate = new OidcUserService();

		return userRequest -> {
			OidcUser oidcUser = delegate.loadUser(userRequest);
			OAuth2AccessToken accessToken = userRequest.getAccessToken();
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
			try {
				JWT jwt = JWTParser.parse(accessToken.getTokenValue());
				JWTClaimsSet claimSet = jwt.getJWTClaimsSet();
				Collection<String> userAuthorities = claimSet.getStringListClaim("authorities");
				mappedAuthorities
						.addAll(userAuthorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
			} catch (ParseException e) {
				System.err.println("Error OAuth2UserService: " + e.getMessage());
			}
			oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
			return oidcUser;
		};
	}

	/**
	 * 这个方法主要完成以下配置:
	 * 
	 * <pre>
	 * 对所有请求进行授权认证,要求用户登录
	 * 设置OAuth 2.0登录的URL
	 * 配置OAuth 2.0的授权端点,使用客户端的PKCE(Proof Key for Code Exchange)来增加安全性
	 * 设置OIDC(OpenID Connect)用户服务,用于获取用户权限信息
	 * 使用默认的OAuth 2.0客户端配置
	 * </pre>
	 * 
	 * @param http
	 * @param clientRegistrationRepository
	 * @return
	 * @throws Exception
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http,
			ClientRegistrationRepository clientRegistrationRepository) throws Exception {

		String base_uri = OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
		DefaultOAuth2AuthorizationRequestResolver resolver =
				new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, base_uri);
		resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());

		http
				// 所有请求都需经过授权认证
				.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
				// 配置登录URL
				.oauth2Login(oauth2Login -> {
					oauth2Login.loginPage("/oauth2/authorization/login");
					oauth2Login.authorizationEndpoint().authorizationRequestResolver(resolver);
					// 配置自定义的oidc用户服务
					oauth2Login.userInfoEndpoint(userInfo -> userInfo.oidcUserService(this.oidcUserService()));
				})

				// 使用默认客户端配置
				.oauth2Client(Customizer.withDefaults());
		return http.build();
	}
}