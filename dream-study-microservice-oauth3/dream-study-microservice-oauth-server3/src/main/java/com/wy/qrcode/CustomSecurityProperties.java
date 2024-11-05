package com.wy.qrcode;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2024-11-04 14:35:22
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@ConfigurationProperties("custom.security")
public class CustomSecurityProperties {

	/**
	 * 登录页面路径
	 */
	private String loginUrl;

	/**
	 * 授权确认页面路径
	 */
	private String consentPageUr;

	/**
	 * 设备码验证页面
	 */
	private String deviceActivateUri;

	/**
	 * 设备码验证成功页面
	 */
	private String deviceActivatedUri;

	/**
	 * 不需要认证的地址
	 */
	private List<String> ignoreUriList;
}