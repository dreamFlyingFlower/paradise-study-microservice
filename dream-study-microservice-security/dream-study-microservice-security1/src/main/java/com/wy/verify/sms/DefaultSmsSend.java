package com.wy.verify.sms;

/**
 * @apiNote 默认短信验证码发送
 * @author ParadiseWY
 * @date 2019年9月24日
 */
public class DefaultSmsSend implements SmsSend {

	@Override
	public void sendSms(String mobile, String code) {
		System.out.println("向手机"+mobile+"发送短信验证码"+code);
	}
}