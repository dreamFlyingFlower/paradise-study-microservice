package com.wy.social;

/**
 * 获得需要登录的第三方的用户信息,登录拦截social的流程:
 * 
 * <pre>
 * SocialAuthenticationFilter
 * ->SocialAuthentiationService(OAuth2AuthenticationService)->ConnnectionFactory
 * ->Authentication(SocialAuthenticationToken)->AuthenticationManager(ProviderManager)
 * ->AuthenticationProvider(SocialAuthenticationProvider)->UsersConnectionRepository(JdbcUsersConnectionRepository)
 * ->SocialUserDetailsService->SocialUserDetails->Authentication(SocialAuthenticationToken)
 * </pre>
 * 
 * @auther 飞花梦影
 * @date 2019-09-23 21:16:40
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface IToken<T> {

	T getUserInfo();
}