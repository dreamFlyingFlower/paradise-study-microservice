package com.wy.qrcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 二维码响应
 *
 * @author 飞花梦影
 * @date 2024-11-04 14:27:59
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeGenerateResponse {

	/**
	 * 二维码id
	 */
	private String qrCodeId;

	/**
	 * 二维码base64值(这里响应一个链接好一些)
	 */
	private String imageData;
}