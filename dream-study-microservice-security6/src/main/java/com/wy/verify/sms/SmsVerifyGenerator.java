package com.wy.verify.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.ServletWebRequest;

import com.wy.properties.DreamSecurityProperties;
import com.wy.verify.VerifyEntity;
import com.wy.verify.VerifyGenerator;

import dream.flying.flower.helper.RandomHelper;

/**
 * 短信验证
 *
 * @author 飞花梦影
 * @date 2019-09-24 23:39:33
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class SmsVerifyGenerator implements VerifyGenerator {

	@Autowired
	private DreamSecurityProperties dreamSecurityProperties;

	@Override
	public VerifyEntity generateVerify(ServletWebRequest request) {
		// 模拟短信验证码
		String code = RandomHelper.randomNumeric(dreamSecurityProperties.getVerify().getSms().getLength());
		System.out.println(code);
		return new VerifyEntity(code, dreamSecurityProperties.getVerify().getSms().getExpireSeconds());
	}
}