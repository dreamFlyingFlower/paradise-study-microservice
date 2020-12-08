package com.wy.social.qq;

import java.util.HashMap;

import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import com.alibaba.fastjson.JSON;
import com.wy.entity.UserQq;
import com.wy.social.IToken;

/**
 * @apiNote 模拟一个企鹅的oauth2登录,需要在企鹅互联开放平台->企鹅开放平台->文档资料->api文档查看api
 * @apiNote 利用oauth2协议进行第三方登录,服务器端需要一个认证服务器和一个资源服务器,登录流程:
 *          第三方client请求服务端的认证服务器->认证服务器向资源所有者发出认证请求,资源拥有者同意授权->
 *          认证服务器将授权码返回给client->client再向服务器申请令牌->服务器令牌给client->client获取资源拥有者信息
 * @apiNote AbstractOAuth2ApiBinding:向服务端发送认证请求,该类中有一个accessToken,是验证时由服务器产生,
 *          并在后续的访问中一直由客户端持有,若是accessToken有过期时间,则需要定时访问认证服务器,防止访问过期
 * @apiNote 由于accessToken是final修饰,所以本类必须是多例的,而不可以是单例的,否则会出现多线程问题
 * @author ParadiseWY
 * @date 2019年9月23日
 */
public class QqTokenService extends AbstractOAuth2ApiBinding implements IToken<UserQq> {

	// 获取鹅厂openId的url地址,需要传入一个access_token
	private static final String URL_GET_OPENID = "https://graph.qq.com/oauth2.0/me?access_token=%s";

	// 获取鹅厂用户信息的url地址,accesstoken会由下面的构造方法中传入
	private static final String URL_GET_USERINFO = "https://graph.qq.com/user/get_user_info?"
			+ "oauth_consumer_key=%s&openid=%s";

	// 申请鹅厂的第三方app登录成功后,分配给app的appid,即在注册自己的应用时鹅厂给的appId
	private String appId;

	// 自己申请的openId
	private String openId;

	/**
	 * 初始化时访问认证服务器,获得相应资源进行token绑定
	 * 
	 * @param accessToken token
	 * @param openId 需要获得的openid
	 */
	public QqTokenService(String accessToken, String appId) {
		// token的使用方式,是放在请求头中,还是放在参数中,此处放在参数中进行拼接
		super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
		this.appId = appId;
		String url = String.format(URL_GET_OPENID, accessToken);
		String result = getRestTemplate().getForObject(url, String.class);
		this.openId = JSON.parseObject(result, HashMap.class).get("openid").toString();
	}

	/**
	 * 在获得必要的访问服务器资源服务器的参数时,可以获得资源所有者(用户)的信息
	 */
	@Override
	public UserQq getUserInfo() {
		UserQq penguin = getRestTemplate()
				.getForEntity(String.format(URL_GET_USERINFO, appId, openId), UserQq.class,
						new Object[] {})
				.getBody();
		return penguin;
	}
}