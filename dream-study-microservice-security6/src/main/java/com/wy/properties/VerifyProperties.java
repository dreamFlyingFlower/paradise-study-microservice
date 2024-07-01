package com.wy.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * 验证类型集合类
 *
 * @author 飞花梦影
 * @date 2024-07-01 23:38:44
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
public class VerifyProperties {

	/**
	 * 是否开启各种验证
	 */
	private boolean enabled = false;

	private VerifyImageProperties image = new VerifyImageProperties();

	private VerifySmsProperties sms = new VerifySmsProperties();

	private VerifyMobileProperties mobile = new VerifyMobileProperties();
}