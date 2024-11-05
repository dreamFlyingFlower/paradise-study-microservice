package com.wy.qrcode;

/**
 * 二维码登录服务接口
 *
 * @author 飞花梦影
 * @date 2024-11-04 14:27:02
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface IQrCodeLoginService {

	/**
	 * 生成二维码
	 *
	 * @return 二维码
	 */
	QrCodeGenerateResponse generateQrCode();

	/**
	 * 扫描二维码响应
	 *
	 * @param loginScan 二维码id
	 * @return 二维码信息
	 */
	QrCodeLoginScanResponse scan(QrCodeLoginScanRequest loginScan);

	/**
	 * 二维码登录确认入参
	 *
	 * @param loginConsent 二维码id
	 */
	void consent(QrCodeLoginConsentRequest loginConsent);

	/**
	 * web端轮询二维码状态处理
	 *
	 * @param qrCodeId 二维码id
	 * @return 二维码信息
	 */
	QrCodeLoginFetchResponse fetch(String qrCodeId);
}