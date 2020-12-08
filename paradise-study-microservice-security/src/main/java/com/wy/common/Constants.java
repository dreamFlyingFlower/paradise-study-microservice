package com.wy.common;

/**
 * @apiNote 固定配置
 * @author ParadiseWY
 * @date 2019年9月23日
 */
public class Constants {

	/**
	 * 验证码放入session时的前缀
	 */
	public static final String SESSION_KEY_PREFIX = "SESSION_KEY_FOR_CODE_";

	/**
	 * 当请求需要身份认证时，默认跳转的url
	 */
	public static final String DEFAULT_UNAUTHENTICATION_URL = "/authentication/require";

	/**
	 * 默认登录页面
	 * @see SecurityController
	 */
	public static final String DEFAULT_LOGIN_PAGE_URL = "/imooc-signIn.html";

	/**
	 * 验证图片验证码时，http请求中默认的携带图片验证码信息的参数的名称
	 */
	public static final String DEFAULT_PARAMETER_NAME_CODE_IMAGE = "image";

	/**
	 * 验证短信验证码时，http请求中默认的携带短信验证码信息的参数的名称
	 */
	public static final String DEFAULT_PARAMETER_NAME_CODE_SMS = "sms";

	/**
	 * 发送短信验证码 或 验证短信验证码时，传递手机号的参数的名称
	 */
	public static final String DEFAULT_PARAMETER_NAME_MOBILE = "mobile";

	/**
	 * session失效默认的跳转地址
	 */
	public static final String DEFAULT_SESSION_INVALID_URL = "/session/invalid";
}