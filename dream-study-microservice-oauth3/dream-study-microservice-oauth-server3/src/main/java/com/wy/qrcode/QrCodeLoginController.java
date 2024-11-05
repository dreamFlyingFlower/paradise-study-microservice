package com.wy.qrcode;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dream.flying.flower.result.Result;
import lombok.AllArgsConstructor;

/**
 * 二维码登录接口
 *
 * @author 飞花梦影
 * @date 2024-11-04 14:24:59
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RestController
@AllArgsConstructor
@RequestMapping("/qrCode")
public class QrCodeLoginController {

	private final IQrCodeLoginService iQrCodeLoginService;

	@GetMapping("/login/generateQrCode")
	public Result<QrCodeGenerateResponse> generateQrCode() {
		// 生成二维码
		return Result.ok(iQrCodeLoginService.generateQrCode());
	}

	@GetMapping("/login/fetch/{qrCodeId}")
	public Result<QrCodeLoginFetchResponse> fetch(@PathVariable String qrCodeId) {
		// 轮询二维码状态
		return Result.ok(iQrCodeLoginService.fetch(qrCodeId));
	}

	@PostMapping("/scan")
	public Result<QrCodeLoginScanResponse> scan(@RequestBody QrCodeLoginScanRequest loginScan) {
		// app 扫码二维码
		return Result.ok(iQrCodeLoginService.scan(loginScan));
	}

	@PostMapping("/consent")
	public Result<String> consent(@RequestBody QrCodeLoginConsentRequest loginConsent) {

		// app 确认登录
		iQrCodeLoginService.consent(loginConsent);

		return Result.ok();
	}
}