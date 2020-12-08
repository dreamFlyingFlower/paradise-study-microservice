package com.wy.social.qq;

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

import com.wy.entity.UserQq;
import com.wy.social.IToken;

/**
 * @apiNote spring发送服务的一系列流程,从而获得服务端提供的token
 * @author ParadiseWY
 * @date 2019年9月23日
 */
public class QqProvider extends AbstractOAuth2ServiceProvider<IToken<UserQq>> {

	// 是一个固定的appId,由第三方服务发放
	private String appId;

	// 引到用户跳转的地址,获得Authorization Code
	private static final String URL_AUTHORIZE = "https://graph.qq.com/oauth2.0/authorize";

	// 通过code获得accesstoken的url地址
	private static final String URL_ACCESS_TOKEN = "https://graph.qq.com/oauth2.0/token";

	/**
	 * 利用springsocial提供的template发送请求,获得token.默认的是OAuth2Template
	 * 
	 * @param appId 固定appId
	 * @param appSecret 申请app时给的密码
	 */
	public QqProvider(String appId, String appSecret) {
		super(new QqTemplate(appId, appSecret, URL_AUTHORIZE, URL_ACCESS_TOKEN));
		this.appId = appId;
	}

	@Override
	public IToken<UserQq> getApi(String accessToken) {
		return new QqTokenService(accessToken, appId);
	}
}