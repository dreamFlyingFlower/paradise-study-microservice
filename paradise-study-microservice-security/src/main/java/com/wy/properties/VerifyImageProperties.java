package com.wy.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @apiNote 页面验证图片校验信息
 * @author ParadiseWY
 * @date 2019年9月23日
 */
@Getter
@Setter
public class VerifyImageProperties extends VerifySmsProperties {

	public VerifyImageProperties() {
		setLength(4);
	}

	private int width = 67;

	private int height = 23;
}