package com.wy.verify.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.wy.common.Constants;
import com.wy.result.ResultException;
import com.wy.verify.AbstractVerify;
import com.wy.verify.VerifyEntity;

/**
 * 短信验证码处理器
 */
@Component
public class SmsVerifyHandler extends AbstractVerify<VerifyEntity> {

	@Autowired
	private SmsSend smsSend;

	@Override
	protected void handler(ServletWebRequest request, VerifyEntity validateCode) {
		String paramName = Constants.DEFAULT_PARAMETER_NAME_MOBILE;
		try {
			String mobile = ServletRequestUtils.getRequiredStringParameter(request.getRequest(), paramName);
			smsSend.sendSms(mobile, validateCode.getVerityCode());
		} catch (ServletRequestBindingException e) {
			e.printStackTrace();
			throw new ResultException("参数校验失败,请重试");
		}
	}
}