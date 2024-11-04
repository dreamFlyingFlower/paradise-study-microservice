package com.wy.wechat;

import java.util.Objects;
import java.util.function.Consumer;

import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import dream.flying.flower.framework.security.constant.ConstOAuthClient;

/**
 * 自定义微信授权解析,获取code,微信的OAuth2和标准的OAuth2参数不一样,需要自定义解析
 * 
 * <pre>
 * appid:微信公众号的唯一标识,相当于标准OAuth2的client_id
 * redirect_ur:授权后重定向的回调链接地址, 请使用urlEncode对链接进行处理
 * response_type:返回类型,请填写code
 * scope:作用域.snsapi_base:不弹出授权页,直接跳转,只能获取用户openid;snsapi_userinfo:弹出授权页,可通过openid拿到昵称,性别等.并且,即使在未关注的情况下,只要用户授权,也能获取其信息
 * state:重定向后会带上state参数,开发者可以填写a-zA-Z0-9的参数值,最多128字节
 * forcePopup:强制此次授权需要用户弹窗确认,默认为false;若用户命中了特殊场景下的静默授权逻辑,则此参数不生效
 * </pre>
 * 
 * {@link DefaultOAuth2AuthorizationRequestResolver}:默认的OAuth2请求解析
 * 
 * @author 飞花梦影
 * @date 2024-11-04 09:14:24
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class WechatAuthorizationRequestConsumer implements Consumer<OAuth2AuthorizationRequest.Builder> {

	@Override
	public void accept(OAuth2AuthorizationRequest.Builder builder) {
		OAuth2AuthorizationRequest authorizationRequest = builder.build();
		Object registrationId = authorizationRequest.getAttribute(OAuth2ParameterNames.REGISTRATION_ID);
		if (Objects.equals(registrationId, ConstOAuthClient.OAUTH2_CLIENT_LOGIN_WECHAT)) {
			// 判断是否微信登录,如果是微信登录则将appid添加至请求参数中
			builder.additionalParameters(
					(params) -> params.put(ConstOAuthClient.OAUTH2_CLIENT_WECHAT_PARAMETER_FORCE_POPUP, true));
			builder.additionalParameters((params) -> params.put(ConstOAuthClient.OAUTH2_CLIENT_WECHAT_PARAMETER_APPID,
					authorizationRequest.getClientId()));

			builder.parameters(params -> {
				params.clear();

				// 重置授权申请参数顺序
				params.put(ConstOAuthClient.OAUTH2_CLIENT_WECHAT_PARAMETER_APPID, authorizationRequest.getClientId());
				params.put(OAuth2ParameterNames.REDIRECT_URI, authorizationRequest.getRedirectUri());
				params.put(OAuth2ParameterNames.RESPONSE_TYPE, authorizationRequest.getResponseType().getValue());
				params.put(OAuth2ParameterNames.SCOPE, String.join(" ", authorizationRequest.getScopes()));
				params.put(OAuth2ParameterNames.STATE, authorizationRequest.getState());
				params.put(OAuth2ParameterNames.CLIENT_ID, authorizationRequest.getClientId());
				params.put(ConstOAuthClient.OAUTH2_CLIENT_WECHAT_PARAMETER_FORCE_POPUP, true);
			});
		}
	}
}