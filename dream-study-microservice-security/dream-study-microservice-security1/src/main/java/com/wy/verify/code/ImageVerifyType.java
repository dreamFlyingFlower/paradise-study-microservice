package com.wy.verify.code;

import org.springframework.context.annotation.Configuration;

import com.wy.verify.VerifyHandler;
import com.wy.verify.VerifyInfo;

/**
 * @apiNote 图片验证类型
 * @author ParadiseWY
 * @date 2019年9月29日 下午9:29:18
 */
@Configuration
public class ImageVerifyType implements VerifyInfo {

	@Override
	public String getVerifyType() {
		return "image";
	}

	@Override
	public String getHandlerName() {
		return "imageVerifyHandler";
	}

	@Override
	public Class<? extends VerifyHandler> getHandlerClass() {
		return ImageVerifyHandler.class;
	}

	@Override
	public String getObtainVerify() {
		return "imageVerifyGenerator";
	}
}