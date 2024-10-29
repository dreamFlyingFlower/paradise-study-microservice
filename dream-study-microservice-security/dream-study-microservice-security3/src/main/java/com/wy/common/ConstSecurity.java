package com.wy.common;

/**
 * 固定配置
 * 
 * @auther 飞花梦影
 * @date 2019-09-23 00:10:52
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface ConstSecurity {

	/** 超级管理员角色编码 */
	String SUPER_ADMIN = "SUPER_ADMIN";

	/** 验证码放入session时的前缀 */
	String SESSION_KEY_PREFIX = "SESSION_KEY_FOR_CODE_";

	/** 当请求需要身份认证时,默认跳转的url */
	String DEFAULT_UNAUTHENTICATION_URL = "/authentication/require";

	/** 默认登录页面 */
	String DEFAULT_LOGIN_PAGE_URL = "/signIn.html";

	/** 验证图片验证码时,http请求中默认的携带图片验证码信息的参数的名称 */
	String DEFAULT_PARAMETER_NAME_CODE_IMAGE = "image";

	/** 验证短信验证码时,http请求中默认的携带短信验证码信息的参数的名称 */
	String DEFAULT_PARAMETER_NAME_CODE_SMS = "sms";

	/** 发送短信验证码 或 验证短信验证码时,传递手机号的参数的名称 */
	String DEFAULT_PARAMETER_NAME_MOBILE = "mobile";

	/** session失效默认的跳转地址 */
	String DEFAULT_SESSION_INVALID_URL = "/session/invalid";

	/** 请求头中的Authorization字段 */
	String HEADER_AUTHORIZATION = "Authorization";

	/** 请求头中的Authorization字段值的前缀 */
	String HEADER_AUTHORIZATION_BEARER = "Bearer ";

	/** 随机字符串请求头名字 */
	String NONCE_HEADER_NAME = "nonce";

	/** 登录方式入参名 */
	String LOGIN_TYPE_NAME = "loginType";

	/** 验证码id入参名 */
	String CAPTCHA_ID_NAME = "captchaId";

	/** 验证码值入参名 */
	String CAPTCHA_CODE_NAME = "code";

	/** 登录方式-短信验证码 */
	String SMS_LOGIN_TYPE = "smsCaptcha";

	/** 登录方式-账号密码登录 */
	String PASSWORD_LOGIN_TYPE = "passwordLogin";

	/** 权限在token中的key */
	String AUTHORITIES_KEY = "authorities";

	/**
	 * 自定义grant type-短信验证码
	 */
	String GRANT_TYPE_SMS_CODE = "urn:ietf:params:oauth:grant-type:sms_code";

	/**
	 * 自定义grant type-短信验证码-手机号的key
	 */
	String OAUTH_PARAMETER_NAME_PHONE = "phone";

	/**
	 * 自定义grant type-短信验证码-短信验证码的key
	 */
	String OAUTH_PARAMETER_NAME_SMS_CAPTCHA = "sms_captcha";
}