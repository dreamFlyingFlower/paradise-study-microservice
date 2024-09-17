package com.wy.oauth2;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

/**
 * SpringSecurity6+OAuth2+Wechat
 * 
 * <pre>
 * 1.需要在微信的官网上配置域名,域名要和项目的地址相同
 * 2.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI:默认用户访问端点,
 * 		需加上配置文件中spring.security.oauth2.client.registration下的key使用,如微信为/oauth2/authorization/wechat
 * 3.客户端在页面或其他服务中访问/oauth2/authorization/wechat,请求被重定向到了https://open.weixin.qq.com/connect/oauth2/authorize这个地址
 * 4.此时客户端会跳转到微信授权页面
 * 5.点击同意后,服务端会重定向到redirect_uri的地址,即本项目地址/login/oauth2/code/wechat.
 * 		若该地址后面携带了code和state参数,则表示code获取成功.回调地址中的state和此前发起请求时的state两个值是一样的
 * 6.通过日志可以看到,接着又发起了获取access_token的请求,如果获取成功,随即就会使用acces_token再请求获取用户信息的接口
 * 7.最后在得到用户数据后会创建对应的Authentication对象,并为其进行持久化操作,至此微信公众号网页授权的整个过程就完成了
 * 8.SecurityContextHolder.getContext().getAuthentication().getName():获取实际的Authentication对象中的用户名
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2023-02-01 16:51:06
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityWechatConfig {

	@Resource
	private ClientRegistrationRepository clientRegistrationRepository;

	/**
	 * 定义前端访问端点,此处直接使用了系统的/oauth2/authorization
	 * 
	 * @param clientRegistrationRepository 客户端认证服务
	 * @return 认证请求认证解析器
	 */
	private OAuth2AuthorizationRequestResolver
			authorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
		// 基础访问地址,实际访问需加上配置文件中spring.security.oauth2.client.registration下的key使用,如微信为/oauth2/authorization/wechat
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
		// 重新调整顺序,修改clientId参数名称为appid
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
		messageConverter.setSupportedMediaTypes(List.of(MediaType.TEXT_PLAIN));
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

		// 参考父类的源码,重写createParameters(),根据微信的文档,依次添加appid,secret,grant_type,code这四个参数
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
		messageConverter.setSupportedMediaTypes(List.of(MediaType.TEXT_PLAIN));
		RestTemplate restTemplate = new RestTemplate(List.of(messageConverter));
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
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				// 自定义oauth2,配置OAuth2 Client和OAuth2 Server交互,启用SSO
				.oauth2Login(oauth2 -> oauth2
						// 登录地址
						// .loginPage(null)
						// 自定义登录成功方法
						// .successHandler(null)
						// 定制发起授权请求,有authorizationRequestResolver的扩展点
						.authorizationEndpoint(authorization -> authorization.authorizationRequestResolver(
								authorizationRequestResolver(clientRegistrationRepository)))
						// 获取access_token,只有一个accessTokenResponseClient扩展点
						.tokenEndpoint(token -> token.accessTokenResponseClient(accessTokenResponseClient()))
						// 获取用户信息,自定义service,配置OAuth2UserService的实例
						.userInfoEndpoint(userInfo -> userInfo.userService(userService())));

		// 自定义oauth2资源服务器
		// httpSecurity.oauth2ResourceServer((oauth2) ->
		// oauth2.jwt(Customizer.withDefaults()));

		return httpSecurity.build();
	}
}