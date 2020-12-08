package com.wy.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @apiNote 验证类型集合类
 * @author ParadiseWY
 * @date 2019年9月23日
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