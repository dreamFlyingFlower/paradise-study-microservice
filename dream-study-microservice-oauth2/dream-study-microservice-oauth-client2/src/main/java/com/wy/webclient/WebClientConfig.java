//package com.wy.webclient;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
//import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
//import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.support.WebClientAdapter;
//import org.springframework.web.service.invoker.HttpServiceProxyFactory;
//
///**
// * 使用WebFlux进行认证请求,SpringBoot3以上可用
// *
// * @author 飞花梦影
// * @date 2024-10-31 17:24:58
// * @git {@link https://github.com/dreamFlyingFlower}
// */
//@Configuration
//public class WebClientConfig {
//
//	/**
//	 * 使用OAuth2AuthorizedClientManager创建HttpServiceProxyFactory,然后使用它创建客户端
//	 *
//	 * @param authorizedClientManager OAuth2AuthorizedClientManager实例
//	 * @return WelcomeClient 实例
//	 * @throws Exception 如果创建客户端时发生错误,则抛出异常
//	 */
//	@Bean
//	public WelcomeClient welcomeClient(OAuth2AuthorizedClientManager authorizedClientManager) throws Exception {
//		return httpServiceProxyFactory(authorizedClientManager).createClient(WelcomeClient.class);
//	}
//
//	/**
//	 * 创建HttpServiceProxyFactory,以便在创建客户端时使用
//	 *
//	 * @param authorizedClientManager OAuth2AuthorizedClientManager实例
//	 * @return 创建的 HttpServiceProxyFactory 实例
//	 */
//	private HttpServiceProxyFactory httpServiceProxyFactory(OAuth2AuthorizedClientManager authorizedClientManager) {
//		// 创建 ServletOAuth2AuthorizedClientExchangeFilterFunction,使用它来处理OAuth2认证
//		ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
//				new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
//
//		// 设置默认的OAuth2授权客户端
//		oauth2Client.setDefaultOAuth2AuthorizedClient(true);
//
//		// 创建WebClient,应用OAuth2认证配置
//		WebClient webClient = WebClient.builder().apply(oauth2Client.oauth2Configuration()).build();
//
//		// 创建WebClientAdapter,允许在创建客户端时使用WebClient
//		WebClientAdapter client = WebClientAdapter.forClient(webClient);
//
//		// 创建HttpServiceProxyFactory,用于创建客户端
//		return HttpServiceProxyFactory.builder(client).build();
//	}
//
//	/**
//	 * 创建OAuth2AuthorizedClientManager
//	 *
//	 * @param clientRegistrationRepository 用于管理客户端注册信息的实例
//	 * @param authorizedClientRepository 用于管理授权客户端信息的实例
//	 * @return OAuth2AuthorizedClientManager
//	 */
//	@Bean
//	public OAuth2AuthorizedClientManager authorizedClientManager(
//			ClientRegistrationRepository clientRegistrationRepository,
//			OAuth2AuthorizedClientRepository authorizedClientRepository) {
//
//		// 创建OAuth2AuthorizedClientProvider,用于获取授权客户端
//		OAuth2AuthorizedClientProvider authorizedClientProvider =
//				OAuth2AuthorizedClientProviderBuilder.builder().authorizationCode().refreshToken().build();
//
//		// 创建DefaultOAuth2AuthorizedClientManager,使用它来管理授权客户端
//		DefaultOAuth2AuthorizedClientManager authorizedClientManager =
//				new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
//
//		// 设置授权客户端提供程序
//		authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
//
//		// 返回OAuth2AuthorizedClientManager
//		return authorizedClientManager;
//	}
//}