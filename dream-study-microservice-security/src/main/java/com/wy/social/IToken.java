package com.wy.social;

/**
 * @apiNote 获得需要登录的第三方的用户信息,登录拦截social的流程:SocialAuthenticationFilter
 *          ->SocialAuthentiationService(OAuth2AuthenticationService)->ConnnectionFactory
 *          ->Authentication(SocialAuthenticationToken)->AuthenticationManager(ProviderManager)
 *          ->AuthenticationProvider(SocialAuthenticationProvider)->UsersConnectionRepository(JdbcUsersConnectionRepository)
 *          ->SocialUserDetailsService->SocialUserDetails->Authentication(SocialAuthenticationToken)
 * @author ParadiseWY
 * @date 2019年9月23日
 */
public interface IToken<T> {

	T getUserInfo();
}