package com.wy.social.qq;

import java.util.HashMap;

import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import com.alibaba.fastjson2.JSON;
import com.wy.entity.UserQq;
import com.wy.social.IToken;

/**
 * 模拟一个企鹅的OAuth2登录,从QQ获取用户信息.需要在企鹅互联开放平台->企鹅开放平台->文档资料->api文档查看api
 * 
 * 利用OAuth2协议进行第三方登录,服务提供商需要一个认证服务器和一个资源服务器,登录流程:
 * 
 * <pre>
 * 第三方client请求服务提供商的认证服务器
 * ->认证服务器向资源所有者发出认证请求,资源拥有者同意授权
 * ->认证服务器将授权码返回给client->client再向服务器申请令牌
 * ->服务器令牌给client
 * ->client获取资源拥有者信息
 * </pre>
 * 
 * AbstractOAuth2ApiBinding:向服务端发送认证请求,该类中有一个accessToken,是验证时由服务提供商发放,
 * 并在后续的访问中一直由客户端持有,若是accessToken有过期时间,则需要定时访问认证服务器,防止访问过期
 * 
 * 由于accessToken是final修饰,所以本类必须是多例的,而不可以是单例的,否则会出现多线程问题
 * 
 * @auther 飞花梦影
 * @date 2019-09-23 23:37:09
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class QqOAuth2ApiBinding extends AbstractOAuth2ApiBinding implements IToken<UserQq> {

	/** 获取鹅厂openId的url地址,需要传入一个access_token */
	private static final String URL_GET_OPENID = "https://graph.qq.com/oauth2.0/me?access_token=%s";

	/** 获取鹅厂用户信息的url地址,accesstoken会由下面的构造方法中传入 */
	private static final String URL_GET_USERINFO =
			"https://graph.qq.com/user/get_user_info?oauth_consumer_key=%s&openid=%s";

	/** 申请鹅厂的第三方app登录成功后,分配给app的appid,即在注册自己的应用时鹅厂给的appId */
	private String appId;

	/** 利用access_token获取的openId */
	private String openId;

	/**
	 * 初始化时访问认证服务器,获得相应资源进行token绑定
	 * 
	 * @param accessToken token
	 * @param openId 需要获得的openid
	 */
	public QqOAuth2ApiBinding(String accessToken, String appId) {
		// token的使用方式,可以放在请求头中,或以access_token为key拼接在url中,或以oauth_token为key拼接在url中
		super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
		this.appId = appId;
		String result = getRestTemplate().getForObject(String.format(URL_GET_OPENID, accessToken), String.class);
		this.openId = JSON.parseObject(result, HashMap.class).get("openid").toString();
	}

	/**
	 * 在获得必要的访问服务器资源服务器的参数时,可以获得资源所有者(用户)的信息
	 */
	@Override
	public UserQq getUserInfo() {
		UserQq penguin = getRestTemplate()
				.getForEntity(String.format(URL_GET_USERINFO, appId, openId), UserQq.class, new Object[] {}).getBody();
		penguin.setOpenId(openId);
		return penguin;
	}
}