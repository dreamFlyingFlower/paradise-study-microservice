package com.wy.qrcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 扫描二维码入参
 *
 * @author 飞花梦影
 * @date 2024-11-04 14:29:31
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeLoginScanRequest {

	/**
	 * 二维码id
	 */
	private String qrCodeId;
}