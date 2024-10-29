package com.wy.properties;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @apiNote 短信验证码自定义配置信息
 * @author ParadiseWY
 * @date 2019年9月23日
 */
@Getter
@Setter
public class VerifySmsProperties {
	
	/**
	 * 是否开启短信验证,默认不开启
	 */
	private Boolean verifySms = false;

	/**
	 * 验证码长度
	 */
	private int length = 6;

	/**
	 * 默认过期时间,秒
	 */
	private int expireSeconds = 60;

	/**
	 * 需要进行验证的url
	 */
	private List<String> urls;
}