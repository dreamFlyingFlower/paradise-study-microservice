package com.wy.qrcode;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 扫描二维码时生成一个临时票据返回,同时返回scope和二维码状态
 *
 * @author 飞花梦影
 * @date 2024-11-04 14:29:56
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeLoginScanResponse {

	/**
	 * 扫描临时票据
	 */
	private String qrCodeTicket;

	/**
	 * 二维码状态
	 */
	private Integer qrCodeStatus;

	/**
	 * 是否已过期
	 */
	private Boolean expired;

	/**
	 * 待确认scope
	 */
	private Set<String> scopes;
}