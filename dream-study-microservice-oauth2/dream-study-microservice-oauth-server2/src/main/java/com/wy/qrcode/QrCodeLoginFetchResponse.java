package com.wy.qrcode;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前端根据二维码id轮询二维码状态时返回二维码状态,如果已扫描也会返回扫描者的头像、昵称
 *
 * @author 飞花梦影
 * @date 2024-11-04 14:28:39
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeLoginFetchResponse {

	/**
	 * 二维码状态 0:待扫描,1:已扫描,2:已确认
	 */
	private Integer qrCodeStatus;

	/**
	 * 是否已过期
	 */
	private Boolean expired;

	/**
	 * 扫描人头像
	 */
	private String avatarUrl;

	/**
	 * 扫描人昵称
	 */
	private String name;

	/**
	 * 待确认scope
	 */
	private Set<String> scopes;

	/**
	 * 跳转登录之前请求的接口
	 */
	private String beforeLoginRequestUri;

	/**
	 * 跳转登录之前请求参数
	 */
	private String beforeLoginQueryString;
}