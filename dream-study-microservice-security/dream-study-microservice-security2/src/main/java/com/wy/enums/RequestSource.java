package com.wy.enums;

import lombok.Getter;

/**
 * 请求来源.如PC,Android,IOS等
 * 
 * @auther 飞花梦影
 * @date 2019-09-29 23:31:39
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
public enum RequestSource {

	/** 安卓手机 */
	MOBILE_ANDROID("mobile", "android"),

	/** ios手机 */
	MOBILE_IOS("mobile", "ios"),

	/** windows系统pc电脑 */
	PC_WINDOWS("pc", "windows"),

	/** mac系统pc电脑 */
	PC_MAC("pc", "mac"),

	/** linux系统pc电脑 */
	PC_LINUX("pc", "linux");

	/** 请求来源类型,手机或pc电脑 */
	private String source;

	/** 操作系统 */
	private String os;

	private RequestSource(String source, String os) {
		this.source = source;
		this.os = os;
	}
}