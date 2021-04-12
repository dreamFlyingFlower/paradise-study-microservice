package com.wy.verify;

public interface VerifyType {

	/**
	 * 返回验证的类型,需要由前端传回
	 * @return 验证类型,图片或短信或其他自定义
	 */
	String getVerifyType();

	/**
	 * 处理验证类型的类在spring环境中的name
	 * @return name
	 */
	String getHandlerName();

	/**
	 * 处理验证码类型的类文件
	 * @return
	 */
	Class<? extends VerifyHandler> getHandlerClass();

	/**
	 * 生成验证码的类在spring环境中的name
	 * @return name
	 */
	String getObtainVerify();
}