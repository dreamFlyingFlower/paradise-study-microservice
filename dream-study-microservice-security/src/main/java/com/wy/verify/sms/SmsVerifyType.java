package com.wy.verify.sms;

import org.springframework.context.annotation.Configuration;

import com.wy.verify.VerifyHandler;
import com.wy.verify.VerifyType;

/**
 * @apiNote 短信验证
 * @author ParadiseWY
 * @date 2019年9月29日 下午9:34:43
 */
@Configuration
public class SmsVerifyType implements VerifyType {

	@Override
	public String getVerifyType() {
		return "sms";
	}

	@Override
	public String getHandlerName() {
		return "smsVerifyHandler";
	}

	@Override
	public Class<? extends VerifyHandler> getHandlerClass() {
		return SmsVerifyHandler.class;
	}

	@Override
	public String getObtainVerify() {
		return "smsVerifyGenerator";
	}
}