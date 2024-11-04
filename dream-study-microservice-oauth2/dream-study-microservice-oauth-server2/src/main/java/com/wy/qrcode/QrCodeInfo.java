package com.wy.qrcode;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 生成二维码时生成的数据bean,存入redis中,等到前端轮询或app端操作时使用
 *
 * @author 飞花梦影
 * @date 2024-11-04 14:30:54
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeInfo {

	/**
	 * 二维码id
	 */
	private String qrCodeId;

	/**
	 * 二维码状态 0:待扫描，1:已扫描，2:已确认
	 */
	private Integer qrCodeStatus;

	/**
	 * 二维码过期时间
	 */
	private LocalDateTime expiresTime;

	/**
	 * 扫描人头像
	 */
	private String avatarUrl;

	/**
	 * 扫描人昵称
	 */
	private String name;

	/**
	 * 待确认的scope
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