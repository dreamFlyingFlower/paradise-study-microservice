package com.wy.verify.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.ServletWebRequest;

import com.wy.properties.UserProperties;
import com.wy.util.RandomTool;
import com.wy.verify.VerifyEntity;
import com.wy.verify.VerifyGenerator;

/**
 * @apiNote 短信验证
 * @author ParadiseWY
 * @date 2019年9月24日
 */
public class SmsVerifyGenerator implements VerifyGenerator {

	@Autowired
	private UserProperties userProperties;

	@Override
	public VerifyEntity generateVerify(ServletWebRequest request) {
		// 模拟短信验证码
		String code = RandomTool.randomNumeric(userProperties.getVerify().getSms().getLength());
		System.out.println(code);
		return new VerifyEntity(code, userProperties.getVerify().getSms().getExpireSeconds());
	}
}