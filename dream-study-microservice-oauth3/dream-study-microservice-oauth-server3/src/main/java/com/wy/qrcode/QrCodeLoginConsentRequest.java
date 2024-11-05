package com.wy.qrcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 确认登录时传入二维码id和上一步生成的临时票据防篡改
 *
 * @author 飞花梦影
 * @date 2024-11-04 14:30:29
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeLoginConsentRequest {

	/**
	 * 二维码id
	 */
	private String qrCodeId;

	/**
	 * 扫码二维码后产生的临时票据(仅一次有效)
	 */
	private String qrCodeTicket;
}