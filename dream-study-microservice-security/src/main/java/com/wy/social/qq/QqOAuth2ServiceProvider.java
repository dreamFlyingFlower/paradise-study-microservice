package com.wy.social.qq;

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

import com.wy.entity.UserQq;
import com.wy.social.IToken;

/**
 * spring发送服务的一系列流程,从服务提供商获取提供的access_token
 * 
 * @auther 飞花梦影
 * @date 2019-09-23 23:33:22
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class QqOAuth2ServiceProvider extends AbstractOAuth2ServiceProvider<IToken<UserQq>> {

	/** 申请鹅厂的第三方app登录成功后,分配给app的appid,即在注册自己的应用时鹅厂给的appId */
	private String appId;

	/** 引到用户跳转的地址,获得Authorization Code */
	private static final String URL_AUTHORIZE = "https://graph.qq.com/oauth2.0/authorize";

	/** 通过code获得access_token的url地址 */
	private static final String URL_ACCESS_TOKEN = "https://graph.qq.com/oauth2.0/token";

	/**
	 * 利用springsocial提供的template发送请求,获得token.默认的是OAuth2Template
	 * 
	 * @param appId 固定appId
	 * @param appSecret 申请app时给的密码
	 */
	public QqOAuth2ServiceProvider(String appId, String appSecret) {
		super(new QqTemplate(appId, appSecret, URL_AUTHORIZE, URL_ACCESS_TOKEN));
		this.appId = appId;
	}

	@Override
	public IToken<UserQq> getApi(String accessToken) {
		return new QqOAuth2ApiBinding(accessToken, appId);
	}
}