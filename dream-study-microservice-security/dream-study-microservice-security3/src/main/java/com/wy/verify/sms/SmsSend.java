package com.wy.verify.sms;

/**
 * @apiNote 短信发送,多渠道
 * @author ParadiseWY
 * @date 2019年9月24日 下午10:17:02
 */
public interface SmsSend {

	/**
	 * 发送验证码
	 * @param mobile 手机号
	 * @param code 验证码
	 */
	void sendSms(String mobile, String code);
}