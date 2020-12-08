package com.wy.social.wx;

import java.util.HashMap;

import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import com.alibaba.fastjson.JSON;
import com.wy.entity.UserWeiXin;
import com.wy.social.IToken;

/**
 * @apiNote
 * @author ParadiseWY
 * @date 2019年9月26日
 */
public class WxTokenService extends AbstractOAuth2ApiBinding implements IToken<UserWeiXin> {

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
	public WxTokenService(String accessToken, String appId) {
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
	public UserWeiXin getUserInfo() {
		UserWeiXin penguin = getRestTemplate().getForEntity(String.format(URL_GET_USERINFO, appId, openId),
				UserWeiXin.class, new Object[] {}).getBody();
		return penguin;
	}
}