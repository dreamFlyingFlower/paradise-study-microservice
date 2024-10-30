package com.wy.oauth.grant;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

/**
 * 自定义手机号授权模式,可参考{@link ResourceOwnerPasswordTokenGranter}
 * 
 * {@link TokenGranter}:授权模式接口
 * 
 * {@link AbstractTokenGranter}:授权模式抽象类,实现了部分授权模式方法
 * 
 * SpringSecurityOauth2会根据传入的grant_type,来将请求转发到对应的Granter进行处理,而用户信息合法性的校验交给authenticationManager处理.
 * authenticationManager不直接进行认证,而是通过委托模式,将认证任务委托给AuthenticationProvider接口的实现类来完成,一个AuthenticationProvider就对应一个认证方式
 *
 * @author 飞花梦影
 * @date 2024-10-24 14:57:02
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Deprecated
public class SmsTokenGranter extends AbstractTokenGranter {

	/** 授权类型名称 */
	public static final String GRANT_TYPE = "phonecode";

	private final AuthenticationManager authenticationManager;

	/**
	 * 构造函数
	 * 
	 * @param tokenServices
	 * @param clientDetailsService
	 * @param requestFactory
	 * @param authenticationManager
	 */
	public SmsTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
			OAuth2RequestFactory requestFactory, AuthenticationManager authenticationManager) {
		this(tokenServices, clientDetailsService, requestFactory, authenticationManager, GRANT_TYPE);
	}

	public SmsTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
			OAuth2RequestFactory requestFactory, AuthenticationManager authenticationManager, String grantType) {
		super(tokenServices, clientDetailsService, requestFactory, grantType);
		this.authenticationManager = authenticationManager;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		// 获取参数
		String phone = parameters.get("phone");
		String phonecode = parameters.get("phoneCode");
		// 创建未认证对象
		Authentication authentication = new SmsAuthenticationToken(phone, phonecode);
		((AbstractAuthenticationToken) authentication).setDetails(parameters);
		try {
			// 进行身份认证
			authentication = authenticationManager.authenticate(authentication);
		} catch (AccountStatusException ase) {
			// 将过期、锁定、禁用的异常统一转换
			throw new InvalidGrantException(ase.getMessage());
		} catch (BadCredentialsException e) {
			// 用户名/密码错误，我们应该发送400/invalid grant
			throw new InvalidGrantException(e.getMessage());
		}
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new InvalidGrantException("用户认证失败: " + phone);
		}

		OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
		return new OAuth2Authentication(storedOAuth2Request, authentication);
	}
}