package com.wy.config;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.DefaultMapOAuth2AccessTokenResponseConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户通过本系统调用Wechat进行登录认证
 *
 * @author 飞花梦影
 * @date 2024-09-17 21:25:37
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@EnableWebSecurity
@AllArgsConstructor
public class SecurityWechatConfig {

	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;

	private OAuth2AuthorizationRequestResolver
			authorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
		String authorizationRequestBaseUri =
				OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
		// 参考框架内默认的实例构造方法
		DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver(
				clientRegistrationRepository, authorizationRequestBaseUri);
		// 设置OAuth2AuthorizationRequest.builder的定制逻辑
		resolver.setAuthorizationRequestCustomizer(builder -> builder.parameters(this::parametersConsumer)
				.authorizationRequestUri(this::authorizationRequestUriFunction));
		return resolver;
	}

	private void parametersConsumer(Map<String, Object> parameters) {
		Object clientId = parameters.get(OAuth2ParameterNames.CLIENT_ID);
		Object redirectUri = parameters.get(OAuth2ParameterNames.REDIRECT_URI);
		Object responseType = parameters.get(OAuth2ParameterNames.RESPONSE_TYPE);
		Object scope = parameters.get(OAuth2ParameterNames.SCOPE);
		Object state = parameters.get(OAuth2ParameterNames.STATE);
		// 清除掉原来所有的参数
		parameters.clear();
		// 修改clientId参数名称为appid
		parameters.put("appid", clientId);
		parameters.put(OAuth2ParameterNames.REDIRECT_URI, redirectUri);
		parameters.put(OAuth2ParameterNames.RESPONSE_TYPE, responseType);
		parameters.put(OAuth2ParameterNames.SCOPE, scope);
		parameters.put(OAuth2ParameterNames.STATE, state);

	}

	private URI authorizationRequestUriFunction(UriBuilder builder) {
		// 添加#wechat_redirect
		builder.fragment("wechat_redirect");
		return builder.build();
	}

	private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
		DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();
		// 注入自定义WechatOAuth2AuthorizationCodeGrantRequestEntityConverter
		client.setRequestEntityConverter(new WechatOAuth2AuthorizationCodeGrantRequestEntityConverter());
		// 创建一个OAuth2AccessTokenResponseHttpMessageConverter对象,设置支持的MediaType为text/plain
		OAuth2AccessTokenResponseHttpMessageConverter messageConverter =
				new OAuth2AccessTokenResponseHttpMessageConverter();
		messageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN));
		messageConverter.setAccessTokenResponseConverter(new WechatOAuth2AccessTokenResponseConverter());
		// 其他配置照搬源码
		RestTemplate restTemplate = new RestTemplate(Arrays.asList(new FormHttpMessageConverter(), messageConverter));
		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
		client.setRestOperations(restTemplate);
		return client;
	}

	private static class WechatOAuth2AccessTokenResponseConverter
			implements Converter<Map<String, Object>, OAuth2AccessTokenResponse> {

		private static final DefaultMapOAuth2AccessTokenResponseConverter delegate =
				new DefaultMapOAuth2AccessTokenResponseConverter();

		// 响应中缺少token_type字段,为避免报错默认填充,剩余部分依然委托给默认的DefaultMapOAuth2AccessTokenResponseConverter处理
		@Override
		public OAuth2AccessTokenResponse convert(Map<String, Object> source) {
			source.put(OAuth2ParameterNames.TOKEN_TYPE, OAuth2AccessToken.TokenType.BEARER.getValue());
			return delegate.convert(source);
		}
	}

	// 无法直接实现接口,不过可以继承OAuth2AuthorizationCodeGrantRequestEntityConverter
	private static class WechatOAuth2AuthorizationCodeGrantRequestEntityConverter
			extends OAuth2AuthorizationCodeGrantRequestEntityConverter {

		// 参考父类的源码,依葫芦画瓢重写createParameters方法,根据微信的文档,依次添加appid,secret,grant_type,code这四个参数
		@Override
		protected MultiValueMap<String, String>
				createParameters(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
			ClientRegistration clientRegistration = authorizationCodeGrantRequest.getClientRegistration();
			OAuth2AuthorizationExchange authorizationExchange =
					authorizationCodeGrantRequest.getAuthorizationExchange();
			MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
			parameters.add("appid", clientRegistration.getClientId());
			parameters.add("secret", clientRegistration.getClientSecret());
			parameters.add(OAuth2ParameterNames.GRANT_TYPE, authorizationCodeGrantRequest.getGrantType().getValue());
			parameters.add(OAuth2ParameterNames.CODE, authorizationExchange.getAuthorizationResponse().getCode());
			return parameters;
		}
	}

	private OAuth2UserService<OAuth2UserRequest, OAuth2User> userService() {
		DefaultOAuth2UserService userService = new DefaultOAuth2UserService();
		// 注入自定义的requestEntityConverter
		userService.setRequestEntityConverter(new WechatOAuth2UserRequestEntityConverter());
		// 创建一个MappingJackson2HttpMessageConverter对象,同样设置支持的MediaType为text/plain
		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
		messageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN));
		RestTemplate restTemplate = new RestTemplate(Arrays.asList(messageConverter));
		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
		userService.setRestOperations(restTemplate);
		return userService;
	}

	private static class WechatOAuth2UserRequestEntityConverter
			implements Converter<OAuth2UserRequest, RequestEntity<?>> {

		// 根据微信文档,在请求地址中拼接上access_token和openid两个参数
		@Override
		public RequestEntity<?> convert(OAuth2UserRequest userRequest) {
			ClientRegistration clientRegistration = userRequest.getClientRegistration();
			URI uri = UriComponentsBuilder
					.fromUriString(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri())
					.queryParam(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.getAccessToken().getTokenValue())
					.queryParam("openid", userRequest.getAdditionalParameters().get("openid"))
					.build()
					.toUri();
			return new RequestEntity<>(HttpMethod.GET, uri);
		}
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.oauth2Login(oauth2 -> oauth2
				.authorizationEndpoint(authorization -> authorization
						.authorizationRequestResolver(authorizationRequestResolver(clientRegistrationRepository)))
				.tokenEndpoint(token -> token.accessTokenResponseClient(accessTokenResponseClient()))
				.userInfoEndpoint(userInfo -> userInfo.userService(userService())));
		DefaultSecurityFilterChain filterChain = http.build();
		filterChain.getFilters().stream().map(Object::toString).forEach(log::info);
		return filterChain;
	}
}