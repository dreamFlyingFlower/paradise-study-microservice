package com.wy.config;

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
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;
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
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.DefaultMapOAuth2AccessTokenResponseConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
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
 * SpringSecurity6整合OAuth2
 * 
 * 加载了OAuth2之后,会注入{@link AuthorizationEndpoint},{@link TokenEndpoint}默认生成如下端点,:
 * 
 * <pre>
 * /oauth/authorize:第三方请求登录认证
 *	forward:/oauth/confirm_access:转发授权页
 * forward:/oauth/error:转发到错误页
 * /oauth/token:获取token,必须已经请求了/oauth/authorize
 * /oauth2/authorization/{registrationId}:{@link OAuth2AuthorizationRequestRedirectFilter#DEFAULT_AUTHORIZATION_REQUEST_BASE_URI},
 * 		客户访问端点,该方法将自动转发到OAuth2服务提供者,发起授权请求,实际访问需加上配置文件中spring.security.oauth2.client.registration下的key使用
 * /login/oauth2/code/{client}:{@link OAuth2LoginAuthenticationFilter#DEFAULT_FILTER_PROCESSES_URI},
 * 		OAuth2服务端重定向端点,用于在OAuth2服务提供者重定向回到本应用时接收code,从而利用code换取accessToken
 * </pre>
 * 
 * {@link CommonOAuth2Provider}:已经定义好的一些第三方服务提供者,如Google,GITHUB
 * 
 * OAuth2大致流程:
 * 
 * <pre>
 * #OAuth2ClientRegistrationRepositoryConfiguration:自动配置以完成客户端信息的注册,构建过程由OAuth2ClientPropertiesMapper完成
 * {@link OAuth2AuthorizationRequestRedirectFilter#doFilterInternal}:用于向OAuth2协议服务端发起认证请求
 * ->{@link OAuth2AuthorizationRequestResolver#resolve}:生成授权请求对象{@link OAuth2AuthorizationRequest}.
 * 		当请求/oauth2/authorization/{registrationId}端点时,authorizationRequestResolver就会解析出{registrationId}对应的值,如github,
 * 		然后通过registrationId查询到对应客户端的注册信息,并通过构造器OAuth2AuthorizationRequest.Builder,创建出一个OAuth2AuthorizationRequest实例,
 * 		它主要作用就是生成重定向到OAuth2服务端获取code的地址.对于github来说,该地址为
 * 		https://github.com/login/oauth/authorize?response_type=code&client_id={client_id}&scope=read:user&state={state}&redirect_uri={redirect_uri}:
 * 		state:客户端生成的一个随机字符串,在Spring Security中,使用了32位长度的Base64编码生成算法;
 * 		redirect_uri:OAuth2服务端通过验证后重定向到本系统的地址,本系统从响应中获取code后发起认证.redirectUri需要事先注册在OAuth2服务端中,否则视为非授权的访问而拒绝.
 * 		在重定向之前,为了在认证通过之后能够跳转回认证前的访问路径,需要保存当前请求的地址,
 * 		在authorizationRequestRepository#saveAuthorizationRequest()中,会将当前请求存储到session中,这样就可以在OAuth2服务端回调之后,再从session取出.
 * 		OAuth2服务端在接受到请求后,如果正常,则会生成一个临时的code,然后连同请求参数中state一起拼接到redirect_uri的参数中,
 * 		例如https://{domain}/login/oauth2/code/github?code={code}&state={state},最后发起重定向,此时请求就会进入过滤器OAuth2LoginAuthenticationFilter
 * 
 * {@link OAuth2LoginAuthenticationFilter#attemptAuthentication}:继承自AbstractAuthenticationProcessingFilter,用于完成OAuth2的认证过程,生成认证对象.
 * 		OAuth2AuthorizationRequest为此前提交授权的请求,保存在Session中,而OAuth2AuthorizationResponse代表了OAuth2服务端重定向回来的响应,
 * 		其中也封装了请求时携带过去的state参数,他们构造了一个OAuth2AuthorizationExchange对象,
 * 		并连同ClientRegistration一并被封装到了OAuth2LoginAuthenticationToken对象中,
 * 		该对象用于在OAuth2LoginAuthenticationProvider发起认证请求时提取各种参数
 * 		具体过程如下:
 * 		1.检查URL参数是否有效,即是否包含了code和state
 * 		2.通过authorizationRequestRepository查询之前存储在session中的authorizationRequest,同时还要将其删除,以保证一个对应的code,只能被处理一次.
 * 		同时如果没有查询到对应的authorizationRequest,也不会继续执行,从而杜绝了其他伪造的重定向请求进入系统
 * 		3.这个authorizationRequest中保存了客户端的registrationId,因此可以通过clientRegistrationRepository查询到对应的客户端信息,即clientRegistration
 * 		4.再根据code和state参数,以及当前request请求的url作为redirectUri,构造出一个OAuth2AuthorizationResponse对象,即authorizationResponse
 * 		5.在得到clientRegistration,authorizationRequest,authorizationResponse三个实例之后,再构造出一个OAuth2LoginAuthenticationToken实例,它便是用来发起OAuth2认证的实际对象
 * 		6.OAuth2认证过程交由OAuth2LoginAuthenticationProvider执行
 * 		7.认证通过后,返回一个OAuth2LoginAuthenticationToken对象,这个对象主要是用于封装授权码模式的认证结果,经过转换,
 * 		将其principal,authorities,和clientRegistration的RegistrationId取出,最终构造出一个标准的OAuth2认证对象,即OAuth2AuthenticationToken
 * 
 * {@link OAuth2LoginAuthenticationProvider}:获取AccessToken.主要包括两个部分:请求OAuth2服务端获取AccessToken;获取服务端用户信息.
 * 		前者委托给了OAuth2AuthorizationCodeAuthenticationProvider来执行具体请求的逻辑,
 * 		而后者则通过UserService实例请求OAuth2服务端的UserInfo相关端点,获取用户信息,
 * 		最后上述AccessToken相关信息,以及用户信息被封装成OAuth2LoginAuthenticationToken认证对象返回
 * 
 * {@link OAuth2AuthorizationCodeAuthenticationProvider#authenticate()}:
 * 		1.authorizationRequest中包含了请求时携带的state,而authorizationResponse中包含了OAuth2服务端重定向URL中携带的state,
 * 		通过两个state的比较,校验是否为非法的重定向地址.
 * 		如果不校验state,当其他账号正常授权时重定向的地址被另一个人点击了,就可以能发生用户在毫无察觉的情况下登录其他人账号的情况,导致信息泄露
 * 		2.accessTokenResponseClient向OAuth2服务端发起认证请求,请求地址存储在ClientRegistration中的tokenUri,
 * 		即https://github.com/login/oauth/access_token.
 * 		请求体参数则包括code,redirect_uri,grant_type,另外还加入了Authorization的请求头,其属性值是用client_id和client_serect拼接后编码出来的一个字符串,
 * 		用于向OAuth2服务端证明客户端的真实性
 * 		3.OAuth2服务端通过认证后就会返回AccessToken,以及创建时间,过期时间等信息,最后封装成OAuth2AuthorizationCodeAuthenticationToken认证对象返回
 * 
 * {@link OAuth2UserService}:访问受保护资源.需要请求OAuth2服务端获取用户信息,用户信息是服务端保护的资源,包含了在Github中个人账号的各类属性,
 * 		例如id,用户名,头像,主页地址等等,因此这里需要携带AccessToken才能访问.
 * 		其中生成请求对象的关键代码在OAuth2UserRequestEntityConverter#convert()中:
 * 		1.用户信息的端点同样也是由clientRegistration提供，即https://api.github.com/user
 * 		2.accessToken作为参数,如果是GET,则被置于Header中的"Authorization"属性中,并按照规范添加"Bearer "的前缀;
 * 		如果是POST,则被放在请求表单参数"access_token"中,此处为GET
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
public class SecurityOAuth2Config {

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