package com.wy.common;

/**
 * 固定配置
 * 
 * @auther 飞花梦影
 * @date 2019-09-23 00:10:52
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface Constants {

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
}