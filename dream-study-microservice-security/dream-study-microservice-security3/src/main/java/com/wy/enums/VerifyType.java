package com.wy.enums;

import lombok.Getter;

/**
 * 校验类型
 * 
 * @auther 飞花梦影
 * @date 2021-06-22 23:33:46
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
public enum VerifyType {

	/** 验证码 */
	CODE,

	/** 手机短信 */
	SMS;

	public static final boolean check(String verifyType) {
		for (VerifyType item : VerifyType.values()) {
			if (item.name().equalsIgnoreCase(verifyType)) {
				return true;
			}
		}
		return false;
	}
}