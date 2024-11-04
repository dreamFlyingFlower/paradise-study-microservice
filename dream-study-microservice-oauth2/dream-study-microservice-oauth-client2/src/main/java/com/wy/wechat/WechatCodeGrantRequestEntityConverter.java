package com.wy.wechat;

import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.util.MultiValueMap;

import dream.flying.flower.framework.security.constant.ConstOAuthClient;

/**
 * 微信自定义根据code获取token
 * 
 * <pre>
 * appid:公众号的唯一标识,相当于OAuth2的client_id
 * secret:公众号的appsecret,相当于OAuth2的client_secret
 * code:填写第一步获取的code参数
 * grant_type:固定为authorization_code
 * </pre>
 * 
 * 微信授权获取code,参数的传递顺序也要注意,否则无法访问
 * 
 * <pre>
 * 在确保微信公众账号拥有授权作用域(scope参数)的权限的前提下(已认证服务号,默认拥有scope参数中的snsapi_base和snsapi_userinfo 权限)，引导关注者打开如下页面:
 * https://open.weixin.qq.com/connect/oauth2/authorize?appid=&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect
 * REDIRECT_URI需要进行URL编码
 * 
 * 若提示“该链接无法访问”,请检查参数是否填写错误,是否拥有scope参数对应的授权作用域权限
 * 由于授权操作安全等级较高,所以在发起授权请求时,微信会对授权链接做正则强匹配校验,如果链接的参数顺序不对,授权页面将无法正常访问
 * 
 * 参考链接(请在微信客户端中打开此链接体验):
 * scope为snsapi_base:
 * https://open.weixin.qq.com/connect/oauth2/authorize?appid=&redirect_uri=&response_type=code&scope=snsapi_base&state=123#wechat_redirect
 * scope为snsapi_userinfo:
 * https://open.weixin.qq.com/connect/oauth2/authorize?appid=&redirect_uri=&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect
 * </pre>
 *
 * @author 飞花梦影
 * @date 2024-11-04 09:23:38
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class WechatCodeGrantRequestEntityConverter extends OAuth2AuthorizationCodeGrantRequestEntityConverter {

	@Override
	protected MultiValueMap<String, String>
			createParameters(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
		// 如果是微信登录,获取token时携带appid参数与secret参数
		MultiValueMap<String, String> parameters = super.createParameters(authorizationCodeGrantRequest);
		String registrationId = authorizationCodeGrantRequest.getClientRegistration().getRegistrationId();
		if (ConstOAuthClient.OAUTH2_CLIENT_LOGIN_WECHAT.equals(registrationId)) {
			// 如果当前是微信登录,携带appid和secret
			parameters.add(ConstOAuthClient.OAUTH2_CLIENT_WECHAT_PARAMETER_APPID,
					authorizationCodeGrantRequest.getClientRegistration().getClientId());
			parameters.add(ConstOAuthClient.OAUTH2_CLIENT_WECHAT_PARAMETER_SECRET,
					authorizationCodeGrantRequest.getClientRegistration().getClientSecret());
		}
		return parameters;
	}
}