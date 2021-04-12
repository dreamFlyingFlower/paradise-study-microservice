package com.wy.common;

/**
 * @apiNote 请求来源,来源分类
 * @author ParadiseWY
 * @date 2019年9月29日 下午9:13:28
 */
public enum RequestSource {

	/**
	 * 安卓手机
	 */
	MOBILE_ANDROID("mobile", "android"),
	/**
	 * ios手机
	 */
	MOBILE_IOS("mobile", "ios"),
	/**
	 * windows系统pc电脑
	 */
	PC_WINDOWS("pc", "windows"),
	/**
	 * mac系统pc电脑
	 */
	PC_MAC("pc", "mac"),
	/**
	 * linux系统pc电脑
	 */
	PC_LINUX("pc", "linux");

	/**
	 * 请求来源类型,手机或pc电脑
	 */
	private String sourceType;

	/**
	 * 操作系统
	 */
	private String category;

	private RequestSource(String sourceType, String category) {
		this.sourceType = sourceType;
		this.category = category;
	}

	public String getSourceType() {
		return sourceType;
	}

	public String getCategory() {
		return category;
	}
}